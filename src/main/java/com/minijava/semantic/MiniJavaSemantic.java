package com.minijava.semantic;

import java.util.*;
import com.minijava.antlr.MiniJavaBaseVisitor;
import com.minijava.antlr.MiniJavaParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.TerminalNode;

public class MiniJavaSemantic extends MiniJavaBaseVisitor<Object> implements AutoCloseable {
    private final Map<String, Type> symbolTable = new HashMap<>();
    private final List<String> semanticErrors = new ArrayList<>();
    private final SemanticLogger logger = new SemanticLogger();

    public List<String> getSemanticErrors() { return semanticErrors; }

    public void printErrors() {
        if (!semanticErrors.isEmpty()) semanticErrors.forEach(System.out::println);
    }

    private void log(String msg) { logger.log("[SEMÂNTICO] " + msg); }

    private void error(Token token, String format, Object... args) {
        semanticErrors.add(String.format("[Erro Semântico] Linha %d: %s", token.getLine(), String.format(format, args)));
    }

    private void error(ParserRuleContext ctx, String format, Object... args) {
        error(ctx.getStart(), format, args);
    }

    private void error(TerminalNode node, String format, Object... args) {
        if (node != null) error(node.getSymbol(), format, args);
        else semanticErrors.add(String.format("[Erro Semântico] Linha ?: %s (Terminal nulo)", String.format(format, args)));
    }

    private Type asType(Object result) {
        return result instanceof Type ? (Type) result : UndefinedType.getInstance();
    }

    private Type safeVisit(ParserRuleContext ctx) {
        return asType(visit(ctx));
    }

    private Type getDeclaredType(String name, Token token) {
        if (!symbolTable.containsKey(name)) {
            error(token, "Variável '%s' não declarada.", name);
            return UndefinedType.getInstance();
        }
        return symbolTable.get(name);
    }

    private void declareVariable(String name, Type type, Token token) {
        if (symbolTable.containsKey(name)) {
            error(token, "Variável '%s' já foi declarada.", name);
        } else {
            symbolTable.put(name, type);
            log("Declarada: '" + name + "' como '" + type.getName() + "'");
        }
    }

    private Type checkBinaryOperation(Type left, Type right, String op, TerminalNode opNode, Type expected, String context) {
        if (left == UndefinedType.getInstance() || right == UndefinedType.getInstance()) return UndefinedType.getInstance();
        if (!expected.isEquivalentTo(left) || !expected.isEquivalentTo(right)) {
            error(opNode, "Operação '%s' em %s requer '%s', mas recebeu '%s' e '%s'",
                    op, context, expected.getName(), left.getName(), right.getName());
            return UndefinedType.getInstance();
        }
        return expected;
    }

    @Override
    public void close() {
        logger.close();
    }

    @Override
    public Object visitDeclaration(MiniJavaParser.DeclarationContext ctx) {
        String name = ctx.ID().getText();
        String typeText = ctx.getChild(0).getText();
        Type type = switch (typeText) {
            case "int" -> IntType.getInstance();
            case "string" -> StringType.getInstance();
            default -> {
                error(ctx, "Tipo desconhecido: '%s'", typeText);
                yield UndefinedType.getInstance();
            }
        };
        declareVariable(name, type, ctx.ID().getSymbol());
        return null;
    }

    @Override
    public Object visitAssignment(MiniJavaParser.AssignmentContext ctx) {
        String name = ctx.ID().getText();
        Type varType = getDeclaredType(name, ctx.ID().getSymbol());
        Type exprType = safeVisit(ctx.expression());

        if (!varType.isEquivalentTo(exprType) && exprType != UndefinedType.getInstance()) {
            error(ctx, "Atribuição incompatível: '%s' é '%s', expressão é '%s'", name, varType.getName(), exprType.getName());
        }
        log("Atribuição: '" + name + "' com '" + exprType.getName() + "'");
        return null;
    }

    @Override
    public Object visitFactor(MiniJavaParser.FactorContext ctx) {
        if (ctx.INT() != null) return IntType.getInstance();

        if (ctx.ID() != null) {
            String name = ctx.ID().getText();
            Type type = getDeclaredType(name, ctx.ID().getSymbol());
            log("Uso: '" + name + "' do tipo '" + type.getName() + "'");
            return type;
        }

        if (ctx.expression() != null) return safeVisit(ctx.expression());

        error(ctx, "Fator inválido.");
        return UndefinedType.getInstance();
    }

    @Override
    public Object visitTerm(MiniJavaParser.TermContext ctx) {
        Type left = safeVisit(ctx.factor(0));
        for (int i = 1; i < ctx.factor().size(); i++) {
            TerminalNode opNode = (TerminalNode) ctx.getChild(i * 2 - 1);
            String op = opNode.getText();
            Type right = safeVisit(ctx.factor(i));

            if ("/".equals(op) && ctx.factor(i).INT() != null && "0".equals(ctx.factor(i).INT().getText())) {
                error(opNode, "Divisão por zero literal.");
                return UndefinedType.getInstance();
            }

            log("Operação termo '" + op + "' entre '" + left.getName() + "' e '" + right.getName() + "'");
            left = checkBinaryOperation(left, right, op, opNode, IntType.getInstance(), "termo");
        }
        return left;
    }

    @Override
    public Object visitAdditiveExpression(MiniJavaParser.AdditiveExpressionContext ctx) {
        if (ctx.STRING() != null) return StringType.getInstance();
        if (ctx.term().isEmpty()) {
            error(ctx, "Expressão aditiva sem termos.");
            return UndefinedType.getInstance();
        }

        Type left = safeVisit(ctx.term(0));
        for (int i = 1; i < ctx.term().size(); i++) {
            TerminalNode opNode = (TerminalNode) ctx.getChild(i * 2 - 1);
            String op = opNode.getText();
            Type right = safeVisit(ctx.term(i));
            if ("+".equals(op)) {
                log("Adição: '" + left.getName() + "' + '" + right.getName() + "'");
            }  else if ("-".equals(op)) {
                log("Subtração: '" + left.getName() + "' - '" + right.getName() + "'");
            } else {
                error(opNode, "Operador desconhecido '%s' em expressão aditiva", op);
                return UndefinedType.getInstance();
            }
            left = checkBinaryOperation(left, right, op, opNode, IntType.getInstance(), "expressão aditiva");
        }
        return left;
    }

    @Override
    public Object visitConcatenation(MiniJavaParser.ConcatenationContext ctx) {
        Type current = safeVisit(ctx.additiveExpression(0));
        for (int i = 1; i < ctx.additiveExpression().size(); i++) {
            TerminalNode opNode = (TerminalNode) ctx.getChild(i * 2 - 1);
            Type right = safeVisit(ctx.additiveExpression(i));
            log("Concatenação '+' entre '" + current.getName() + "' e '" + right.getName() + "'");

            if (current instanceof StringType || right instanceof StringType)
                current = StringType.getInstance();
            else if (current instanceof IntType && right instanceof IntType)
                current = IntType.getInstance();
            else {
                error(opNode, "Concatenação '+' inválida entre '%s' e '%s'", current.getName(), right.getName());
                return UndefinedType.getInstance();
            }
        }
        return current;
    }

    @Override
    public Object visitComparison(MiniJavaParser.ComparisonContext ctx) {
        Type left = safeVisit(ctx.expression(0));
        Type right = safeVisit(ctx.expression(1));
        TerminalNode opNode = (TerminalNode) ctx.getChild(1);

        log("Comparação '" + opNode.getText() + "' entre '" + left.getName() + "' e '" + right.getName() + "'");

        boolean valid = (left instanceof IntType && right instanceof IntType)
                     || (left instanceof StringType && right instanceof StringType);

        if (!valid) {
            error(opNode, "Comparação inválida entre '%s' e '%s'", left.getName(), right.getName());
            return UndefinedType.getInstance();
        }
        return BooleanType.getInstance();
    }

    @Override
    public Object visitLogicalFactor(MiniJavaParser.LogicalFactorContext ctx) {
        boolean isNegated = ctx.getChildCount() == 2 && "!".equals(ctx.getChild(0).getText());
        Type baseType = safeVisit((ParserRuleContext) (isNegated ? ctx.getChild(1) : ctx.getChild(0)));

        if (isNegated && !(baseType instanceof BooleanType)) {
            error((TerminalNode) ctx.getChild(0), "Operador '!' requer booleano, recebeu '%s'", baseType.getName());
            return UndefinedType.getInstance();
        }
        return baseType;
    }

    @Override
    public Object visitLogicalExpression(MiniJavaParser.LogicalExpressionContext ctx) {
        Type current = safeVisit(ctx.logicalFactor(0));
        for (int i = 1; i < ctx.logicalFactor().size(); i++) {
            TerminalNode opNode = (TerminalNode) ctx.getChild(i * 2 - 1);
            Type right = safeVisit(ctx.logicalFactor(i));
            log("Operação lógica entre '" + current.getName() + "' e '" + right.getName() + "'");
            current = checkBinaryOperation(current, right, opNode.getText(), opNode, BooleanType.getInstance(), "expressão lógica");
        }
        return current;
    }
}

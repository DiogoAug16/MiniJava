package com.minijava.semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.minijava.antlr.MiniJavaBaseVisitor;
import com.minijava.antlr.MiniJavaParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

public class MiniJavaSemantic extends MiniJavaBaseVisitor<Object> implements AutoCloseable {
    private final Map<String, Type> symbolTable = new HashMap<>();
    private final List<String> semanticErrors = new ArrayList<>();
    private final SemanticLogger logger;

    public MiniJavaSemantic() {
        this.logger = new SemanticLogger();
    }

    public List<String> getSemanticErrors() {
        return semanticErrors;
    }

    private void reportError(Token token, String format, Object... args) {
        String msg = String.format(format, args);
        semanticErrors.add(String.format("[Erro Semântico] Linha %d: %s", token.getLine(), msg));
    }

    private void reportError(ParserRuleContext ctx, String format, Object... args) {
        reportError(ctx.getStart(), format, args);
    }

    private void reportError(TerminalNode node, String format, Object... args) {
        if (node != null) {
            reportError(node.getSymbol(), format, args);
        } else {
            String msg = String.format(format, args);
            semanticErrors.add(String.format("[Erro Semântico] Linha ?: %s (Nó Terminal nulo)", msg));
        }
    }

    public void printErrors() {
        if (semanticErrors.isEmpty()) {
            System.out.println("Análise semântica concluída com sucesso, sem erros.");
        } else {
            System.out.println("Erros encontrados na análise semântica:");
            semanticErrors.forEach(System.out::println);
        }
    }

    private void logAction(String msg) {
        if (logger != null) {
            logger.log("[LOG SEMÂNTICO] " + msg);
        }
    }

    @Override
    public void close() {
        if (this.logger != null) {
            this.logger.close();
        }
    }

    private Type asType(Object result) {
        if (result instanceof Type) {
            return (Type) result;
        }

        return UndefinedType.getInstance();
    }

    @Override
    public Object visitDeclaration(MiniJavaParser.DeclarationContext ctx) {
        String name = ctx.ID().getText();
        TerminalNode typeNode = (TerminalNode) ctx.getChild(0);
        String typeKeyword = ctx.getChild(0).getText();
        Type varType;

        switch (typeKeyword) {
            case "int":
                varType = IntType.getInstance();
                break;
            case "string":
                varType = StringType.getInstance();
                break;
            default:
                reportError(typeNode.getSymbol(), "Tipo de declaração desconhecido: '%s'", typeKeyword);
                varType = UndefinedType.getInstance();
        }

        if (symbolTable.containsKey(name)) {
            reportError(ctx.ID().getSymbol(), "Variável '%s' já foi declarada.", name);
        } else {
            symbolTable.put(name, varType);
            logAction("Variável '" + name + "' declarada como '" + varType.getName() + "'");
        }
        return null;
    }

    @Override
    public Object visitAssignment(MiniJavaParser.AssignmentContext ctx) {
        String id = ctx.ID().getText();
        if (!symbolTable.containsKey(id)) {
            reportError(ctx.ID().getSymbol(), "Variável '%s' não foi declarada antes do uso.", id);
            visit(ctx.expression());
        } else {
            Type varType = symbolTable.get(id);
            Type exprType = asType(visit(ctx.expression()));

            if (exprType != UndefinedType.getInstance() && !varType.isEquivalentTo(exprType)) {
                reportError(ctx, "Atribuição incompatível: variável '%s' é do tipo '%s' mas expressão é do tipo '%s'", id, varType.getName(), exprType.getName());
            }
            logAction("Atribuição à variável '" + id + "' do tipo " + varType.getName() + " com expressão do tipo " + exprType.getName());
        }
        return null;
    }

    @Override
    public Object visitFactor(MiniJavaParser.FactorContext ctx) {
        if (ctx.INT() != null) {
            return IntType.getInstance();
        }
        if (ctx.ID() != null) {
            String id = ctx.ID().getText();
            if (!symbolTable.containsKey(id)) {
                reportError(ctx.ID().getSymbol(), "Uso de variável não declarada: '%s'", id);
                return UndefinedType.getInstance();
            }
            logAction("Uso de variável '" + id + "' do tipo " + symbolTable.get(id).getName());
            return symbolTable.get(id);
        }
        if (ctx.expression() != null) {
            return visit(ctx.expression());
        }
        reportError(ctx, "Fator desconhecido ou inválido.");
        return UndefinedType.getInstance();
    }

    @Override
    public Object visitTerm(MiniJavaParser.TermContext ctx) {
        Type currentType = asType(visit(ctx.factor(0)));
        if (currentType == UndefinedType.getInstance()) return UndefinedType.getInstance();

        for (int i = 0; i < ctx.factor().size() - 1; i++) {
            TerminalNode opNode = (TerminalNode) ctx.getChild((i * 2) + 1);
            String operator = opNode.getText();
            MiniJavaParser.FactorContext rightFactorCtx = ctx.factor(i + 1);
            Type rightFactorType = asType(visit(rightFactorCtx));

            if (rightFactorType == UndefinedType.getInstance()) return UndefinedType.getInstance();
            
            logAction("Operação termo '" + operator + "' entre '" + currentType.getName() + "' e '" + rightFactorType.getName() + "'");

            if (operator.equals("/")) {
                if (rightFactorCtx.INT() != null && "0".equals(rightFactorCtx.INT().getText())) {
                    reportError(opNode, "Divisão por zero literal detectada.");
                    return UndefinedType.getInstance();
                }
            }

            if (!(currentType instanceof IntType) || !(rightFactorType instanceof IntType)) {
                reportError(opNode, "Operações '%s' requerem operandos do tipo '%s', mas recebeu '%s' e '%s'", operator, IntType.getInstance().getName(), currentType.getName(), rightFactorType.getName());
                return UndefinedType.getInstance();
            }
            currentType = IntType.getInstance();
        }
        return currentType;
    }
    
    @Override
    public Object visitAdditiveExpression(MiniJavaParser.AdditiveExpressionContext ctx) {
        if (ctx.STRING() != null) {
            logAction("Expressão aditiva é um literal STRING");
            return StringType.getInstance();
        }

        if (ctx.term().isEmpty()) {
            reportError(ctx, "Expressão aditiva inválida, falta termo.");
            return UndefinedType.getInstance();
        }

        Type currentType = asType(visit(ctx.term(0)));
        if (currentType == UndefinedType.getInstance()) return UndefinedType.getInstance();

        if (ctx.term().size() == 1 && ctx.getChildCount() == 1) {
             return currentType;
        }

        for (int i = 0; i < ctx.term().size() - 1; i++) {
            TerminalNode opNode = (TerminalNode) ctx.getChild((i * 2) + 1);
            String operator = opNode.getText();
            Type rightTermType = asType(visit(ctx.term(i + 1)));

            if (rightTermType == UndefinedType.getInstance()) return UndefinedType.getInstance();

            logAction("Operação aditiva '" + operator + "' entre '" + currentType.getName() + "' e '" + rightTermType.getName() + "'");
            
            if (!(currentType instanceof IntType) || !(rightTermType instanceof IntType)) {
                reportError(opNode, "Operação '%s' em expressão aditiva requer operandos do tipo '%s', mas recebeu '%s' e '%s'.", operator, IntType.getInstance().getName(), currentType.getName(), rightTermType.getName());
                return UndefinedType.getInstance();
            }
            currentType = IntType.getInstance();
        }
        return currentType;
    }

    @Override
    public Object visitConcatenation(MiniJavaParser.ConcatenationContext ctx) {
        Type currentType = asType(visit(ctx.additiveExpression(0)));
        if (currentType == UndefinedType.getInstance()) return UndefinedType.getInstance();

        if (ctx.additiveExpression().size() == 1) {
            return currentType;
        }

        for (int i = 0; i < ctx.additiveExpression().size() - 1; i++) {
            TerminalNode opNode = (TerminalNode) ctx.getChild((i * 2) + 1);

            Type otherType = asType(visit(ctx.additiveExpression(i + 1)));
            if (otherType == UndefinedType.getInstance()) return UndefinedType.getInstance();

            logAction("Operação de concatenação/adição '+' entre '" + currentType.getName() + "' e '" + otherType.getName() + "'");

            if (currentType instanceof StringType || otherType instanceof StringType) {
                currentType = StringType.getInstance();
            }
            else if (currentType instanceof IntType && otherType instanceof IntType) {
                currentType = IntType.getInstance();
            }
            else {
                reportError(opNode, "Operação '+' entre tipos incompatíveis para concatenação/adição: '%s' e '%s'. Esperado string ou ambos int.", currentType.getName(), otherType.getName());
                return UndefinedType.getInstance();
            }
        }
        return currentType;
    }

    @Override
    public Object visitComparison(MiniJavaParser.ComparisonContext ctx) {
        Type leftType = asType(visit(ctx.expression(0)));
        Type rightType = asType(visit(ctx.expression(1)));

        if (leftType == UndefinedType.getInstance() || rightType == UndefinedType.getInstance()) {
            return UndefinedType.getInstance();
        }

        TerminalNode opNode = (TerminalNode) ctx.getChild(1);
        logAction("Comparação '" + opNode.getText() + "' entre '" + leftType.getName() + "' e '" + rightType.getName() + "'");

        boolean compatible = false;
        if (leftType instanceof IntType && rightType instanceof IntType) {
            compatible = true;
        } else if (leftType instanceof StringType && rightType instanceof StringType) {
            compatible = true;
        }

        if (!compatible) {
             reportError(opNode, "Comparação entre tipos incompatíveis: '%s' e '%s'. Apenas int com int ou string com string.", leftType.getName(), rightType.getName());
             return UndefinedType.getInstance();
        }
        
        return BooleanType.getInstance();
    }

    @Override
    public Object visitLogicalFactor(MiniJavaParser.LogicalFactorContext ctx) {
        Type baseType;
        boolean isNegated = ctx.getChild(0) instanceof TerminalNode && "!".equals(ctx.getChild(0).getText());
        
        ParserRuleContext innerContext = ctx.comparison() != null ? ctx.comparison() : ctx.logicalExpression();

        if (innerContext != null) {
            baseType = asType(visit(isNegated ? ctx.getChild(1) : ctx.getChild(0)));
        } else {
            reportError(ctx, "Fator lógico inválido: esperado 'comparison' ou '(logicalExpression)'.");
            return UndefinedType.getInstance();
        }

        if (baseType == UndefinedType.getInstance()) return UndefinedType.getInstance();

        if (isNegated) {
            logAction("Operador de negação '!' aplicado a '" + baseType.getName() + "'");
            if (!(baseType instanceof BooleanType)) {
                reportError((TerminalNode)ctx.getChild(0), "Operador '!' requer um operando booleano, mas recebeu '%s'", baseType.getName());
                return UndefinedType.getInstance();
            }
            return BooleanType.getInstance(); 
        }
        return baseType; 
    }

    @Override
    public Object visitLogicalExpression(MiniJavaParser.LogicalExpressionContext ctx) {
        Type currentType = asType(visit(ctx.logicalFactor(0)));
        if (currentType == UndefinedType.getInstance()) return UndefinedType.getInstance();

        if (!(currentType instanceof BooleanType)) {
            reportError(ctx.logicalFactor(0), "Expressão lógica requer valores booleanos, mas o primeiro fator é '%s'", currentType.getName());
            return UndefinedType.getInstance();
        }

        for (int i = 0; i < ctx.logicalFactor().size() - 1; i++) {
            TerminalNode opNode = (TerminalNode) ctx.getChild((i * 2) + 1);
            logAction("Operador lógico '" + opNode.getText() + "' realizado"); 
            
            Type nextFactorType = asType(visit(ctx.logicalFactor(i + 1)));
            if (nextFactorType == UndefinedType.getInstance()) return UndefinedType.getInstance();

            if (!(nextFactorType instanceof BooleanType)) {
                reportError(ctx.logicalFactor(i + 1), "Operador lógico '%s' requer operandos booleanos, mas o operando direito é '%s'", opNode.getText(), nextFactorType.getName());
                return UndefinedType.getInstance();
            }
        }
        return BooleanType.getInstance();
    }
    
    @Override
    public Object visitCondition(MiniJavaParser.ConditionContext ctx) {

        if (ctx.logicalExpression() != null) {
            Type type = asType(visit(ctx.logicalExpression()));
            if (!(type instanceof BooleanType) && type != UndefinedType.getInstance()) {
                 reportError(ctx, "Condição baseada em expressão lógica deve ser do tipo booleano, mas foi '%s'.", type.getName());
                 return UndefinedType.getInstance();
            }
            return type;
        }

        if (ctx.expression() != null) {
            Type type = asType(visit(ctx.expression()));

            if (!(type instanceof BooleanType) && type != UndefinedType.getInstance()) {
                 reportError(ctx, "Condição não pode ser do tipo '%s'. Deve ser uma expressão booleana.", type.getName());
                 return UndefinedType.getInstance();
            }
            return type;
        }
        reportError(ctx, "Estrutura de condição inválida.");
        return UndefinedType.getInstance();
    }

    @Override
    public Object visitIfStatement(MiniJavaParser.IfStatementContext ctx) {
        logAction("Analisando if statement");
        visit(ctx.condition()); 
        
        visit(ctx.block(0));
        if (ctx.block().size() > 1 && ctx.block(1) != null) {
            logAction("Analisando else block do if statement");
            visit(ctx.block(1));
        }
        return null;
    }

    @Override
    public Object visitWhileStatement(MiniJavaParser.WhileStatementContext ctx) {
        logAction("Analisando while statement");
        visit(ctx.condition()); 
        visit(ctx.block());
        return null;
    }

    @Override
    public Object visitRead(MiniJavaParser.ReadContext ctx) {
        String id = ctx.ID().getText();
        if (!symbolTable.containsKey(id)) {
            reportError(ctx.ID().getSymbol(), "Variável '%s' não foi declarada antes do uso (scanf).", id);
        } else {
            Type varType = symbolTable.get(id);
            logAction("Leitura (scanf) para variável '" + id + "' do tipo '" + varType.getName() + "'");
        }
        return null;
    }

    @Override
    public Object visitWrite(MiniJavaParser.WriteContext ctx) {
        logAction("Analisando instrução de escrita (" + ctx.getChild(0).getText() + ")");
        Type exprType = asType(visit(ctx.expression())); 
        if (exprType != UndefinedType.getInstance()){
             logAction("Expressão em '" + ctx.getChild(0).getText() + "' é do tipo '" + exprType.getName() + "'");
        }
        return null;
    }
}
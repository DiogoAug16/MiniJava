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

public class MiniJavaSemantic extends MiniJavaBaseVisitor<String> implements AutoCloseable {
    private final Map<String, String> symbolTable = new HashMap<>();
    private final List<String> semanticErrors = new ArrayList<>();
    private final SemanticLogger logger;

    private static final String TYPE_INT = "int";
    private static final String TYPE_STRING = "string";
    private static final String TYPE_BOOLEAN = "boolean";
    private static final String TYPE_UNDEFINED = "undefined";

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
            System.out.println("Analise semântica concluída com sucesso, sem erros.");
        } else {
            System.out.println("Erros encontrados na análise semântica:");
            semanticErrors.forEach(System.out::println);
        }
    }

    private void logAction(String msg) {
        logger.log("[LOG SEMÂNTICO] " + msg);
    }

    @Override
    public void close() {
        if (this.logger != null) {
            this.logger.close();
        }
    }

    @Override
    public String visitDeclaration(MiniJavaParser.DeclarationContext ctx) {
        String name = ctx.ID().getText();
        String type = ctx.getChild(0).getText();
        if (symbolTable.containsKey(name)) {
            reportError(ctx.ID().getSymbol(), "Variável '%s' já foi declarada.", name);
        } else {
            symbolTable.put(name, type);
            logAction("Variável '" + name + "' declarada como '" + type + "'");
        }
        return null;
    }

    @Override
    public String visitAssignment(MiniJavaParser.AssignmentContext ctx) {
        String id = ctx.ID().getText();
        if (!symbolTable.containsKey(id)) {
            reportError(ctx.ID().getSymbol(), "Variável '%s' não foi declarada antes do uso.", id);
        } else {
            String varType = symbolTable.get(id);
            String exprType = visit(ctx.expression());
            if (!TYPE_UNDEFINED.equals(exprType) && !varType.equals(exprType)) {
                reportError(ctx, "Atribuição incompatível: variável '%s' é do tipo '%s' mas expressão é do tipo '%s'", id, varType, exprType);
            }
            logAction("Atribuição à variável '" + id + "'");
        }
        return null;
    }

    @Override
    public String visitFactor(MiniJavaParser.FactorContext ctx) {
        if (ctx.INT() != null) return TYPE_INT;
        if (ctx.ID() != null) {
            String id = ctx.ID().getText();
            if (!symbolTable.containsKey(id)) {
                reportError(ctx.ID().getSymbol(), "Uso de variável não declarada: '%s'", id);
                return TYPE_UNDEFINED;
            }
            logAction("Uso de variável '" + id + "'");
            return symbolTable.get(id);
        }
        if (ctx.expression() != null) return visit(ctx.expression());
        reportError(ctx, "Fator desconhecido ou inválido.");
        return TYPE_UNDEFINED;
    }

    @Override
    public String visitTerm(MiniJavaParser.TermContext ctx) {
        String currentType = visit(ctx.factor(0));
        if (TYPE_UNDEFINED.equals(currentType)) return TYPE_UNDEFINED;

        for (int i = 0; i < ctx.factor().size() - 1; i++) {
            TerminalNode opNode = (TerminalNode) ctx.getChild((i * 2) + 1);
            String operator = opNode.getText();
            MiniJavaParser.FactorContext rightFactorCtx = ctx.factor(i + 1);
            String rightFactorType = visit(rightFactorCtx);

            if (TYPE_UNDEFINED.equals(rightFactorType)) return TYPE_UNDEFINED;
            
            logAction("Operação termo '" + operator + "' realizada");

            if (operator.equals("/")) {
                if (rightFactorCtx.INT() != null && "0".equals(rightFactorCtx.INT().getText())) {
                    reportError(opNode, "Divisão por zero detectada.");
                    return TYPE_UNDEFINED;
                }
            }
            if (!TYPE_INT.equals(currentType) || !TYPE_INT.equals(rightFactorType)) {
                reportError(opNode, "Operações '%s' requerem operandos do tipo '%s', mas recebeu '%s' e '%s'", operator, TYPE_INT, currentType, rightFactorType);
                return TYPE_UNDEFINED;
            }
        }
        return currentType;
    }

    @Override
    public String visitAdditiveExpression(MiniJavaParser.AdditiveExpressionContext ctx) {
        if (ctx.STRING() != null) return TYPE_STRING; 

        String currentType = visit(ctx.term(0));
        if (TYPE_UNDEFINED.equals(currentType)) return TYPE_UNDEFINED;

        if (ctx.term().size() > 1) { 
            if (!TYPE_INT.equals(currentType)) {
                TerminalNode firstOpNode = (TerminalNode) ctx.getChild(1); 
                reportError(ctx.term(0), "Operação '%s' requer operando esquerdo do tipo '%s', mas recebeu '%s'", firstOpNode.getText(), TYPE_INT, currentType);
                return TYPE_UNDEFINED;
            }
            for (int i = 0; i < ctx.term().size() - 1; i++) {
                TerminalNode opNode = (TerminalNode) ctx.getChild((i * 2) + 1);
                String operator = opNode.getText();

                if ("+".equals(operator)) {
                    logAction("Operação de adição inteira '+' realizada");
                } else if ("-".equals(operator)) {
                    logAction("Operação de subtração inteira '-' realizada");
                }          

                String rightTermType = visit(ctx.term(i + 1));
                if (TYPE_UNDEFINED.equals(rightTermType)) return TYPE_UNDEFINED;
                if (!TYPE_INT.equals(rightTermType)) {
                    reportError(ctx.term(i + 1), "Operação '%s' requer operando direito do tipo '%s', mas recebeu '%s'", opNode.getText(), TYPE_INT, rightTermType);
                    return TYPE_UNDEFINED;
                }
            }
        }
        return currentType; 
    }

    @Override
    public String visitConcatenation(MiniJavaParser.ConcatenationContext ctx) {
        String currentType = visit(ctx.additiveExpression(0));
        if (TYPE_UNDEFINED.equals(currentType)) return TYPE_UNDEFINED;

        for (int i = 0; i < ctx.additiveExpression().size() - 1; i++) {
            TerminalNode opNode = (TerminalNode) ctx.getChild((i * 2) + 1);
            String operator = opNode.getText();

            String otherType = visit(ctx.additiveExpression(i + 1));
            if (TYPE_UNDEFINED.equals(otherType)) return TYPE_UNDEFINED;

            if (TYPE_STRING.equals(currentType) || TYPE_STRING.equals(otherType)) {
                logAction("Operação de concatenação de string '" + operator + "' realizada");
                currentType = TYPE_STRING;
            } else if (TYPE_INT.equals(currentType) && TYPE_INT.equals(otherType)) {
                logAction("Operação de adição de inteiros '" + operator + "' realizada");
                currentType = TYPE_INT; 
            } else {
                reportError(opNode, "Operação '+' entre tipos incompatíveis para concatenação/adição: '%s' e '%s'. Esperado string ou ambos int.", currentType, otherType);
                return TYPE_UNDEFINED;
            }
        }
        return currentType;
    }

    @Override
    public String visitComparison(MiniJavaParser.ComparisonContext ctx) {
        String leftType = visit(ctx.expression(0));
        String rightType = visit(ctx.expression(1));

        if (TYPE_UNDEFINED.equals(leftType) || TYPE_UNDEFINED.equals(rightType)) return TYPE_UNDEFINED;

        TerminalNode opNode = (TerminalNode) ctx.getChild(1);
        if (!leftType.equals(rightType)) {
            reportError(opNode, "Comparação entre tipos incompatíveis: '%s' e '%s'", leftType, rightType);
            return TYPE_UNDEFINED;
        }
        logAction("Comparação realizada com operador '" + opNode.getText() + "'");
        return TYPE_BOOLEAN;
    }

    @Override
    public String visitLogicalFactor(MiniJavaParser.LogicalFactorContext ctx) {
        String baseType;
        boolean isNegated = ctx.getChild(0) instanceof TerminalNode && "!".equals(ctx.getChild(0).getText());
        
        if (ctx.comparison() != null) { 
            baseType = visit(ctx.comparison());
        } else if (ctx.logicalExpression() != null) { 
            baseType = visit(ctx.logicalExpression());
        } else {
            reportError(ctx, "Fator lógico inválido: esperado 'comparison' ou '(logicalExpression)'.");
            return TYPE_UNDEFINED;
        }

        if (TYPE_UNDEFINED.equals(baseType)) return TYPE_UNDEFINED;

        if (isNegated) {
            logAction("Operador de negação '!' aplicado");
            if (!TYPE_BOOLEAN.equals(baseType)) {
                reportError((TerminalNode)ctx.getChild(0), "Operador '!' requer um operando booleano, mas recebeu '%s'", baseType);
                return TYPE_UNDEFINED;
            }
            return TYPE_BOOLEAN; 
        }
        return baseType; 
    }

    @Override
    public String visitLogicalExpression(MiniJavaParser.LogicalExpressionContext ctx) {
        String currentType = visit(ctx.logicalFactor(0));
        if (TYPE_UNDEFINED.equals(currentType)) return TYPE_UNDEFINED;

        if (!TYPE_BOOLEAN.equals(currentType)) {
            reportError(ctx.logicalFactor(0), "Expressão lógica requer valores booleanos, mas o primeiro fator é '%s'", currentType);
            return TYPE_UNDEFINED;
        }

        for (int i = 0; i < ctx.logicalFactor().size() - 1; i++) {
            TerminalNode opNode = (TerminalNode) ctx.getChild((i * 2) + 1);
            logAction("Operador lógico '" + opNode.getText() + "' realizado"); 
            
            String nextFactorType = visit(ctx.logicalFactor(i + 1));
            if (TYPE_UNDEFINED.equals(nextFactorType)) return TYPE_UNDEFINED;
            if (!TYPE_BOOLEAN.equals(nextFactorType)) {
                reportError(ctx.logicalFactor(i + 1), "Operador lógico '%s' requer operandos booleanos, mas o operando direito é '%s'", opNode.getText(), nextFactorType);
                return TYPE_UNDEFINED;
            }
        }
        return TYPE_BOOLEAN;
    }

    private void checkConditionIsBoolean(ParserRuleContext conditionCtx, String conditionType, String constructName) {
        if (TYPE_UNDEFINED.equals(conditionType)) return; 
        if (!TYPE_BOOLEAN.equals(conditionType)) {
            reportError(conditionCtx, "A condição do '%s' deve ser do tipo '%s', mas é '%s'", constructName, TYPE_BOOLEAN, conditionType);
        }
    }

    @Override
    public String visitIfStatement(MiniJavaParser.IfStatementContext ctx) {
        String conditionType = visit(ctx.condition()); 
        checkConditionIsBoolean(ctx.condition(), conditionType, "if");
        
        visit(ctx.block(0));
        if (ctx.block().size() > 1 && ctx.block(1) != null) {
            visit(ctx.block(1));
        }
        return null;
    }

    @Override
    public String visitWhileStatement(MiniJavaParser.WhileStatementContext ctx) {
        String conditionType = visit(ctx.condition()); 
        checkConditionIsBoolean(ctx.condition(), conditionType, "while");
        visit(ctx.block());
        return null;
    }

    @Override
    public String visitRead(MiniJavaParser.ReadContext ctx) {
        String id = ctx.ID().getText();
        if (!symbolTable.containsKey(id)) {
            reportError(ctx.ID().getSymbol(), "Variável '%s' não foi declarada antes do uso (scanf).", id);
        } else {
            logAction("Leitura de variável '" + id + "'");
        }
        return null;
    }

}
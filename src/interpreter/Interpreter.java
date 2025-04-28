package interpreter;
import java.util.HashMap;
import java.util.Map;

import antlr.GrammarBaseVisitor;
import antlr.GrammarParser;

public class Interpreter extends GrammarBaseVisitor<Object> {

    private Map<String, Object> memory = new HashMap<>();

    @Override
    public Object visitProgram(GrammarParser.ProgramContext ctx) {
        visitDeclarations(ctx.declarations());
        visitStatements(ctx.statements());
        return null;
    }

    @Override
    public Object visitDeclarations(GrammarParser.DeclarationsContext ctx) {
        for (GrammarParser.DeclarationContext decl : ctx.declaration()) {
            visit(decl);
        }
        return null;
    }

    @Override
    public Object visitDeclaration(GrammarParser.DeclarationContext ctx) {
        String varName = ctx.ID().getText();
        if (ctx.getText().startsWith("int")) {
            memory.put(varName, 0);
        } else {
            memory.put(varName, "");
        }
        return null;
    }

    @Override
    public Object visitAssignment(GrammarParser.AssignmentContext ctx) {
        String varName = ctx.ID().getText();
        Object value = visit(ctx.expression());
        memory.put(varName, value);
        return null;
    }

    @Override
    public Object visitWrite(GrammarParser.WriteContext ctx) {
        Object value = visit(ctx.expression());

        if (ctx.getChild(0).getText().equals("write")) {
            System.out.print(value + " ");
        } else {
            System.out.println(value);
        }
        return null;
    }


    @Override
    public Object visitIfStatement(GrammarParser.IfStatementContext ctx) {
        Boolean condition = (Boolean) visit(ctx.logicalExpression());
        if (condition) {
            visit(ctx.statements(0));
        } else if (ctx.statements().size() > 1) {
            visit(ctx.statements(1));
        }
        return null;
    }

    @Override
    public Object visitWhileStatement(GrammarParser.WhileStatementContext ctx) {
        while ((Boolean) visit(ctx.logicalExpression())) {
            visit(ctx.statements());
        }
        return null;
    }

    @Override
    public Object visitExpression(GrammarParser.ExpressionContext ctx) {
        if (ctx.STRING() != null) {
            // String entre aspas
            return ctx.STRING().getText().replace("\"", "");
        }
        
        Object result = visit(ctx.term(0));
        for (int i = 1; i < ctx.term().size(); i++) {
            String op = ctx.getChild(2 * i - 1).getText();
            Object nextTerm = visit(ctx.term(i));
            if (op.equals("+")) {
                result = ((Number) result).doubleValue() + ((Number) nextTerm).doubleValue();
            } else if (op.equals("-")) {
                result = ((Number) result).doubleValue() - ((Number) nextTerm).doubleValue();
            }
        }
        return result;
    }

    @Override
    public Object visitTerm(GrammarParser.TermContext ctx) {
        Object result = visit(ctx.factor(0));
        for (int i = 1; i < ctx.factor().size(); i++) {
            String op = ctx.getChild(2 * i - 1).getText();
            Object nextFactor = visit(ctx.factor(i));
            if (op.equals("*")) {
                result = ((Number) result).doubleValue() * ((Number) nextFactor).doubleValue();
            } else if (op.equals("/")) {
                result = ((Number) result).doubleValue() / ((Number) nextFactor).doubleValue();
            }
        }
        return result;
    }

    @Override
    public Object visitFactor(GrammarParser.FactorContext ctx) {
        if (ctx.INT() != null) {
            return Integer.parseInt(ctx.INT().getText());
        } else if (ctx.ID() != null) {
            return memory.get(ctx.ID().getText());
        } else {
            return visit(ctx.expression());
        }
    }

    @Override
    public Object visitLogicalExpression(GrammarParser.LogicalExpressionContext ctx) {
        Object result = visit(ctx.logicalFactor(0));
        for (int i = 1; i < ctx.logicalFactor().size(); i++) {
            String op = ctx.getChild(2 * i - 1).getText();
            Object next = visit(ctx.logicalFactor(i));
            if (op.equals("&&")) {
                result = (Boolean) result && (Boolean) next;
            } else if (op.equals("||")) {
                result = (Boolean) result || (Boolean) next;
            }
        }
        return result;
    }

    @Override
    public Object visitLogicalFactor(GrammarParser.LogicalFactorContext ctx) {
        if (ctx.getChildCount() == 2) { // ! (negacao)
            return !(Boolean) visit(ctx.getChild(1));
        } else {
            return visit(ctx.getChild(0));
        }
    }

    @Override
    public Object visitComparison(GrammarParser.ComparisonContext ctx) {
        Double left = ((Number) visit(ctx.expression(0))).doubleValue();
        Double right = ((Number) visit(ctx.expression(1))).doubleValue();
        String op = ctx.getChild(1).getText();

        switch (op) {
            case "==":
                return left.equals(right);
            case ">":
                return left > right;
            case "<":
                return left < right;
            case ">=":
                return left >= right;
            case "<=":
                return left <= right;
            default:
                return false;
        }
    }
}
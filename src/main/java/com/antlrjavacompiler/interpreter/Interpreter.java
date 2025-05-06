package com.antlrjavacompiler.interpreter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.antlrjavacompiler.antlr.GrammarBaseVisitor;
import com.antlrjavacompiler.antlr.GrammarParser;

public class Interpreter extends GrammarBaseVisitor<Object> {

    private final Scanner scanner = new Scanner(System.in);
    private Map<String, Object> memory = new HashMap<>();

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

        if (ctx.getChild(0).getText().equals("print")) {
            System.out.print(value + " ");
        } else {
            System.out.println(value);
        }
        return null;
    }

    @Override
    public Object visitRead(GrammarParser.ReadContext ctx) {
        String varName = ctx.ID().getText();
        String input = scanner.nextLine();
        
        if (memory.get(varName) instanceof Integer) {
            memory.put(varName, Integer.parseInt(input));
        } else {
            memory.put(varName, input);
        }
        return null;
    }

    @Override
    public Object visitIfStatement(GrammarParser.IfStatementContext ctx) {
        Boolean condition = (Boolean) visit(ctx.logicalExpression());
        if (condition) {
            visit(ctx.block(0));
        } else if (ctx.block().size() > 1) {
            visit(ctx.block(1));
        }
        return null;
    }
    
    @Override
    public Object visitWhileStatement(GrammarParser.WhileStatementContext ctx) {
        while ((Boolean) visit(ctx.logicalExpression())) {
            visit(ctx.block());
        }
        return null;
    }
    
    @Override
    public Object visitConcatenation(GrammarParser.ConcatenationContext ctx) {
        Object result = visit(ctx.additiveExpression(0));
        for (int i = 1; i < ctx.additiveExpression().size(); i++) {
            Object next = visit(ctx.additiveExpression(i));
            result = String.valueOf(result) + String.valueOf(next);
        }
        return result;
    }    

    @Override
    public Object visitAdditiveExpression(GrammarParser.AdditiveExpressionContext ctx) {
        
        if (ctx.STRING() != null) {
            return ctx.STRING().getText().replace("\"", "");
        }
    
        Object result = visit(ctx.term(0));
        for (int i = 1; i < ctx.term().size(); i++) {
            String op = ctx.getChild(2 * i - 1).getText();
            Object next = visit(ctx.term(i));
    
            if (op.equals("+")) {
                result = ((Number) result).intValue() + ((Number) next).intValue();
            } else if (op.equals("-")) {
                result = ((Number) result).intValue() - ((Number) next).intValue();
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
                result = ((Number) result).intValue() * ((Number) nextFactor).intValue();
            } else if (op.equals("/")) {
                result = ((Number) result).intValue() / ((Number) nextFactor).intValue();
            }
        }
        return result;
    }

    @Override
    public Object visitFactor(GrammarParser.FactorContext ctx) {
        if (ctx.INT() != null) {
            return Integer.parseInt(ctx.INT().getText());
        } else if (ctx.ID() != null) {
            String varName = ctx.ID().getText();
            Object value = memory.get(varName);
            
            if (value == null) {
                throw new RuntimeException("Variável '" + varName + "' não foi inicializada.");
            }
            
            return value;
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
        if (ctx.getChildCount() == 2) {
            return !(Boolean) visit(ctx.getChild(1));
        } else {
            return visit(ctx.getChild(0));
        }
    }

    @Override
    public Object visitComparison(GrammarParser.ComparisonContext ctx) {
        Integer left = ((Number) visit(ctx.expression(0))).intValue();
        Integer right = ((Number) visit(ctx.expression(1))).intValue();
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
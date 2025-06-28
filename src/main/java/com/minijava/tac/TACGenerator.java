package com.minijava.tac;

import java.util.*;
import com.minijava.antlr.MiniJavaBaseVisitor;
import com.minijava.antlr.MiniJavaParser;

public class TACGenerator extends MiniJavaBaseVisitor<TACOperand> {
    private List<TACInstruction> instructions = new ArrayList<>();
    private int tempCount = 0;
    private int labelCount = 0;

    private TACOperand newTemp() {
        return TACOperand.temporary(tempCount++);
    }

    private TACOperand newLabel() {
        return TACOperand.label(labelCount++);
    }

    public List<TACInstruction> getInstructions() {
        return instructions;
    }

    @Override
    public TACOperand visitConcatenation(MiniJavaParser.ConcatenationContext ctx) {
        List<TACOperand> args = new ArrayList<>();
        TACOperand temp = newTemp();

        for (var additive : ctx.additiveExpression()) {
            args.add(visit(additive));
        }

        if (args.size() == 1) {
            return args.get(0);
        }
        
        instructions.add(new TACInstruction("concat", temp, args));
        return temp;
    }
    
    @Override
    public TACOperand visitAdditiveExpression(MiniJavaParser.AdditiveExpressionContext ctx) {
        if (ctx.STRING() != null) {
            return TACOperand.literal(ctx.STRING().getText(), TACOperand.LiteralType.STRING);
        }
    
        TACOperand left = visit(ctx.term(0));
        for (int i = 1; i < ctx.term().size(); i++) {
            TACOperand right = visit(ctx.term(i));
            String op = ctx.getChild(2 * i - 1).getText();
            TACOperand temp = newTemp();
            instructions.add(new TACInstruction(op, temp, left, right));
            left = temp;
        }
        return left;
    }

    @Override
    public TACOperand visitTerm(MiniJavaParser.TermContext ctx) {
        TACOperand left = visit(ctx.factor(0));
        for (int i = 1; i < ctx.factor().size(); i++) {
            TACOperand right = visit(ctx.factor(i));
            String op = ctx.getChild(2 * i - 1).getText();
            TACOperand temp = newTemp();
            instructions.add(new TACInstruction(op, temp, left, right));
            left = temp;
        }
        return left;
    }

    @Override
    public TACOperand visitFactor(MiniJavaParser.FactorContext ctx) {
        if (ctx.INT() != null) {
            return TACOperand.literal(ctx.INT().getText(), TACOperand.LiteralType.INT);
        } else if (ctx.ID() != null) {
            return TACOperand.identifier(ctx.ID().getText());
        } else {
            return visit(ctx.expression());
        }
    }

    @Override
    public TACOperand visitCondition(MiniJavaParser.ConditionContext ctx) {
        if (ctx.logicalExpression() != null) {
            return visit(ctx.logicalExpression());
        }
        return visit(ctx.expression());
    }

    private void visitCondition(MiniJavaParser.ConditionContext ctx, TACOperand trueLabel, TACOperand falseLabel) {
        if (ctx.logicalExpression() != null) {
            visitLogicalExpression(ctx.logicalExpression(), trueLabel, falseLabel);
        } else {
            TACOperand res = visit(ctx.expression());
            instructions.add(new TACInstruction("!=", falseLabel, res, TACOperand.literal("0", TACOperand.LiteralType.INT)));
            instructions.add(new TACInstruction("goto", trueLabel, null, null));
        }
    }

    @Override
    public TACOperand visitLogicalExpression(MiniJavaParser.LogicalExpressionContext ctx) {
        TACOperand left = visit(ctx.logicalFactor(0));
        for (int i = 1; i < ctx.logicalFactor().size(); i++) {
            TACOperand right = visit(ctx.logicalFactor(i));
            String op = ctx.getChild(2 * i - 1).getText();
            TACOperand temp = newTemp();
            instructions.add(new TACInstruction(op, temp, left, right));
            left = temp;
        }
        return left;
    }

    private void visitLogicalExpression(MiniJavaParser.LogicalExpressionContext ctx, TACOperand trueLabel, TACOperand falseLabel) {
        if (ctx.logicalFactor().size() == 1) {
            visitLogicalFactor(ctx.logicalFactor(0), trueLabel, falseLabel);
        } else {
            String op = ctx.getChild(1).getText();
            if (op.equals("&&")) {
                List<MiniJavaParser.LogicalFactorContext> factors = ctx.logicalFactor();
                for (int i = 0; i < factors.size() - 1; i++) {
                    TACOperand nextLabel = newLabel();
                    visitLogicalFactor(factors.get(i), nextLabel, falseLabel);
                    instructions.add(new TACInstruction(nextLabel.toString() + ":", null, null, null));
                }
                visitLogicalFactor(factors.get(factors.size() - 1), trueLabel, falseLabel);
            } else if (op.equals("||")) {
                List<MiniJavaParser.LogicalFactorContext> factors = ctx.logicalFactor();
                for (int i = 0; i < factors.size() - 1; i++) {
                    TACOperand nextLabel = newLabel();
                    visitLogicalFactor(factors.get(i), trueLabel, nextLabel);
                    instructions.add(new TACInstruction(nextLabel.toString() + ":", null, null, null));
                }
                visitLogicalFactor(factors.get(factors.size() - 1), trueLabel, falseLabel);
            }
        }
    }

    @Override
    public TACOperand visitLogicalFactor(MiniJavaParser.LogicalFactorContext ctx) {
        TACOperand expr;
        if (ctx.comparison() != null) {
            expr = visit(ctx.comparison());
        } else {
            expr = visit(ctx.logicalExpression());
        }

        if (ctx.getText().startsWith("!")) {
            TACOperand temp = newTemp();
            instructions.add(new TACInstruction("!", temp, expr, null));
            return temp;
        }

        return expr;
    }

    private void visitLogicalFactor(MiniJavaParser.LogicalFactorContext ctx, TACOperand trueLabel, TACOperand falseLabel) {
        if (ctx.getText().startsWith("!")) {
            visitLogicalExpression(ctx.logicalExpression(), falseLabel, trueLabel);
        } else if (ctx.comparison() != null) {
            visitComparison(ctx.comparison(), trueLabel, falseLabel);
        } else {
            visitLogicalExpression(ctx.logicalExpression(), trueLabel, falseLabel);
        }
    }

    @Override
    public TACOperand visitComparison(MiniJavaParser.ComparisonContext ctx) {
        TACOperand left = visit(ctx.expression(0));
        TACOperand right = visit(ctx.expression(1));
        TACOperand temp = newTemp();
        instructions.add(new TACInstruction(ctx.getChild(1).getText(), temp, left, right));
        return temp;
    }

    private void visitComparison(MiniJavaParser.ComparisonContext ctx, TACOperand trueLabel, TACOperand falseLabel) {
        TACOperand left = visit(ctx.expression(0));
        TACOperand right = visit(ctx.expression(1));
        String op = ctx.getChild(1).getText();
        instructions.add(new TACInstruction(op, trueLabel, left, right));
        instructions.add(new TACInstruction("goto", falseLabel, null, null));
    }

    @Override
    public TACOperand visitAssignment(MiniJavaParser.AssignmentContext ctx) {
        TACOperand id = new TACOperand(TACOperand.Type.IDENTIFIER, ctx.ID().getText());
        TACOperand value = visit(ctx.expression());
        instructions.add(new TACInstruction("COPY", id, value, null));
        return null;
    }

    @Override
    public TACOperand visitRead(MiniJavaParser.ReadContext ctx) {
        TACOperand id = new TACOperand(TACOperand.Type.IDENTIFIER, ctx.ID().getText());
        instructions.add(new TACInstruction("read", id, null, null));
        return null;
    }

    @Override
    public TACOperand visitWrite(MiniJavaParser.WriteContext ctx) {
        TACOperand value = visit(ctx.expression());
        String func = ctx.getStart().getText();
        instructions.add(new TACInstruction(func, value, null, null));
        return null;
    }

    @Override
    public TACOperand visitIfStatement(MiniJavaParser.IfStatementContext ctx) {
        TACOperand trueLabel = newLabel();
        TACOperand falseLabel = newLabel();
        TACOperand endLabel = (ctx.block().size() > 1) ? newLabel() : falseLabel;

        visitCondition(ctx.condition(), trueLabel, falseLabel);

        instructions.add(new TACInstruction(trueLabel.toString() + ":", null, null, null));
        visit(ctx.block(0));

        if (ctx.block().size() > 1) {
            instructions.add(new TACInstruction("goto", endLabel, null, null));
            instructions.add(new TACInstruction(falseLabel.toString() + ":", null, null, null));
            visit(ctx.block(1));
            instructions.add(new TACInstruction("goto", endLabel, null, null));
            instructions.add(new TACInstruction(endLabel.toString() + ":", null, null, null));
        } else {
            instructions.add(new TACInstruction(falseLabel.toString() + ":", null, null, null));
        }

        return null;
    }

    @Override
    public TACOperand visitWhileStatement(MiniJavaParser.WhileStatementContext ctx) {
        TACOperand beginLabel = newLabel();
        TACOperand trueLabel = newLabel();
        TACOperand endLabel = newLabel();

        instructions.add(new TACInstruction(beginLabel.toString() + ":", null, null, null));
        visitCondition(ctx.condition(), trueLabel, endLabel);

        instructions.add(new TACInstruction(trueLabel.toString() + ":", null, null, null));
        visit(ctx.block());
        instructions.add(new TACInstruction("goto", beginLabel, null, null));
        instructions.add(new TACInstruction(endLabel.toString() + ":", null, null, null));

        return null;
    }
}

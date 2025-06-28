// Generated from src/main/java/com/minijava/antlr/MiniJava.g4 by ANTLR 4.13.1

package com.minijava.antlr;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MiniJavaParser}.
 */
public interface MiniJavaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MiniJavaParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MiniJavaParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#declarations}.
	 * @param ctx the parse tree
	 */
	void enterDeclarations(MiniJavaParser.DeclarationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#declarations}.
	 * @param ctx the parse tree
	 */
	void exitDeclarations(MiniJavaParser.DeclarationsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(MiniJavaParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(MiniJavaParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#statements}.
	 * @param ctx the parse tree
	 */
	void enterStatements(MiniJavaParser.StatementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#statements}.
	 * @param ctx the parse tree
	 */
	void exitStatements(MiniJavaParser.StatementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(MiniJavaParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(MiniJavaParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(MiniJavaParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(MiniJavaParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#read}.
	 * @param ctx the parse tree
	 */
	void enterRead(MiniJavaParser.ReadContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#read}.
	 * @param ctx the parse tree
	 */
	void exitRead(MiniJavaParser.ReadContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#write}.
	 * @param ctx the parse tree
	 */
	void enterWrite(MiniJavaParser.WriteContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#write}.
	 * @param ctx the parse tree
	 */
	void exitWrite(MiniJavaParser.WriteContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(MiniJavaParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(MiniJavaParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(MiniJavaParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(MiniJavaParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(MiniJavaParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(MiniJavaParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(MiniJavaParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(MiniJavaParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#concatenation}.
	 * @param ctx the parse tree
	 */
	void enterConcatenation(MiniJavaParser.ConcatenationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#concatenation}.
	 * @param ctx the parse tree
	 */
	void exitConcatenation(MiniJavaParser.ConcatenationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression(MiniJavaParser.AdditiveExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression(MiniJavaParser.AdditiveExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(MiniJavaParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(MiniJavaParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFactor(MiniJavaParser.FactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFactor(MiniJavaParser.FactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#logicalExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalExpression(MiniJavaParser.LogicalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#logicalExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalExpression(MiniJavaParser.LogicalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#logicalFactor}.
	 * @param ctx the parse tree
	 */
	void enterLogicalFactor(MiniJavaParser.LogicalFactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#logicalFactor}.
	 * @param ctx the parse tree
	 */
	void exitLogicalFactor(MiniJavaParser.LogicalFactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(MiniJavaParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(MiniJavaParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#comparison}.
	 * @param ctx the parse tree
	 */
	void enterComparison(MiniJavaParser.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#comparison}.
	 * @param ctx the parse tree
	 */
	void exitComparison(MiniJavaParser.ComparisonContext ctx);
}
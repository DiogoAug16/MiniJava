// Generated from src/grammar/Grammar.g4 by ANTLR 4.13.1

package antlr;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link GrammarParser}.
 */
public interface GrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link GrammarParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(GrammarParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(GrammarParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#declarations}.
	 * @param ctx the parse tree
	 */
	void enterDeclarations(GrammarParser.DeclarationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#declarations}.
	 * @param ctx the parse tree
	 */
	void exitDeclarations(GrammarParser.DeclarationsContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(GrammarParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(GrammarParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#statements}.
	 * @param ctx the parse tree
	 */
	void enterStatements(GrammarParser.StatementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#statements}.
	 * @param ctx the parse tree
	 */
	void exitStatements(GrammarParser.StatementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(GrammarParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(GrammarParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(GrammarParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(GrammarParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#read}.
	 * @param ctx the parse tree
	 */
	void enterRead(GrammarParser.ReadContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#read}.
	 * @param ctx the parse tree
	 */
	void exitRead(GrammarParser.ReadContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#write}.
	 * @param ctx the parse tree
	 */
	void enterWrite(GrammarParser.WriteContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#write}.
	 * @param ctx the parse tree
	 */
	void exitWrite(GrammarParser.WriteContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(GrammarParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(GrammarParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(GrammarParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(GrammarParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(GrammarParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(GrammarParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(GrammarParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(GrammarParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(GrammarParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(GrammarParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFactor(GrammarParser.FactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFactor(GrammarParser.FactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#logicalExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalExpression(GrammarParser.LogicalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#logicalExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalExpression(GrammarParser.LogicalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#logicalFactor}.
	 * @param ctx the parse tree
	 */
	void enterLogicalFactor(GrammarParser.LogicalFactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#logicalFactor}.
	 * @param ctx the parse tree
	 */
	void exitLogicalFactor(GrammarParser.LogicalFactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#comparison}.
	 * @param ctx the parse tree
	 */
	void enterComparison(GrammarParser.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#comparison}.
	 * @param ctx the parse tree
	 */
	void exitComparison(GrammarParser.ComparisonContext ctx);
}
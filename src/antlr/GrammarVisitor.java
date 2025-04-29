// Generated from src/grammar/Grammar.g4 by ANTLR 4.13.1

package antlr;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link GrammarParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface GrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link GrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(GrammarParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#declarations}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclarations(GrammarParser.DeclarationsContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(GrammarParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#statements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatements(GrammarParser.StatementsContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(GrammarParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(GrammarParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#read}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRead(GrammarParser.ReadContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#write}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWrite(GrammarParser.WriteContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(GrammarParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#whileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(GrammarParser.WhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(GrammarParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(GrammarParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(GrammarParser.TermContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactor(GrammarParser.FactorContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#logicalExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalExpression(GrammarParser.LogicalExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#logicalFactor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalFactor(GrammarParser.LogicalFactorContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#comparison}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison(GrammarParser.ComparisonContext ctx);
}
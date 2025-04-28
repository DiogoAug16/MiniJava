import org.antlr.v4.runtime.*;

import antlr.GrammarLexer;
import antlr.GrammarParser;
import exception.CustomErrorListener;
import interpreter.Interpreter;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            GrammarLexer lexer = new GrammarLexer(CharStreams.fromFileName("input/classificacao_triangulo.txt"));

            lexer.removeErrorListeners();
            lexer.addErrorListener(new CustomErrorListener());

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            GrammarParser parser = new GrammarParser(tokens);

            parser.removeErrorListeners();
            parser.addErrorListener(new CustomErrorListener());

            tokens.fill();

            for (Token token : tokens.getTokens()) {
                String tokenType = lexer.getVocabulary().getDisplayName(token.getType());
                String lexema = token.getText();
                int linha = token.getLine();
                int coluna = token.getCharPositionInLine() + 1;

                System.out.println("<" + tokenType + ", " + lexema + ", " + linha + ", " + coluna + ">");
            }

            GrammarParser.ProgramContext tree = parser.program();

            System.out.println("Parsing completed successfully");

            Interpreter interpreter = new Interpreter();
            interpreter.visit(tree);

        } catch (IOException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Parsing error: " + e.getMessage());
        }
    }
}

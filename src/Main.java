import org.antlr.v4.runtime.*;

import antlr.GrammarLexer;
import antlr.GrammarParser;
import exception.LexerErrorListener;
import exception.ParserErrorListener;
import interpreter.Interpreter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            // Criação do lexer e parser
            GrammarLexer lexer = new GrammarLexer(CharStreams.fromFileName("input/teste_erro_sintatico.txt"));

            lexer.removeErrorListeners();
            lexer.addErrorListener(new LexerErrorListener());

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            GrammarParser parser = new GrammarParser(tokens);

            parser.removeErrorListeners();
            parser.addErrorListener(new ParserErrorListener());

            // Parsing do código
            GrammarParser.ProgramContext tree = parser.program();

            // Se chegou até aqui, quer dizer que o parsing foi bem-sucedido
            System.out.println("Parsing completed successfully");

            // Interpretador para executar o código
            Interpreter interpreter = new Interpreter();
            interpreter.visit(tree);

            // Criação da pasta "tokens" dentro de "output" caso não exista
            File outputDir = new File("output/tokens");
            if (!outputDir.exists()) {
                outputDir.mkdirs();  // Cria a pasta "tokens" se não existir
            }

            // Criação do arquivo de saída para os tokens
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output/tokens/tokens.txt"))) {
                tokens.fill();  // Preenche os tokens

                // Itera sobre os tokens e escreve no arquivo
                for (Token token : tokens.getTokens()) {
                    String tokenType = lexer.getVocabulary().getDisplayName(token.getType());
                    String lexema = token.getText();
                    int linha = token.getLine();
                    int coluna = token.getCharPositionInLine() + 1;

                    // Escreve o token no arquivo
                    writer.write("<" + tokenType + ", " + lexema + ", " + linha + ", " + coluna + ">");
                    writer.newLine();  // Quebra de linha após cada token
                }
            } catch (IOException e) {
                System.err.println("Erro ao gravar no arquivo: " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Parsing error: " + e.getMessage());
        }
    }
}

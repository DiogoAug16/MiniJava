import org.antlr.v4.runtime.*;

import antlr.GrammarLexer;
import antlr.GrammarParser;
import astviewer.AstViewer;
import exception.LexerErrorListener;
import exception.ParserErrorListener;
import interpreter.Interpreter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option = 0;
                    
        File directory = new File("input/");
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                System.err.println("Arquivo encontrado: "+(i+1)+"-"+files[i].getName());
            }            
        } else {
            System.out.println("Nenhum arquivo encontrado na pasta input.");
        }

        System.out.println("Digite o numero do arquivo que deseje compilar:");
        option = scanner.nextInt();

        if (option < 1 || option > files.length) {
            System.out.println("Escolha inválida.");
            scanner.close();
            return;
        }

        File selectedFile = files[option-1];
        System.out.println("Arquivo selecionado: " + selectedFile.getName());
        System.out.println();

        try {
            GrammarLexer lexer = new GrammarLexer(CharStreams.fromFileName(selectedFile.getPath()));

            // Criação do lexer e parser
            lexer.removeErrorListeners();
            lexer.addErrorListener(new LexerErrorListener());

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            GrammarParser parser = new GrammarParser(tokens);
            
            parser.removeErrorListeners();
            parser.addErrorListener(new ParserErrorListener());
            
            // Parsing do código
            GrammarParser.ProgramContext tree = parser.program();
            
            System.out.println("Parsing completado");
            
            // Criação da pasta "dot" ou "pngs" dentro de "output" caso não exista
            File outputDotDir = new File("output/dot/pngs");
            if (!outputDotDir.exists()) {
                outputDotDir.mkdirs();
            }

            AstViewer astViewer = new AstViewer(tree, Arrays.asList(parser.getRuleNames()));
            // astViewer.show(); // Teste astviewer
            astViewer.exportDot("output/dot/ast.dot");

            // Gera o arquivo png a partir do arquivo dot
            String dotFilePath = "output/dot/ast.dot";
            String pngFilePath = "output/dot/pngs/ast.png";
            ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tpng", dotFilePath, "-o", pngFilePath);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Arquivo PNG gerado com sucesso: " + pngFilePath);
            } else {
                System.err.println("Erro ao gerar o arquivo PNG.");
            }
            
            // Interpretador para executar o código
            Interpreter interpreter = new Interpreter();
            interpreter.visit(tree);
            
            // Criação da pasta "tokens" dentro de "output" caso não exista
            File outputDir = new File("output/tokens");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            // Criação do arquivo de saída para os tokens
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output/tokens/tokens.txt"))) {
                tokens.fill();
                
                // Itera sobre os tokens e escreve no arquivo
                for (Token token : tokens.getTokens()) {
                    String tokenType = lexer.getVocabulary().getDisplayName(token.getType());
                    String lexema = token.getText();
                    int linha = token.getLine();
                    int coluna = token.getCharPositionInLine() + 1;
                    
                    writer.write("<" + tokenType + ", " + lexema + ", " + linha + ", " + coluna + ">");
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Erro ao gravar no arquivo: " + e.getMessage());
            }
            
        } catch (IOException e) {
            System.out.println("Arquivo nao encontrado: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro de parsing: " + e.getMessage());
        }
        scanner.close();
    }
}
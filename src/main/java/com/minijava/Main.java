package com.minijava;

import org.antlr.v4.runtime.*;

import com.minijava.antlr.GrammarLexer;
import com.minijava.antlr.GrammarParser;
import com.minijava.ast.dot.DotGenerator;
import com.minijava.ast.image.AstImageGenerator;
import com.minijava.classcheck.ClassVerification;
import com.minijava.exception.LexerErrorListener;
import com.minijava.exception.ParserErrorListener;
import com.minijava.interpreter.Interpreter;
import com.minijava.tokengenerator.TokenGenerator;

import java.io.File;
import java.io.IOException;
// import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option = 0;

        File directory = new File("input/");
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                System.out.println("Arquivo encontrado: " + (i + 1) + "-" + files[i].getName());
            }
        } else {
            System.out.println("Nenhum arquivo encontrado na pasta input.");
        }

        System.out.println("Digite o numero do arquivo que deseja compilar:");
        option = scanner.nextInt();

        if (option < 1 || option > files.length) {
            System.out.println("Escolha inválida.");
            scanner.close();
            return;
        }

        File selectedFile = files[option - 1];
        System.out.println("Arquivo selecionado: " + selectedFile.getName());
        System.out.println();

        try {
            // Criação do lexer e parser
            GrammarLexer lexer = new GrammarLexer(CharStreams.fromFileName(selectedFile.getPath())); // gera os tokens a partir do codigo fonte

            lexer.removeErrorListeners();
            lexer.addErrorListener(new LexerErrorListener());

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            GrammarParser parser = new GrammarParser(tokens); // Consome os tokens do lexer
            parser.removeErrorListeners();
            parser.addErrorListener(new ParserErrorListener());

            // Parsing do código
            GrammarParser.ProgramContext tree = parser.program(); // Cria ast

            // AstViewer astViewer = new AstViewer(tree, Arrays.asList(parser.getRuleNames()));
            // astViewer.show();

            // Verificação da classe usando a classe Verification
            if (!ClassVerification.verifyClassName(tree, selectedFile)) {
                scanner.close();
                return;
            }

            System.out.println("Parsing completado");

            DotGenerator dotGen = new DotGenerator(tree);
            dotGen.exportDot("output/dot/ast.dot");

            // Gera o arquivo png a partir do arquivo dot
            String dotFilePath = "output/dot/ast.dot";
            String pngFilePath = "output/dot/pngs/ast.png";
            AstImageGenerator.generatePngFromDot(dotFilePath, pngFilePath);

            // Interpretador para executar o código
            Interpreter interpreter = new Interpreter();
            interpreter.visit(tree);

            // Criação do arquivo de saída para os tokens
            TokenGenerator.generate(tokens, "output/tokens/tokens.txt", lexer.getVocabulary());

        } catch (IOException e) {
            System.out.println("Arquivo nao encontrado: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro de parsing: " + e.getMessage());
        }
        scanner.close();
    }
}

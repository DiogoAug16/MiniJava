package com.minijava;

import org.antlr.v4.runtime.*;
import com.minijava.antlr.MiniJavaLexer;
import com.minijava.antlr.MiniJavaParser;
import com.minijava.ast.dot.DotGenerator;
import com.minijava.ast.image.AstImageGenerator;
import com.minijava.classcheck.ClassVerification;
import com.minijava.exception.LexerErrorListener;
import com.minijava.exception.ParserErrorListener;
import com.minijava.generateflags.MainHelper;

import com.minijava.semantic.MiniJavaSemantic;
import com.minijava.tokengenerator.TokenGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        File selectedFile = null;
        Scanner menuScanner = new Scanner(System.in); 

        try {
            int option = 0;
            File directory = new File("input/");
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));

            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    System.out.println("Arquivo encontrado: " + (i + 1) + "-" + files[i].getName());
                }
            } else {
                System.out.println("Nenhum arquivo encontrado na pasta input.");
                menuScanner.close();
                return; 
            }

            System.out.println("Digite o numero do arquivo que deseja compilar:");
            if (menuScanner.hasNextInt()) {
                option = menuScanner.nextInt();
            } else {
                System.out.println("Entrada inválida.");
                menuScanner.close();
                return;
            }
            
            if (option < 1 || files == null || option > files.length) {
                System.out.println("Escolha inválida.");
                menuScanner.close();
                return;
            }
            selectedFile = files[option - 1];

            System.out.println("Arquivo selecionado: " + selectedFile.getName());
            System.out.println();

            try (MiniJavaSemantic semanticAnalyzer = new MiniJavaSemantic()) {
                MiniJavaLexer lexer = new MiniJavaLexer(CharStreams.fromFileName(selectedFile.getPath()));
                lexer.removeErrorListeners();
                LexerErrorListener myLexerErrorListener = new LexerErrorListener();
                lexer.addErrorListener(myLexerErrorListener);
                
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                tokens.fill();
                
                MiniJavaParser parser = new MiniJavaParser(tokens);
                parser.removeErrorListeners();
                ParserErrorListener myParserErrorListener = new ParserErrorListener();
                parser.addErrorListener(myParserErrorListener); 
                
                MiniJavaParser.ProgramContext tree = parser.program();

                if (!ClassVerification.verifyClassName(tree, selectedFile)) {
                    return; 
                }

                if (parser.getNumberOfSyntaxErrors() > 0 || tree == null) {
                    System.out.println("Análise abortada devido a erros sintáticos.");
                    return; 
                }

                semanticAnalyzer.visit(tree); 
                semanticAnalyzer.printErrors(); 

                if (!semanticAnalyzer.getSemanticErrors().isEmpty()) {
                    System.out.println("Corrija os erros semanticos antes de continuar");
                    return; 
                }

                // AstViewer astViewer = new AstViewer(tree, Arrays.asList(parser.getRuleNames()));
                // astViewer.show();

                System.out.println("Parsing e analise semantica completado");

                DotGenerator dotGen = new DotGenerator(tree);
                dotGen.exportDot("output/dot/ast.dot");

                String dotFilePath = "output/dot/ast.dot";
                String svgFilePath = "output/dot/svgs/ast.svg";
                AstImageGenerator.generateSvgFromDot(dotFilePath, svgFilePath);

                MainHelper.generateTacAndLlvm(args, tree);

                TokenGenerator.generate(tokens, "output/tokens/tokens.txt", lexer.getVocabulary());
            }
        } catch (IOException e) {
            System.out.println("Erro de I/O: " + e.getMessage());
        } catch (Exception e) { 
            System.out.println("Erro durante o processamento: " + e.getMessage());
        } finally {
            if (menuScanner != null) {
                menuScanner.close();
            }
        }
    }
}
package com.minijava.ast.image;

import java.io.File;
import java.io.IOException;

public class AstImageGenerator {

    public static void generateSvgFromDot(String dotFilePath, String svgFilePath) {
        try {
            // Cria os diretórios se não existirem
            File pngDir = new File(svgFilePath).getParentFile();
            if (!pngDir.exists()) {
                pngDir.mkdirs();
            }

            // Chama o Graphviz para gerar a imagem
            ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tsvg", dotFilePath, "-o", svgFilePath);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Arquivo SVG gerado com sucesso: " + svgFilePath);
            } else {
                System.err.println("Erro ao gerar o arquivo SVG. Código de saída: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao gerar a imagem SVG: " + e.getMessage());
        }
    }
}
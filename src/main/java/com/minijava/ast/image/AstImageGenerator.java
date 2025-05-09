package com.minijava.ast.image;

import java.io.File;
import java.io.IOException;

public class AstImageGenerator {

    public static void generatePngFromDot(String dotFilePath, String pngFilePath) {
        try {
            // Cria os diretórios se não existirem
            File pngDir = new File(pngFilePath).getParentFile();
            if (!pngDir.exists()) {
                pngDir.mkdirs();
            }

            // Chama o Graphviz para gerar a imagem
            ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tpng", dotFilePath, "-o", pngFilePath);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Arquivo PNG gerado com sucesso: " + pngFilePath);
            } else {
                System.err.println("Erro ao gerar o arquivo PNG. Código de saída: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao gerar a imagem PNG: " + e.getMessage());
        }
    }
}

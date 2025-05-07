package com.antlrjavacompiler.classcheck;


import java.io.File;

import com.antlrjavacompiler.antlr.GrammarParser.ProgramContext;

public class ClassVerification {

    public static boolean verifyClassName(ProgramContext tree, File selectedFile) {
        if (tree.className == null) {
            System.err.println("Erro de parsing: a árvore sintática está incompleta (className é null). Verifique se o código fonte está correto.");
            return false;
        }

        String className = tree.className.getText();
        String fileNameWithoutExtension = selectedFile.getName().replaceFirst("[.][^.]+$", "");

        if (!fileNameWithoutExtension.equals(className)) {
            System.out.println("Erro: o nome da classe '" + className + "' deve ser igual ao nome do arquivo '" + fileNameWithoutExtension + "'.");
            return false;
        }

        return true;
    }
}

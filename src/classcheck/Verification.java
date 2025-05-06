package classcheck;

import org.antlr.v4.runtime.tree.ParseTree;
import java.io.File;

public class Verification {

    public static boolean verifyClassName(ParseTree tree, File selectedFile) {
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

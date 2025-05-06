package tokengenerator;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TokenGenerator {

    public static void generate(CommonTokenStream tokens, String outputPath, Vocabulary vocabulary) {
        File outputDir = new File(outputPath).getParentFile();
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            tokens.fill();
            for (Token token : tokens.getTokens()) {
                String tokenType = vocabulary.getDisplayName(token.getType());
                String lexema = token.getText();
                int linha = token.getLine();
                int coluna = token.getCharPositionInLine() + 1;

                writer.write("<" + tokenType + ", " + lexema + ", " + linha + ", " + coluna + ">");
                writer.newLine();
            }
            System.out.println();
            System.out.println("Tokens salvos em: " + outputPath);
        } catch (IOException e) {
            System.out.println();
            System.err.println("Erro ao salvar tokens: " + e.getMessage());
        }
    }
}

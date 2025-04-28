
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            GrammarCompilerLexer lexer = new GrammarCompilerLexer(CharStreams.fromFileName("input/triangulo_pascal.txt"));

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            GrammarCompilerParser parser = new GrammarCompilerParser(tokens);

            parser.program();

            System.out.println("Parsing completed successfully!");

        } catch (IOException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Parsing error: " + e.getMessage());
        }
    }
}

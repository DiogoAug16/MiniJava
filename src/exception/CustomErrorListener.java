package exception;
import org.antlr.v4.runtime.*;

public class CustomErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                             int line, int charPositionInLine, String msg, RecognitionException e) {
                                
        String errorMessage = String.format("ERRO LÉXICO [Linha %d, Coluna %d]: Símbolo '%s' inválido.", 
                                            line, charPositionInLine + 1, ((Token) offendingSymbol).getText());
        
        System.err.println(errorMessage);
    }
}

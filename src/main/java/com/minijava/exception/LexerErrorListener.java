package com.minijava.exception;


import org.antlr.v4.runtime.*;

public class LexerErrorListener extends BaseErrorListener {
 
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine, String msg, RecognitionException e) {
                                
        String lexema = "";

        if (offendingSymbol instanceof Token token) {
            lexema = token.getText();
        } else if (msg.contains("token recognition error")) {
            // extrai o símbolo inválido da mensagem
            int start = msg.indexOf("'") + 1;
            int end = msg.lastIndexOf("'");
            if (start > 0 && end > start) {
                lexema = msg.substring(start, end);
            }
        }

        String errorMessage = String.format(
            "ERRO LÉXICO [Linha %d, Coluna %d]: Símbolo '%s' inválido.",
            line, charPositionInLine + 1, lexema);
        System.err.println(errorMessage);
    }
}

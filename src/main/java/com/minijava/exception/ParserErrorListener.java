package com.minijava.exception;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.List;
import java.util.stream.Collectors;

public class ParserErrorListener extends BaseErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg, RecognitionException e) {
    
        String encontrado = offendingSymbol instanceof Token token
                ? token.getText()
                : "desconhecido";
    
        String esperado = "desconhecido";
    
        IntervalSet expectedTokens = null;
    
        if (e != null) {
            expectedTokens = e.getExpectedTokens();
        } else if (recognizer instanceof Parser parser) {
            expectedTokens = parser.getExpectedTokens();
        }
    
        if (expectedTokens != null) {
            List<String> expectedTokenNames = expectedTokens.toList().stream()
                    .map(tokenType -> recognizer.getVocabulary().getDisplayName(tokenType))
                    .collect(Collectors.toList());
            esperado = String.join(" ou ", expectedTokenNames);
        }
    
        System.err.printf("ERRO SINTÁTICO [Linha %d, Coluna %d]: Esperado %s, encontrado '%s'.%n",
                line, charPositionInLine + 1, esperado, encontrado);
    }
    
}
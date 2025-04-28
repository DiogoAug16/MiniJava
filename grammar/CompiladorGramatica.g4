grammar CompiladorGramatica;

programa
    : comando* EOF
    ;

comando
    : atribuicao ';'
    | leitura ';'
    | escrita ';'
    | condicional 
    | repeticao
    ;

atribuicao
    : ID '=' expressao
    ;

leitura
    : 'leia' '(' ID ')' 
    ;

escrita
    : 'escreva' '(' expressao ')' 
    ;

condicional
    : 'se' expressao 'entao' comando* ('senao' comando*)? 'fimse'
    ;

repeticao
    : 'enquanto' expressao 'faca' comando* 'fimenquanto'
    ;

expressao
    : expressaoLogica
    ;

expressaoLogica
    : expressaoRelacional ( ( '&&' | '||' ) expressaoRelacional )*
    ;

expressaoRelacional
    : expressaoAritmetica ( ( '==' | '!=' | '<' | '<=' | '>' | '>=' ) expressaoAritmetica )?
    ;

expressaoAritmetica
    : termo ( ( '+' | '-' ) termo )*
    ;

termo
    : fator ( ( '*' | '/' ) fator )*
    ;

fator
    : '(' expressao ')'
    | ID
    | NUMERO
    | STRING
    | '!' fator
    ;

NUMERO 
    : [0-9]+ ('.' [0-9]+)?
    ;

STRING 
    : '"' (~["\r\n])* '"'
    ;

ID
    : [a-zA-Z_] [a-zA-Z0-9_]*
    ;

WS 
    : [ \t\r\n]+ -> skip
    ;
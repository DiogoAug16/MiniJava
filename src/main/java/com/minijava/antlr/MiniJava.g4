grammar MiniJava;

@header {
package com.minijava.antlr;
}

program
    : ('private')? 'class' className=ID '{' declarations statements '}'EOF //toda classe é publica por padrão
    ;

declarations
    : (declaration)*
    ;

declaration
    : ('int' | 'string') ID ';'
    ;

statements
    : (statement)*
    ;

statement
    : assignment
    | read
    | write
    | ifStatement
    | whileStatement
    ;

assignment
    : ID '=' expression ';'
    ;

read
    : 'scanf' '(' ID ')' ';'
    ;

write
    : ('println' | 'print') '(' expression ')' ';'
    ;

ifStatement
    : 'if' '(' condition ')' '{' block '}' ('else' '{' block '}')?
    ;

whileStatement
    : 'while' '(' condition ')' '{' block '}'
    ;

block
    : statement+
    ;

expression
    : concatenation
    ;

concatenation
    : additiveExpression ( '+' additiveExpression )*
    ;

additiveExpression
    : term (('+'|'-') term)*
    | STRING
    ;

term
    : factor (('*'|'/') factor)*
    ;

factor
    : INT
    | ID
    | '(' expression ')'
    ;

logicalExpression
    : logicalFactor (('&&'|'||') logicalFactor)*
    ;

logicalFactor
    : '!'? (comparison | '(' logicalExpression ')')
    ;

condition
    : logicalExpression
    | expression
    ;

comparison
    : expression ('==' | '>' | '<' | '>=' | '<=') expression
    ;

// Tokens

ID
    : [a-zA-Z_] [a-zA-Z_0-9]*
    ;

INT
    : [0-9]+
    ;

STRING
    : '"' (~["\r\n])* '"'
    ;

WS
    : [ \t\r\n]+ -> skip
    ;

COMMENT
    : '//' ~[\r\n]* -> skip
    ;
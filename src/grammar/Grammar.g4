grammar Grammar;

@header {
package antlr;
}

program
    : declarations statements EOF
    ;

declarations
    : (declaration)*
    ;

declaration
    : 'int' ID ';'
    | 'string' ID ';'
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
    : 'read' '(' ID ')' ';'
    ;

write
    : 'writeln' '(' expression ')' ';'
    | 'write' '(' expression ')' ';'
    ;

ifStatement
    : 'if' '(' logicalExpression ')' 'then' block ('else' block)? 'endif'
    ;

whileStatement
    : 'while' '(' logicalExpression ')' 'do' block 'endwhile'
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
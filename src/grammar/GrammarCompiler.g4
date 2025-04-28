grammar GrammarCompiler;

program: declarations statements EOF;

declarations: (declaration)*;

declaration: 'int' ID ';'
           | 'string' ID ';';

statements: (statement)*;

statement: assignment
         | read
         | write
         | ifStatement
         | whileStatement;

assignment: ID '=' expression ';';

read: 'read' '(' ID ')' ';';

write: 'write' '(' expression ')' ';';

ifStatement: 'if' '(' logicalExpression ')' 'then' statements ('else' statements)? 'endif';

whileStatement: 'while' '(' logicalExpression ')' 'do' statements 'endwhile';

expression: term (('+'|'-') term)*;

term: factor (('*'|'/') factor)*;

factor: INT
      | ID
      | '(' expression ')';

logicalExpression: logicalFactor (('&&'|'||') logicalFactor)*;

logicalFactor: '!'? (comparison | '(' logicalExpression ')');

comparison: expression '==' expression;

// Tokens

ID: [a-zA-Z_] [a-zA-Z_0-9]*;
INT: [0-9]+;
STRING: '"' .*? '"';

WS: [ \t\r\n]+ -> skip;

COMMENT: '//' ~[\r\n]* -> skip;

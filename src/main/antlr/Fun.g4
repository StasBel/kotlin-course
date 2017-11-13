grammar Fun;

file
    : block EOF
    ;

block
    : statement*
    ;

statement
    : function
    | variable
    | expression
    | whileBlock
    | ifBlock
    | assignment
    | returnBlock
    ;

function
    : 'fun' name=ID '(' argumentNames ')' '{' block '}'
    ;

argumentNames
    : (ID (',' ID)*)?
    ;

variable
    : 'var' name=ID ('=' expression)?
    ;

expression
    : name=ID '(' arguments ')'                                  # FuncCallExpr
    | left=expression op=('*' |'/' |'%'     ) right=expression   # BinopExpr
    | left=expression op=('+' |'-'          ) right=expression   # BinopExpr
    | left=expression op=('<' |'<='|'>'|'>=') right=expression   # BinopExpr
    | left=expression op=('=='|'!='         ) right=expression   # BinopExpr
    | left=expression op= '&&'                right=expression   # BinopExpr
    | left=expression op= '||'                right=expression   # BinopExpr
    | value=LITERAL                                              # LiteralExpr
    | name=ID                                                    # IDExpr
    | '('  expression ')'                                        # ParentsExpr
    ;

arguments
    : (expression (',' expression)*)?
    ;

whileBlock
    : 'while' '(' condition=expression ')' '{' block '}'
    ;

ifBlock
    : 'if' '(' condition=expression ')' '{' trueBlock=block '}' ('else' '{' falseBlock=block '}')?
    ;

assignment
    : name=ID '=' expression
    ;

returnBlock
    : 'return' expression
    ;


KEYWORDS  : 'fun' | 'var' | 'while' | 'if' | 'else' | 'return' ;

ID        : [a-zA-Z_] [a-zA-Z_0-9]*  ;
LITERAL   : [-+]? ('0'|[1-9][0-9]*)  ;

WS        : [ \r\t\u000C\n]+ -> skip ;
COMMENT   : '//' ~[\r\n]*    -> skip ;
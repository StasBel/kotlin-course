grammar Expr;


prog
    :   expr EOF
    ;

expr
    :   left=expr op=('*' |'/' |'%'     ) right=expr   # BinopExpr
    |   left=expr op=('+' |'-'          ) right=expr   # BinopExpr
    |   left=expr op=('<' |'<='|'>'|'>=') right=expr   # BinopExpr
    |   left=expr op=('=='|'!='         ) right=expr   # BinopExpr
    |   left=expr op= '&&'                right=expr   # BinopExpr
    |   left=expr op= '||'                right=expr   # BinopExpr
    |   '(' expr ')'                                   # ParentsExpr
    |   value=LITERAL                                  # LiteralExpr
    ;

ID        :   [a-zA-Z_] [a-zA-Z_0-9]*  ;
LITERAL   :   [-+]? ('0'|[1-9][0-9]*)  ;

WS        :   [ \r\t\u000C\n]+ -> skip ;
COMMENT   :   '//' ~[\r\n]*    -> skip ;
term          := operator "(" term term ")" | unary ;

operator      := "add" | "subtract" | "divide" | "multiply" ;

unary         := ("NOT" | "-") unary | call ;

call          := primary ("(" args? ")")? ; # take "(" greedy lookahead

args          := expr ("," expr)* ;

primary       := ("cast_to" type)? ("true" | "false" | NUMBER | STRING | ID | "(" expr ")") ;



## what if

var something is 10

add(something -5)

grammar allows:

something + 5   ->  "add" + "call" + "call" + "..."
something + 5   ->  "add" + "ID" + "expr" + "..."

something(5) + ... -> "add" + "call" + "..."
something(5) + ... -> "add" + "ID (args)" + "..."

resursvie descent parser with 1 lookahead only allows:

something(5) + ... -> "add" + "call" + "..."
something(5) + ... -> "add" + "ID (args)" + "..."

solvable with scoping but **not** in *parser*

do we force it to alwyas take it as function call if () or 
make multiple parse-trees or ...
# Language Specification for Yan

## Syntax Grammar

```
program -> declartion* EOF
```

### Decalarations

```
declaration -> funcDecl 
						 | varDecl
						 | statement
funcDecl -> "func" IDENTIFIER "(" parameters ")" ["->" type] block
varDecl -> "var" IDENTIFIER ":" type ["=" expression] ";"
type -> "float" | "int" | "string" | "bool"
parameters -> IDENTIFIER: type
```

### Statements 

```
statement -> block
					 | ifStmt
					 | whileStmt
					 | forStmt
					 | exprStmt
					 | returnStmt
					 | continueStmt
					 | breakStmt
					 | printStmt
ifStmt -> "if" "(" expression ")" "{" block "}" ["else" block ]
whileStmt -> "while" "(" expression ")"  block
## forStmt -> for(varDecl|exprStmt expression; expression) block
exprStmt -> expression ";"
returnStmt -> "return" ";"
continueStmt -> "continue" ";"
breakStmt -> "break" ";"
block -> "{" statement* "}"
printStmt -> "print" "(" expression ")" 
```

### Expressions

```
expression -> assignment
assignment -> IDENTIFIER "=" assignment 
						| logic_or
logic_or -> logic_and ("||" logic_and)*
logic_and -> equality ("&&" equality)*
equality -> comparison (("!=" | "==") comparison)*
comparison -> addition ((">=" | ">" | "<" | "<=") addition)*
addition -> multiplication (("+" | "-") multiplication)*
multiplication -> unary (("*" | "/") unary)*
unary -> ("!" | "-") unary | call
call -> primary "("arguments?")"
primary -> "true" | "false" 
				 | NUMBER | STRING | IDENTIFIER | "("expression")"
arguments -> expression ("," expression)* 
```

## Lexical Grammar

```
IDENTIFIER -> ALPHA (ALPHA | DIGIT)*
ALPHA -> a-z | A-Z | _
DIGIT -> 0-9
STRING -> "any char except ""
NUMBER -> DIGIT+ [.DIGIT+]
## COMMENT -> "//"
```


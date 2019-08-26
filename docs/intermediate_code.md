# Intermediate Code Specification

## ILGEN

- global var: type name initializer 
- function block
    - commands: op arg1 arg2 result
    - var: type name initializer 
    - type signature: type
- current block

## Instruction Set

```
add, sub, multi, div,
and, or, 
equ, nequ,
ge, g, l, le,
not, neg,

call,
param,
ret,

jmp,
test,

[optional]
func_b
func_e
gparam

gvar
lvar
```

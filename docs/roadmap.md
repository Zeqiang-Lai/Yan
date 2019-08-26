## Interpreter
- [ ] refactor runtime exception
- [ ] more test

## Compiler

### semantic analysis

every expression has a type.

#### Declaration

**var declaration**

- store symbol
- initializer must be able to be evaluated at compile time.
- type of initializer must match the type of variable.
- variable should not be defined before in the same scope.

**func declaration**

- check if required return statement exist, if its type is right.

#### Statement

**block**

- begin a new scope.

**if**

- check if conditional expression has bool type.

**while**

- check if while conditional expression has bool type.

**return**

- check if it is in a function body.

**continue, break**

- check if it is in a while body


#### Expression

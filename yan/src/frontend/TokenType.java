package frontend;

public enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN,
    LEFT_BRACE, RIGHT_BRACE,
    COMMA, SEMICOLON, COLON,

    // One or two character tokens.
    ADD, ADD_ASSIGN,
    SUB, SUB_ASSIGN,
    MULTI, MULTI_ASSIGN,
    DIV, DIV_ASSIGN,
    MOD, MOD_ASSIGN,

    REL_AND, REL_OR,
    REL_NOT, NOT_EQUAL,
    ASSIGN, EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    ARROW,

    // Literals.
    IDENTIFIER, STRING, INTEGER_CONSTANT, FLOAT_CONSTANT,

    // Keywords.
    IF, ELSE, WHILE, RETURN, PRINT, BREAK, CONTINUE, FUNC, VAR,

    // Types
    INT, FLOAT, CHAR,

    EOF,
    UNKNOWN;
}

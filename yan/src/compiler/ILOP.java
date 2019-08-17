package compiler;

import frontend.TokenType;

import java.util.HashMap;
import java.util.Map;

// intermediate language operator
public enum ILOP {
    add, sub, multi, div,
    and, or,
    equ, nequ,
    ge, g, l, le,
    not, neg,

    assign,

    call,
    param,
    ret,

    jf,jt,jmp,
    print,
    label;

    static int max_length = 5;

    static Map<TokenType, ILOP> type2op = new HashMap<>();
    static {
        type2op.put(TokenType.ADD, add);
        type2op.put(TokenType.SUB, sub);
        type2op.put(TokenType.MULTI, multi);
        type2op.put(TokenType.DIV, div);
        type2op.put(TokenType.REL_AND, and);
        type2op.put(TokenType.REL_OR, or);
        type2op.put(TokenType.EQUAL, equ);
        type2op.put(TokenType.NOT_EQUAL, nequ);
        type2op.put(TokenType.GREATER_EQUAL, ge);
        type2op.put(TokenType.GREATER, g);
        type2op.put(TokenType.LESS_EQUAL, le);
        type2op.put(TokenType.LESS, l);
        type2op.put(TokenType.REL_NOT, not);
    }

    static ILOP valueOf(TokenType type) {
        return type2op.get(type);
    }
}

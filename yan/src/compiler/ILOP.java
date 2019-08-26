package compiler;

import frontend.DataType;
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
    label,

    // type conversion
    i2f,f2i,b2i,i2b
    ;

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

    static ILOP getConvertOP(DataType from, DataType to) {
        if(from == DataType.INT && to == DataType.FLOAT) return i2f;
        if(from == DataType.FLOAT && to == DataType.INT) return f2i;
        if(from == DataType.BOOL && to == DataType.INT) return b2i;
        if(from == DataType.INT && to == DataType.BOOL) return i2b;
        throw new RuntimeException("invalid conversion " + from + " " + to);
    }
}

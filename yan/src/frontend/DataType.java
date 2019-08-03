package frontend;

import java.util.HashMap;
import java.util.Map;

public enum DataType {
    INT, FLOAT, STRING, BOOL, CHAR,
    FUNCTION;

    static public Map<TokenType, DataType> tokenType2DataType = new HashMap<>();

    static {
        tokenType2DataType.put(TokenType.INT, INT);
        tokenType2DataType.put(TokenType.FLOAT, FLOAT);
        tokenType2DataType.put(TokenType.STRING, STRING);
        tokenType2DataType.put(TokenType.BOOL, BOOL);
        tokenType2DataType.put(TokenType.CHAR, CHAR);
    }

    static public boolean computeCompatible(DataType left, DataType right) {
        return compatible(left, right, true);
    }

    static public boolean assignCompatible(DataType left, DataType right) {
        return compatible(left, right, false);
    }

    static public boolean compatible(DataType left, DataType right, boolean is_compute) {
        if(left == right)
            return true;
        switch (left) {
            case INT:
                if(is_compute)
                    return right == BOOL || right == FLOAT;
                return right == BOOL;
            case FLOAT:
            case BOOL:
                return right == INT;
            case STRING:
            case FUNCTION:
                return false;
        }
        return false;
    }

    static public DataType implicitConversion(DataType left, DataType right) {
        if(left == right)
            return left;
        if(left == FLOAT || right == FLOAT)
            return FLOAT;
        if(left == INT && right == BOOL || left == BOOL && right == INT)
            return INT;
        throw new RuntimeException();
    }
}

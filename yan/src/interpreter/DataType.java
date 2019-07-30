package interpreter;
import frontend.TokenType;

import java.util.HashMap;
import java.util.Map;

public enum DataType {
    INT, FLOAT, STRING, BOOL,
    FUNCTION;

    static public Map<TokenType, DataType> tokenType2DataType = new HashMap<>();

    static {
        tokenType2DataType.put(TokenType.INT, INT);
        tokenType2DataType.put(TokenType.FLOAT, FLOAT);
        tokenType2DataType.put(TokenType.STRING, STRING);
        tokenType2DataType.put(TokenType.BOOL, BOOL);
    }
}

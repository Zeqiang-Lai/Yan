package compiler;

import frontend.DataType;
import frontend.ast.Node;

public class Symbol {
    DataType type;
    Node value;

    public Symbol(DataType type, Node value) {
        this.type = type;
        this.value = value;
    }
}

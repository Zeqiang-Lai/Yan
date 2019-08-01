package interpreter;

import frontend.DataType;

public class YanObject extends Object{
    Object value;
    DataType type;

    public YanObject(Object value, DataType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

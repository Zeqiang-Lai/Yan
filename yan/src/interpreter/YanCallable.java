package interpreter;

import java.util.List;

public interface YanCallable {
    YanObject call(Interpreter interpreter, List<YanObject> arguments);
    int arity();
    boolean checkType(DataType type, int idx);
}

package interpreter;

import java.util.List;

public interface YanCallable {
    Object call(Interpreter interpreter, List<YanObject> arguments);
}

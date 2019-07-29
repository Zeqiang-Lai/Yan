package interpreter;

import frontend.ast.StmtNode;

import java.util.List;

public class YanFunction extends YanObject implements YanCallable{
    private final StmtNode.Function function;

    public YanFunction(StmtNode.Function function) {
        super(function.name.lexeme, DataType.FUNCTION);
        this.function = function;
    }

    @Override
    public YanObject call(Interpreter interpreter, List<YanObject> arguments) {
        Environment environment = new Environment(interpreter.globals);
        // check arguments signature
        for(int i=0; i<arguments.size(); i++) {
                
        }

        return null;
    }

    @Override
    public String toString() {
        return "<fn " + function.name.lexeme + " >";
    }
}

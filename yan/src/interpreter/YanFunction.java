package interpreter;

import frontend.DataType;
import frontend.ast.StmtNode;
import interpreter.error.RuntimeError;

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
        for(int i=0; i<arguments.size(); ++i) {
            if(!(checkType(arguments.get(i).type, i)))
                throw new RuntimeError(null, "type not matched, expected " +
                        function.types.get(i) + ", but got" + arguments.get(i).type);
            environment.define(function.params.get(i).lexeme, arguments.get(i));
        }
        try {
            interpreter.executeBlock(function.body.items, environment);
        }catch (RuntimeException e) {
            if(e instanceof Return)
                return ((Return) e).value;
            throw e;
        }
        return null;
    }

    @Override
    public int arity() {
        return function.params.size();
    }

    @Override
    public boolean checkType(DataType type, int idx) {
        return type ==function.types.get(idx);
    }

    @Override
    public String toString() {
        return "<fn " + function.name.lexeme + " >";
    }
}

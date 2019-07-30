package interpreter;

import error.ErrorCollector;
import interpreter.error.RuntimeError;
import frontend.TokenType;
import frontend.ast.ExprNode;
import frontend.ast.StmtNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static frontend.TokenType.*;

public class Interpreter implements ExprNode.Visitor<YanObject>, StmtNode.Visitor<YanObject> {
    final Environment globals = new Environment();
    private Environment environment = globals;
    private ErrorCollector errorCollector = ErrorCollector.getInstance();

    private boolean breakloop = false;
    private boolean exitBlock = false;

    private final static Map<TokenType, YanObject> defalutValue = new HashMap<>();


    static {
        defalutValue.put(INT, new YanObject(0, DataType.INT));
        defalutValue.put(FLOAT, new YanObject(0, DataType.FLOAT));
        defalutValue.put(STRING, new YanObject(0, DataType.STRING));
        defalutValue.put(BOOL, new YanObject(0, DataType.BOOL));
    }

    public Interpreter() {
    }


    // region: Interface

    public void interpret(List<StmtNode> statements) {
        for (StmtNode statement : statements) {
            try {
                execute(statement);
            } catch (RuntimeError error) {
                errorCollector.add(error);
            }
        }
    }

    // endregion

    // region: Utils

    private YanObject evaluate(ExprNode expr) {
        return expr.accept(this);
    }

    private void execute(StmtNode stmt) {
        stmt.accept(this);
    }

    public void executeBlock(List<StmtNode> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (StmtNode statement : statements) {
                execute(statement);
                if(exitBlock) break;
            }
        } finally {
            this.environment = previous;
        }
    }

    private boolean isTruthy(Object value) {
        if (value instanceof Boolean) return (boolean) value;
        if (value instanceof Integer) {
            Integer tmp = (Integer) value;
            if (tmp == 0)
                return false;
            return true;
        }
        throw new RuntimeError(null,
                "expression in the condition should be able to evaluate as boolean.");
    }

    private String stringify(Object value) {
        return value.toString();
    }

    private boolean checkType(DataType target, DataType... types) {
        for(DataType type : types) {
            if(target == type)
                return true;
        }
        throw new RuntimeError(null,
                "operand of negative sign should be a number.");
    }

    // endregion

    // region: Expression

    @Override
    public YanObject visitAssignExpr(ExprNode.Assign expr) {
        YanObject value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return value;
    }

    @Override
    public YanObject visitBinaryExpr(ExprNode.Binary expr) {
        YanObject left = evaluate(expr.left);
        YanObject right = evaluate(expr.right);
        TokenType op = expr.operator.type;

        // should be number type.
        checkType(left.type, DataType.INT, DataType.FLOAT);
        checkType(right.type, DataType.INT, DataType.FLOAT);

        Double left_value = Double.valueOf(String.valueOf(left.value));
        Double right_value = Double.valueOf(String.valueOf(right.value));
        Double result = new Double(0);
        DataType result_type;
        if(left.type == DataType.FLOAT || right.type == DataType.FLOAT)
            result_type = DataType.FLOAT;
        else
            result_type = DataType.INT;

        switch (op) {
            case ADD:
                result = left_value + right_value; break;
            case SUB:
                result = left_value - right_value; break;
            case MULTI:
                result = left_value * right_value; break;
            case DIV:
                result = left_value / right_value; break;
            default:
                // unreachable.
        }
        if(result_type == DataType.INT)
            return new YanObject(Integer.valueOf(result.intValue()), DataType.INT);
        else
            return new YanObject(result, DataType.FLOAT);
    }

    @Override
    public YanObject visitCallExpr(ExprNode.FunCall expr) {
        // check if the function is defined.
        YanObject func = environment.get(expr.paren);
        if(func instanceof YanCallable) {
            // validate args number
            if(((YanCallable) func).arity() != expr.arguments.size())
                throw new RuntimeError(null,
                        "the number of provided arguments and expected number of arguments are not matched");
            // evaluate args
            List<YanObject> args = new LinkedList<>();
            for(int i=0; i<expr.arguments.size(); ++i) {
                YanObject value = evaluate(expr.arguments.get(i));
                args.add(value);
            }
            // call
            return ((YanCallable) func).call(this, args);
        } else {
            throw new RuntimeError(null, "invalid function call.");
        }
    }

    @Override
    public YanObject visitGroupingExpr(ExprNode.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public YanObject visitLiteralExpr(ExprNode.Literal expr) {
        YanObject value = null;
        if (expr.value instanceof Integer) {
            value = new YanObject(expr.value, DataType.INT);
        } else if(expr.value instanceof Boolean) {
            value = new YanObject(expr.value, DataType.BOOL);
        } else if(expr.value instanceof Double) {
            value = new YanObject(expr.value, DataType.FLOAT);
        } else if(expr.value instanceof String) {
            value = new YanObject(expr.value, DataType.STRING);
        } else {
            // should be unreachable.
            throw new RuntimeError(null, "invalid literal type.");
        }
        return value;
    }

    @Override
    public YanObject visitLogicalExpr(ExprNode.Logical expr) {
        YanObject left = evaluate(expr.left);
        YanObject right = evaluate(expr.right);
        TokenType op = expr.operator.type;

        if(left.type != DataType.BOOL || right.type != DataType.BOOL)
            throw new RuntimeError(expr.operator,
                    "operands of relation operator should be able to be evaluated as bool");
        boolean result;
        switch (op) {
            case REL_AND:
                result = (Boolean)left.value && (Boolean)right.value;
                return new YanObject(result, DataType.BOOL);
            case REL_OR:
                result = (Boolean)left.value || (Boolean)right.value;
                return new YanObject(result, DataType.BOOL);
        }
        return null;
    }

    @Override
    public YanObject visitRelationExpr(ExprNode.Relation expr) {
        YanObject left = evaluate(expr.left);
        YanObject right = evaluate(expr.right);
        TokenType op = expr.operator.type;

        // should be number type.
        checkType(left.type, DataType.INT, DataType.FLOAT);
        checkType(right.type, DataType.INT, DataType.FLOAT);

        double left_value = (Double) left.value;
        double right_value = (Double) right.value;

        switch (op) {
            case GREATER:
                return new YanObject(left_value > right_value, DataType.BOOL);
            case GREATER_EQUAL:
                return new YanObject(left_value >= right_value, DataType.BOOL);
            case LESS:
                return new YanObject(left_value < right_value, DataType.BOOL);
            case LESS_EQUAL:
                return new YanObject(left_value <= right_value, DataType.BOOL);
            case EQUAL:
                return new YanObject(left.equals(right), DataType.BOOL);
            case NOT_EQUAL:
                return new YanObject(!left.equals(right), DataType.BOOL);
        }

        return null;
    }

    @Override
    public YanObject visitUnaryExpr(ExprNode.Unary expr) {
        YanObject right = evaluate(expr.right);
        Object value = null;
        switch (expr.operator.type) {
            case SUB:
                if(checkType(right.type, DataType.INT))
                    value = -(int)right.value;
                if(checkType(right.type, DataType.FLOAT))
                    value = -(double)right.value;
                break;
            case REL_NOT:
                value = !(Boolean) right.value;
                break;
            default:
                // unreachable
                break;
        }
        return new YanObject(value, right.type);
    }

    @Override
    public YanObject visitVariableExpr(ExprNode.Variable expr) {
        return environment.get(expr.name);
    }
    // endregion

    // region: Statement
    @Override
    public YanObject visitBlockStmt(StmtNode.Block stmt) {
        executeBlock(stmt.items, new Environment(environment));
        return null;
    }

    @Override
    public YanObject visitEmptyStmt(StmtNode.Empty stmt) {
        return null;
    }

    @Override
    public YanObject visitExpressionStmt(StmtNode.Expression stmt) {
        return evaluate(stmt.expr);
    }

    @Override
    public YanObject visitFunctionStmt(StmtNode.Function stmt) {
        environment.define(stmt.name.lexeme, new YanFunction(stmt));
        return null;
    }

    @Override
    public YanObject visitIfStmt(StmtNode.If stmt) {
        if (isTruthy(evaluate(stmt.cond))) {
            execute(stmt.if_body);
        } else if (stmt.else_body != null) {
            execute(stmt.else_body);
        }
        return null;
    }

    @Override
    public YanObject visitPrintStmt(StmtNode.Print stmt) {
        Object value = evaluate(stmt.value);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public YanObject visitReturnStmt(StmtNode.Return stmt) {
        YanObject value = evaluate(stmt.value);
        throw new Return(value);
    }

    @Override
    public YanObject visitVarStmt(StmtNode.Var stmt) {
        YanObject value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        } else {
            value = defalutValue.get(stmt.type.type);
        }
        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public YanObject visitWhileStmt(StmtNode.While stmt) {
        while (isTruthy(evaluate(stmt.cond))) {
            execute(stmt.body);
            exitBlock = false;
            if(breakloop)
                break;
        }
        breakloop = false;
        return null;
    }

    @Override
    public YanObject visitBreakStmt(StmtNode.Break stmt) {
        // FIXME: handle nested loop
        exitBlock = true;
        breakloop = true;
        return null;
    }

    @Override
    public YanObject visitContinueStmt(StmtNode.Continue stmt) {
        // FIXME: handle nested loop
        exitBlock = true;
        return null;
    }

    // endregion
}

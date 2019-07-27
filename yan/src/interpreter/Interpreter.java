package interpreter;

import error.ErrorCollector;
import error.RuntimeError;
import frontend.Token;
import frontend.TokenType;
import frontend.ast.ExprNode;
import frontend.ast.StmtNode;

import java.util.List;

import static frontend.TokenType.*;

public class Interpreter implements ExprNode.Visitor<Object>, StmtNode.Visitor<Object> {
    private Environment environment = new Environment();
    private ErrorCollector errorCollector = ErrorCollector.getInstance();

    private boolean breakloop = false;
    private boolean exitBlock = false;

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

    private Object evaluate(ExprNode expr) {
        return expr.accept(this);
    }

    private void execute(StmtNode stmt) {
        stmt.accept(this);
    }

    private void executeBlock(List<StmtNode> statements, Environment environment) {
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

    // endregion

    // region: Expression

    @Override
    public Object visitAssignExpr(ExprNode.Assign expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return value;
    }

    @Override
    public Object visitBinaryExpr(ExprNode.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        TokenType op = expr.operator.type;

        switch (op) {
            case ADD:
                return (Double) left + (Double) right;
            case SUB:
                return (Double) left - (Double) right;
            case MULTI:
                return (Double) left * (Double) right;
            case DIV:
                return (Double) left / (Double) right;
        }

        return null;
    }

    @Override
    public Object visitCallExpr(ExprNode.FunCall expr) {
        return null;
    }

    @Override
    public Object visitGroupingExpr(ExprNode.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(ExprNode.Literal expr) {
        Object value = null;
        if (expr.value instanceof Integer) {
            Integer tmp = (Integer) expr.value;
            value = Double.valueOf((double) tmp);
        } else {
            value = expr.value;
        }
        return value;
    }

    @Override
    public Object visitLogicalExpr(ExprNode.Logical expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        TokenType op = expr.operator.type;

        switch (op) {
            case REL_AND:
                return (Boolean) left && (Boolean) right;
            case REL_OR:
                return (Boolean) left || (Boolean) right;
        }
        return null;
    }

    @Override
    public Object visitRelationExpr(ExprNode.Relation expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        TokenType op = expr.operator.type;

        switch (op) {
            case GREATER:
                return (Double) left > (Double) right;
            case GREATER_EQUAL:
                return (Double) left >= (Double) right;
            case LESS:
                return (Double) left < (Double) right;
            case LESS_EQUAL:
                return (Double) left <= (Double) right;
            case EQUAL:
                return left.equals(right);
            case NOT_EQUAL:
                return !left.equals(right);
        }
        return null;
    }

    @Override
    public Object visitUnaryExpr(ExprNode.Unary expr) {
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case SUB:
                if (right instanceof Double)
                    right = -(Double) right;
                else if (right instanceof Integer)
                    right = -(Integer) right;
                else
                    throw new RuntimeError(expr.operator,
                            "operand of negative sign should be a number.");
                break;
            case REL_NOT:
                right = !(Boolean) right;
                break;
            default:
                // unreachable
                break;
        }
        return right;
    }

    @Override
    public Object visitVariableExpr(ExprNode.Variable expr) {
        return environment.get(expr.name);
    }
    // endregion

    // region: Statement
    @Override
    public String visitBlockStmt(StmtNode.Block stmt) {
        executeBlock(stmt.items, new Environment(environment));
        return null;
    }

    @Override
    public String visitEmptyStmt(StmtNode.Empty stmt) {
        return null;
    }

    @Override
    public Object visitExpressionStmt(StmtNode.Expression stmt) {
        return evaluate(stmt.expr);
    }

    @Override
    public String visitFunctionStmt(StmtNode.Function stmt) {
        return null;
    }

    @Override
    public String visitIfStmt(StmtNode.If stmt) {
        if (isTruthy(evaluate(stmt.cond))) {
            execute(stmt.if_body);
        } else if (stmt.else_body != null) {
            execute(stmt.else_body);
        }
        return null;
    }

    @Override
    public String visitPrintStmt(StmtNode.Print stmt) {
        Object value = evaluate(stmt.value);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public String visitReturnStmt(StmtNode.Return stmt) {
        return null;
    }

    @Override
    public String visitVarStmt(StmtNode.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        } else {
            switch (stmt.type.type) {
                case INT:
                case FLOAT:
                    value = 0;
                    break;
                case STRING:
                    value = "";
                    break;
                case BOOL:
                    value = true;
                    break;
            }
        }
        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public String visitWhileStmt(StmtNode.While stmt) {
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
    public String visitBreakStmt(StmtNode.Break stmt) {
        exitBlock = true;
        breakloop = true;
        return null;
    }

    @Override
    public String visitContinueStmt(StmtNode.Continue stmt) {
        exitBlock = true;
        return null;
    }

    // endregion
}

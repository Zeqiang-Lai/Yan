package interpreter;

import error.ErrorCollector;
import error.RuntimeError;
import frontend.TokenType;
import frontend.ast.ExprNode;
import frontend.ast.StmtNode;

import java.util.List;

import static frontend.TokenType.*;

public class Interpreter implements ExprNode.Visitor<Object>, StmtNode.Visitor<Object> {
    private Environment environment = new Environment();
    private ErrorCollector errorCollector = ErrorCollector.getInstance();
    // region: Interface

    public void interpret(List<StmtNode> statements) {
        for(StmtNode statement : statements) {
            try {
                execute(statement);
            } catch (RuntimeError error) {
                errorCollector.add(error);
            }
        }
    }

    // endregion

    // region: Utils

    private Object evaluate(ExprNode expr) throws RuntimeError {
        return expr.accept(this);
    }

    private void execute(StmtNode stmt) throws RuntimeError {
        stmt.accept(this);
    }

    private String stringify(Object value) {
        return value.toString();
    }

    // endregion

    // region: Expression

    @Override
    public Object visitAssignExpr(ExprNode.Assign expr) {
        return null;
    }

    @Override
    public Object visitBinaryExpr(ExprNode.Binary expr) throws RuntimeError {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        TokenType op = expr.operator.type;

        switch (op) {
            case ADD:
                return (Double)left + (Double)right;
            case SUB:
                return (Double)left - (Double)right;
            case MULTI:
                return (Double)left * (Double)right;
            case DIV:
                return (Double)left / (Double)right;
        }

        return null;
    }

    @Override
    public Object visitCallExpr(ExprNode.FunCall expr) {
        return null;
    }

    @Override
    public Object visitGroupingExpr(ExprNode.Grouping expr) throws RuntimeError {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(ExprNode.Literal expr) {
        Object value = null;
        if(expr.value instanceof Integer) {
            Integer tmp = (Integer)expr.value;
            value = Double.valueOf((double)tmp);
        } else {
            value = expr.value;
        }
        return value;
    }

    @Override
    public Object visitLogicalExpr(ExprNode.Logical expr) throws RuntimeError {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        TokenType op = expr.operator.type;

        switch (op) {
            case REL_AND:
                return (Boolean)left && (Boolean) right;
            case REL_OR:
                return (Boolean)left || (Boolean) right;
        }
        return null;
    }

    @Override
    public Object visitRelationExpr(ExprNode.Relation expr) throws RuntimeError {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        TokenType op = expr.operator.type;

        switch (op) {
            case GREATER:
                return (Double)left > (Double)right;
            case GREATER_EQUAL:
                return (Double)left >= (Double)right;
            case LESS:
                return (Double)left < (Double)right;
            case LESS_EQUAL:
                return (Double)left <= (Double)right;
        }
        return null;
    }

    @Override
    public Object visitUnaryExpr(ExprNode.Unary expr) throws RuntimeError {
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case SUB:
                if(right instanceof Double)
                    right = -(Double)right;
                else if(right instanceof Integer)
                    right = -(Integer)right;
                else
                    throw new RuntimeError(expr.operator,
                            "operand of negative sign should be a number.");
                break;
            case REL_NOT:
                right = !(Boolean)right;
                break;
            default:
                // unreachable
                break;
        }
        return right;
    }

    @Override
    public Object visitVariableExpr(ExprNode.Variable expr) throws RuntimeError {
        return environment.get(expr.name);
    }
    // endregion

    // region: Statement
    @Override
    public String visitBlockStmt(StmtNode.Block stmt) {
        return null;
    }

    @Override
    public String visitEmptyStmt(StmtNode.Empty stmt) {
        return null;
    }

    @Override
    public Object visitExpressionStmt(StmtNode.Expression stmt) throws RuntimeError {
        return evaluate(stmt.expr);
    }

    @Override
    public String visitFunctionStmt(StmtNode.Function stmt) {
        return null;
    }

    @Override
    public String visitIfStmt(StmtNode.If stmt) {
        return null;
    }

    @Override
    public String visitPrintStmt(StmtNode.Print stmt) throws RuntimeError {
        Object value = evaluate(stmt.value);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public String visitReturnStmt(StmtNode.Return stmt) {
        return null;
    }

    @Override
    public String visitVarStmt(StmtNode.Var stmt) throws RuntimeError {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        } else {
            switch(stmt.type.type) {
                case INT: case FLOAT:
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
        return null;
    }

    @Override
    public String visitBreakStmt(StmtNode.Break stmt) {
        return null;
    }

    @Override
    public String visitContinueStmt(StmtNode.Continue stmt) {
        return null;
    }

    // endregion
}

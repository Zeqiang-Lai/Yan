package compiler;

import frontend.DataType;
import frontend.Token;
import frontend.ast.ExprNode;
import frontend.ast.StmtNode;


/**
 * 1. Resolve type of every expression.
 * 2. Resolve every reference of variable.
 */
public class Resolver implements StmtNode.Visitor<DataType>, ExprNode.Visitor<DataType> {

    Scope scope = new Scope();
    Scope.Type scope_type = null;

    private DataType evaluate(ExprNode expr) {
        return expr.accept(this);
    }

    private DataType evaluate(Token name) {
        return null;
    }

    private void execute(StmtNode stmt) {
        stmt.accept(this);
    }

    private DataType checkType(ExprNode left, ExprNode right) {
        DataType left_type = evaluate(left);
        DataType right_type = evaluate(right);
        if(!DataType.computeCompatible(left_type, right_type))
            throw new TypeError();
        return DataType.implicitConversion(left_type, right_type);
    }

    // region expression

    @Override
    public DataType visitAssignExpr(ExprNode.Assign expr) {
        DataType left_type = evaluate(expr.name);
        DataType right_type = evaluate(expr.value);
        if(!DataType.assignCompatible(left_type, right_type))
            throw new TypeError( right_type + "could not be assigned to a " + left_type);
        expr.type = right_type;
        return right_type;
    }

    @Override
    public DataType visitBinaryExpr(ExprNode.Binary expr) {
        DataType type = checkType(expr.left, expr.right);
        expr.type = type;
        return type;
    }

    @Override
    public DataType visitCallExpr(ExprNode.FunCall expr) {
        DataType type;
        for(ExprNode node : expr.arguments) {
            type = evaluate(node);

        }
//        return ;
    }

    @Override
    public DataType visitGroupingExpr(ExprNode.Grouping expr) {
        expr.type = evaluate(expr.expression);
        return expr.type;
    }

    @Override
    public DataType visitLiteralExpr(ExprNode.Literal expr) {
        if(expr.value instanceof Double) return DataType.FLOAT;
        if(expr.value instanceof Integer) return DataType.INT;
        if(expr.value instanceof Boolean) return DataType.BOOL;
        throw new TypeError("type of'"+expr.value+"' is not supported.");
    }

    @Override
    public DataType visitLogicalExpr(ExprNode.Logical expr) {
        DataType left_type = evaluate(expr.left);
        DataType right_type = evaluate(expr.right);
        if(left_type != DataType.BOOL || right_type != DataType.BOOL)
            throw new TypeError();
        expr.type = left_type;
        return expr.type;
    }

    @Override
    public DataType visitRelationExpr(ExprNode.Relation expr) {
        return null;
    }

    @Override
    public DataType visitUnaryExpr(ExprNode.Unary expr) {
        return null;
    }

    @Override
    public DataType visitVariableExpr(ExprNode.Variable expr) {
        return null;
    }

    // endregion

    // region statement

    @Override
    public DataType visitBlockStmt(StmtNode.Block stmt) {
        if(scope_type == null) scope.beginScope(Scope.Type.BLOCK);
        for(StmtNode node : stmt.items)
            node.accept(this);
        if(scope_type == null) scope.endScope();
        return null;
    }

    @Override
    public DataType visitExpressionStmt(StmtNode.Expression stmt) {
        evaluate(stmt.expr);
        return null;
    }

    @Override
    public DataType visitFunctionStmt(StmtNode.Function stmt) {
        this.scope_type = Scope.Type.FUNCTION;
        scope.current.put(stmt.name.lexeme, new Symbol(DataType.FUNCTION, stmt));

        scope.beginScope(Scope.Type.FUNCTION);
        scope_type = Scope.Type.FUNCTION;
        stmt.body.accept(this);
        scope_type = null;
        scope.endScope();

        return null;
    }

    @Override
    public DataType visitIfStmt(StmtNode.If stmt) {
        this.scope_type = Scope.Type.IF;

        stmt.cond.type = evaluate(stmt.cond);

        scope.beginScope(Scope.Type.IF);
        scope_type = Scope.Type.IF;
        stmt.if_body.accept(this);
        scope_type = null;
        scope.endScope();

        if(stmt.else_body != null) {
            scope.beginScope(Scope.Type.IF);
            scope_type = Scope.Type.IF;
            stmt.else_body.accept(this);
            scope_type = null;
            scope.endScope();
        }
        return null;
    }

    @Override
    public DataType visitPrintStmt(StmtNode.Print stmt) {
        stmt.value.type = evaluate(stmt.value);
        return null;
    }

    @Override
    public DataType visitReturnStmt(StmtNode.Return stmt) {
        if(stmt.value != null)
            stmt.value.type = evaluate(stmt.value);
        return null;
    }

    @Override
    public DataType visitVarStmt(StmtNode.Var stmt) {
        return null;
    }

    @Override
    public DataType visitWhileStmt(StmtNode.While stmt) {
        this.scope_type = Scope.Type.LOOP;
        stmt.cond.type = evaluate(stmt.cond);

        scope.beginScope(Scope.Type.LOOP);
        scope_type = Scope.Type.LOOP;
        stmt.body.accept(this);
        scope_type = null;
        scope.endScope();

        return null;
    }

    @Override
    public DataType visitBreakStmt(StmtNode.Break stmt) {
        return null;
    }

    @Override
    public DataType visitContinueStmt(StmtNode.Continue stmt) {
        return null;
    }

    @Override
    public DataType visitEmptyStmt(StmtNode.Empty stmt) {
        return null;
    }
    // endregion
}

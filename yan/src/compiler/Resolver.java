package compiler;

import frontend.DataType;
import frontend.ast.ExprNode;
import frontend.ast.StmtNode;


/**
 * 1. Resolve type of every expression.
 * 2. Resolve every reference of variable.
 */
public class Resolver implements StmtNode.Visitor<DataType>, ExprNode.Visitor<DataType> {

    // region expression

    @Override
    public DataType visitAssignExpr(ExprNode.Assign expr) {
        return null;
    }

    @Override
    public DataType visitBinaryExpr(ExprNode.Binary expr) {
        return null;
    }

    @Override
    public DataType visitCallExpr(ExprNode.FunCall expr) {
        return null;
    }

    @Override
    public DataType visitGroupingExpr(ExprNode.Grouping expr) {
        return null;
    }

    @Override
    public DataType visitLiteralExpr(ExprNode.Literal expr) {
        return null;
    }

    @Override
    public DataType visitLogicalExpr(ExprNode.Logical expr) {
        return null;
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
        return null;
    }

    @Override
    public DataType visitEmptyStmt(StmtNode.Empty stmt) {
        return null;
    }

    @Override
    public DataType visitExpressionStmt(StmtNode.Expression stmt) {
        return null;
    }

    @Override
    public DataType visitFunctionStmt(StmtNode.Function stmt) {
        return null;
    }

    @Override
    public DataType visitIfStmt(StmtNode.If stmt) {
        return null;
    }

    @Override
    public DataType visitPrintStmt(StmtNode.Print stmt) {
        return null;
    }

    @Override
    public DataType visitReturnStmt(StmtNode.Return stmt) {
        return null;
    }

    @Override
    public DataType visitVarStmt(StmtNode.Var stmt) {
        return null;
    }

    @Override
    public DataType visitWhileStmt(StmtNode.While stmt) {
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

    // endregion
}

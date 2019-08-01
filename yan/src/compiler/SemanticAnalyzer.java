package compiler;

import frontend.ast.ExprNode;
import frontend.ast.StmtNode;

public class SemanticAnalyzer implements StmtNode.Visitor<Object>, ExprNode.Visitor<Object> {

    @Override
    public Object visitAssignExpr(ExprNode.Assign expr) {
        return null;
    }

    @Override
    public Object visitBinaryExpr(ExprNode.Binary expr) {
        return null;
    }

    @Override
    public Object visitCallExpr(ExprNode.FunCall expr) {
        return null;
    }

    @Override
    public Object visitGroupingExpr(ExprNode.Grouping expr) {
        return null;
    }

    @Override
    public Object visitLiteralExpr(ExprNode.Literal expr) {
        return null;
    }

    @Override
    public Object visitLogicalExpr(ExprNode.Logical expr) {
        return null;
    }

    @Override
    public Object visitRelationExpr(ExprNode.Relation expr) {
        return null;
    }

    @Override
    public Object visitUnaryExpr(ExprNode.Unary expr) {
        return null;
    }

    @Override
    public Object visitVariableExpr(ExprNode.Variable expr) {
        return null;
    }

    @Override
    public Object visitBlockStmt(StmtNode.Block stmt) {
        return null;
    }

    @Override
    public Object visitEmptyStmt(StmtNode.Empty stmt) {
        return null;
    }

    @Override
    public Object visitExpressionStmt(StmtNode.Expression stmt) {
        return null;
    }

    @Override
    public Object visitFunctionStmt(StmtNode.Function stmt) {
        return null;
    }

    @Override
    public Object visitIfStmt(StmtNode.If stmt) {
        return null;
    }

    @Override
    public Object visitPrintStmt(StmtNode.Print stmt) {
        return null;
    }

    @Override
    public Object visitReturnStmt(StmtNode.Return stmt) {
        return null;
    }

    @Override
    public Object visitVarStmt(StmtNode.Var stmt) {
        return null;
    }

    @Override
    public Object visitWhileStmt(StmtNode.While stmt) {
        return null;
    }

    @Override
    public Object visitBreakStmt(StmtNode.Break stmt) {
        return null;
    }

    @Override
    public Object visitContinueStmt(StmtNode.Continue stmt) {
        return null;
    }
}

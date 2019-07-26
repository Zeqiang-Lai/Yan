package frontend.ast;

import frontend.Token;

import java.util.List;

public class ExprNode {
    interface Visitor<R> {
        R visitAssignExpr(Assign expr);
        R visitBinaryExpr(Binary expr);
        R visitCallExpr(Call expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitLogicalExpr(Logical expr);
        R visitUnaryExpr(Unary expr);
        R visitVariableExpr(Variable expr);
    }

    static class Assign extends ExprNode {
        Assign(Token name, ExprNode value) {
            this.name = name;
            this.value = value;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }

        final Token name;
        final ExprNode value;
    }

    static class Binary extends ExprNode {
        Binary(ExprNode left, Token operator, ExprNode right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        final ExprNode left;
        final Token operator;
        final ExprNode right;
    }

    static class Call extends ExprNode {
        Call(ExprNode callee, Token paren, List<ExprNode> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }

        final ExprNode callee;
        final Token paren;
        final List<ExprNode> arguments;
    }

    static class Grouping extends ExprNode {
        Grouping(ExprNode expression) {
            this.expression = expression;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        final ExprNode expression;
    }

    static class Literal extends ExprNode {
        Literal(Object value) {
            this.value = value;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final Object value;
    }

    static class Logical extends ExprNode {
        Logical(ExprNode left, Token operator, ExprNode right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }

        final ExprNode left;
        final Token operator;
        final ExprNode right;
    }

    static class Unary extends ExprNode {
        Unary(Token operator, ExprNode right) {
            this.operator = operator;
            this.right = right;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        final Token operator;
        final ExprNode right;
    }

    static class Variable extends ExprNode {
        Variable(Token name) {
            this.name = name;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        final Token name;
    }
}

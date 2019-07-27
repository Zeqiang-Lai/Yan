package frontend.ast;

import frontend.Token;

import java.util.List;

public class ExprNode {
    interface Visitor<R> {
        R visitAssignExpr(Assign expr);
        R visitBinaryExpr(Binary expr);
        R visitCallExpr(FunCall expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitLogicalExpr(Logical expr);
        R visitRelationExpr(Relation expr);
        R visitUnaryExpr(Unary expr);
        R visitVariableExpr(Variable expr);
    }

    public static class Assign extends ExprNode {
        public Assign(Token name, Token type, ExprNode value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }

        final Token name;
        final Token type;
        final ExprNode value;
    }

    public static class Binary extends ExprNode {
        public Binary(ExprNode left, Token operator, ExprNode right) {
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

    public static class Relation extends ExprNode {
        public Relation(ExprNode left, Token operator, ExprNode right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitRelationExpr(this);
        }

        final ExprNode left;
        final Token operator;
        final ExprNode right;
    }

    public static class FunCall extends ExprNode {
        public FunCall(ExprNode callee, Token paren, List<ExprNode> arguments) {
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

    public static class Grouping extends ExprNode {
        public Grouping(ExprNode expression) {
            this.expression = expression;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        final ExprNode expression;
    }

    public static class Literal extends ExprNode {
        public Literal(Object value) {
            this.value = value;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final Object value;
    }

    public static class Logical extends ExprNode {
        public Logical(ExprNode left, Token operator, ExprNode right) {
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

    public static class Unary extends ExprNode {
        public Unary(Token operator, ExprNode right) {
            this.operator = operator;
            this.right = right;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        final Token operator;
        final ExprNode right;
    }

    public static class Variable extends ExprNode {
        public Variable(Token name) {
            this.name = name;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        public final Token name;
    }
}

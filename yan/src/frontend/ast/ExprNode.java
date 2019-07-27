package frontend.ast;

import error.RuntimeError;
import frontend.Token;

import java.util.List;

public abstract class ExprNode {

    public interface Visitor<R> {
        R visitAssignExpr(Assign expr);
        R visitBinaryExpr(Binary expr) throws RuntimeError;
        R visitCallExpr(FunCall expr);
        R visitGroupingExpr(Grouping expr) throws RuntimeError;
        R visitLiteralExpr(Literal expr);
        R visitLogicalExpr(Logical expr) throws RuntimeError;
        R visitRelationExpr(Relation expr) throws RuntimeError;
        R visitUnaryExpr(Unary expr) throws RuntimeError;
        R visitVariableExpr(Variable expr) throws RuntimeError;
    }
    public abstract <R> R accept(Visitor<R> visitor) throws RuntimeError;

    public static class Assign extends ExprNode {
        public Assign(Token name, Token type, ExprNode value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }

        public final Token name;
        public final Token type;
        public final ExprNode value;
    }

    public static class Binary extends ExprNode {
        public Binary(ExprNode left, Token operator, ExprNode right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public <R> R accept(Visitor<R> visitor) throws RuntimeError {
            return visitor.visitBinaryExpr(this);
        }

        public final ExprNode left;
        public final Token operator;
        public final ExprNode right;
    }

    public static class Relation extends ExprNode {
        public Relation(ExprNode left, Token operator, ExprNode right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public <R> R accept(Visitor<R> visitor) throws RuntimeError {
            return visitor.visitRelationExpr(this);
        }

        public final ExprNode left;
        public final Token operator;
        public final ExprNode right;
    }

    public static class FunCall extends ExprNode {
        public FunCall(ExprNode callee, Token paren, List<ExprNode> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }

        public final ExprNode callee;
        public final Token paren;
        public final List<ExprNode> arguments;
    }

    public static class Grouping extends ExprNode {
        public Grouping(ExprNode expression) {
            this.expression = expression;
        }

        public <R> R accept(Visitor<R> visitor) throws RuntimeError {
            return visitor.visitGroupingExpr(this);
        }

        public final ExprNode expression;
    }

    public static class Literal extends ExprNode {
        public Literal(Object value) {
            this.value = value;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        public final Object value;
    }

    public static class Logical extends ExprNode {
        public Logical(ExprNode left, Token operator, ExprNode right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public  <R> R accept(Visitor<R> visitor) throws RuntimeError {
            return visitor.visitLogicalExpr(this);
        }

        public final ExprNode left;
        public final Token operator;
        public final ExprNode right;
    }

    public static class Unary extends ExprNode {
        public Unary(Token operator, ExprNode right) {
            this.operator = operator;
            this.right = right;
        }

        public <R> R accept(Visitor<R> visitor) throws RuntimeError {
            return visitor.visitUnaryExpr(this);
        }

        public final Token operator;
        public final ExprNode right;
    }

    public static class Variable extends ExprNode {
        public Variable(Token name) {
            this.name = name;
        }

        public <R> R accept(Visitor<R> visitor) throws RuntimeError {
            return visitor.visitVariableExpr(this);
        }

        public final Token name;
    }
}

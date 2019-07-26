package frontend.ast;

import frontend.Token;

import java.util.List;
import java.util.Vector;

public abstract class StmtNode {
    interface Visitor<R> {
        R visitBlockStmt(Block stmt);
        R visitEmptyStmt(Empty stmt);
        R visitExpressionStmt(Expression stmt);
        R visitFunctionStmt(Function stmt);
        R visitIfStmt(If stmt);
//        R visitPrintStmt(Print stmt);
        R visitReturnStmt(Return stmt);
        R visitVarStmt(Var stmt);
        R visitWhileStmt(While stmt);
        R visitBreakStmt(Break stmt);
        R visitContinueStmt(Continue stmt);
    }

    abstract <R> R accept(Visitor<R> visitor);

    static public class Function extends StmtNode {
        final Token name;
        final List<Token> params;
        final List<Token> types;
        final Block body;

        public Function(Token name, List<Token> params, List<Token> types, Block body) {
            this.name = name;
            this.params = params;
            this.types = types;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }
    }

    static public class Var extends StmtNode {
        final Token name;
        final ExprNode initializer;
        final Token type;

        public Var(Token name, ExprNode initializer, Token type) {
            this.name = name;
            this.initializer = initializer;
            this.type = type;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    public static class Block extends StmtNode {
        final List<StmtNode> items;

        public Block(List<StmtNode> items) {
            this.items = items;
        }

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }

    public static class If extends StmtNode {
        ExprNode cond;
        StmtNode if_body;
        StmtNode else_body;

        public If(ExprNode cond, StmtNode if_body, StmtNode else_body) {
            this.cond = cond;
            this.if_body = if_body;
            this.else_body = else_body;
        }

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    public static class While extends StmtNode {
        ExprNode cond;
        StmtNode body;

        public While(ExprNode cond, StmtNode body) {
            this.cond = cond;
            this.body = body;
        }

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }

    public static class Empty extends StmtNode {
        public Empty() {}

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitEmptyStmt(this);
        }
    }

    public static class Return extends StmtNode {
        ExprNode value;

        public Return(ExprNode value) {
            this.value = value;
        }

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }
    }

    public static class Break extends StmtNode {
        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitBreakStmt(this);
        }
    }

    public static class Continue extends StmtNode {
        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitContinueStmt(this);
        }
    }

    public static class Expression extends StmtNode {
        ExprNode expr;

        public Expression(ExprNode expr) {
            this.expr = expr;
        }

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

}

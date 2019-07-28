package frontend.ast;

import error.RuntimeError;
import frontend.Token;

import java.util.List;
import java.util.Vector;

public abstract class StmtNode {
    public interface Visitor<R> {
        R visitBlockStmt(Block stmt);

        R visitEmptyStmt(Empty stmt);

        R visitExpressionStmt(Expression stmt);

        R visitFunctionStmt(Function stmt);

        R visitIfStmt(If stmt);

        R visitPrintStmt(Print stmt);

        R visitReturnStmt(Return stmt);

        R visitVarStmt(Var stmt);

        R visitWhileStmt(While stmt);

        R visitBreakStmt(Break stmt);

        R visitContinueStmt(Continue stmt);
    }

    public abstract <R> R accept(Visitor<R> visitor);

    static public class Print extends StmtNode {
        public ExprNode value;

        public Print(ExprNode value) {
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }

    static public class Function extends StmtNode {
        public final Token name;
        public final List<Token> params;
        public final List<Token> types;
        public final Token return_type;
        public final Block body;

        public Function(Token name, List<Token> params, List<Token> types, Token return_type, Block body) {
            this.name = name;
            this.params = params;
            this.types = types;
            this.return_type = return_type;
            this.body = body;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }
    }

    static public class Var extends StmtNode {
        public final Token name;
        public final ExprNode initializer;
        public final Token type;

        public Var(Token name, ExprNode initializer, Token type) {
            this.name = name;
            this.initializer = initializer;
            this.type = type;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    public static class Block extends StmtNode {
        public final List<StmtNode> items;

        public Block(List<StmtNode> items) {
            this.items = items;
        }

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }

    public static class If extends StmtNode {
        public ExprNode cond;
        public StmtNode if_body;
        public StmtNode else_body;

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
        public ExprNode cond;
        public StmtNode body;

        public While(ExprNode cond, StmtNode body) {
            this.cond = cond;
            this.body = body;
        }

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }

    public static class Empty extends StmtNode {
        public Empty() {
        }

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitEmptyStmt(this);
        }
    }

    public static class Return extends StmtNode {
        public ExprNode value;

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
        public ExprNode expr;

        public Expression(ExprNode expr) {
            this.expr = expr;
        }

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

}

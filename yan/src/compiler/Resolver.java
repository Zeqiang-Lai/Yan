package compiler;

import compiler.error.NameError;
import compiler.error.TypeError;
import error.CompilerError;
import frontend.DataType;
import frontend.Token;
import frontend.ast.ExprNode;
import frontend.ast.StmtNode;

import javax.xml.crypto.Data;


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

    private DataType checkType(ExprNode left, ExprNode right, String err_msg) {
        DataType left_type = evaluate(left);
        DataType right_type = evaluate(right);
        if(!DataType.computeCompatible(left_type, right_type))
            throw new TypeError(err_msg);
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
        DataType type = checkType(expr.left, expr.right,
                "operand of binary expression should be number.");
        expr.type = type;
        return expr.type;
    }

    @Override
    public DataType visitCallExpr(ExprNode.FunCall expr) {
        String func_name = expr.paren.lexeme;

        Symbol symbol = scope.current.get(func_name);
        StmtNode.Function func;
        if(symbol == null)
            throw new NameError(func_name, false);
        if(symbol.type != DataType.FUNCTION)
            throw new TypeError("'" + symbol.type + "'object is not callable");
        func = (StmtNode.Function)symbol.value;

        if(expr.arguments.size() != func.params.size())
            throw new TypeError(func_name + "() takes " + func.params.size() +
                    " positional argument but " + expr.arguments.size() + " were given");

        DataType arg_type, param_type;
        for(int i=0; i<expr.arguments.size(); i++) {
            arg_type = evaluate(expr.arguments.get(i));
            param_type = DataType.tokenType2DataType.get(func.types.get(i).type);
            if(arg_type != param_type)
                throw new TypeError("expected " + param_type + " for " +
                        i + "th parameter of " + func_name + "(), but " + arg_type + " were given");
        }
        return DataType.tokenType2DataType.get(func.return_type.type);
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
            throw new TypeError("operand of logical expression should be boolean.");
        expr.type = left_type;
        return expr.type;
    }

    @Override
    public DataType visitRelationExpr(ExprNode.Relation expr) {
        checkType(expr.left, expr.right,
                "operand of relational expression should be number.");
        expr.type = DataType.BOOL;
        return expr.type;
    }

    @Override
    public DataType visitUnaryExpr(ExprNode.Unary expr) {
        expr.type = evaluate(expr.right);
        return expr.type;
    }

    @Override
    public DataType visitVariableExpr(ExprNode.Variable expr) {
        Symbol symbol = scope.current.get(expr.name.lexeme);
        if(symbol == null)
            throw new NameError(expr.name.lexeme, false);
        return symbol.type;
    }

    // endregion

    // region statement

    @Override
    public DataType visitBlockStmt(StmtNode.Block stmt) {
        if(scope_type == null) scope.beginScope(Scope.Type.BLOCK);
        for(StmtNode node : stmt.items)
            execute(node);
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
        execute(stmt.body);
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
        execute(stmt.if_body);
        scope_type = null;
        scope.endScope();

        if(stmt.else_body != null) {
            scope.beginScope(Scope.Type.IF);
            scope_type = Scope.Type.IF;
            execute(stmt.else_body);
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
        if(scope.current.get(stmt.name.lexeme) != null)
            throw new NameError(stmt.name.lexeme, true);

        DataType type = DataType.tokenType2DataType.get(stmt.type.type);
        DataType init_type = evaluate(stmt.initializer);

        if(!DataType.assignCompatible(type, init_type))
            throw new TypeError(init_type + " can not be assigned to " + type);

        scope.current.put(stmt.name.lexeme, new Symbol(type, stmt));
        return null;
    }

    @Override
    public DataType visitWhileStmt(StmtNode.While stmt) {
        this.scope_type = Scope.Type.LOOP;
        stmt.cond.type = evaluate(stmt.cond);

        scope.beginScope(Scope.Type.LOOP);
        scope_type = Scope.Type.LOOP;
        execute(stmt.body);
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

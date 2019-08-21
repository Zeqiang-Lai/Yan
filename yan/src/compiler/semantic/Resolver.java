package compiler.semantic;

import compiler.error.SyntaxError;
import compiler.error.TypeError;
import error.BaseError;
import error.ErrorCollector;
import frontend.DataType;
import frontend.Token;
import frontend.ast.ExprNode;
import frontend.ast.StmtNode;


/**
 * 1. Resolve type of every expression.
 * 2. Resolve every reference of variable.
 * 3. Resolve break, continue
 * 4. Resolve return
 */
public class Resolver implements StmtNode.Visitor<DataType>, ExprNode.Visitor<DataType> {
    private ErrorCollector errorCollector = ErrorCollector.getInstance();

    private NestedScope scopes = new NestedScope();
    // used to distinguish scope of individual block and blocks in if and while
    private Scope.Type scope_type = null;

    private DataType evaluate(ExprNode expr) {
        return expr.accept(this);
    }

    private DataType evaluate(Token name) {
        return scopes.get(name.lexeme).type;
    }

    public void execute(StmtNode stmt) {
        try {
            stmt.accept(this);
        }catch (BaseError error) {
            errorCollector.add(error);
        }
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
            throw new TypeError( right_type + " could not be assigned to a " + left_type);
        expr.type = right_type;
        return expr.type;
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
        String func_name = expr.name.lexeme;

        Symbol symbol = scopes.get(func_name);
        if(symbol.type != DataType.FUNCTION)
            throw new TypeError("'" + symbol.type + "'object is not callable");

        StmtNode.Function func = (StmtNode.Function)symbol.value;
        expr.func = func;

        if(expr.arguments.size() != func.params.size())
            throw new TypeError(func_name + "() takes " + func.params.size() +
                    " positional argument but " + expr.arguments.size() + " were given");

        DataType arg_type, param_type;
        for(int i=0; i<expr.arguments.size(); i++) {
            arg_type = evaluate(expr.arguments.get(i));
            param_type = func.types.get(i);
            if(arg_type != param_type)
                throw new TypeError("expected " + param_type + " for " +
                        i + "th parameter of " + func_name + "(), but " + arg_type + " were given");
        }
        expr.type = func.return_type;
        return expr.type;
    }

    @Override
    public DataType visitGroupingExpr(ExprNode.Grouping expr) {
        expr.type = evaluate(expr.expression);
        return expr.type;
    }

    @Override
    public DataType visitLiteralExpr(ExprNode.Literal expr) {
        DataType type = null;
        if(expr.value instanceof Double) type = DataType.FLOAT;
        if(expr.value instanceof Integer) type = DataType.INT;
        if(expr.value instanceof Boolean) type = DataType.BOOL;

        if(type == null)
            throw new TypeError("type of'"+expr.value+"' is not supported.");
        expr.type = type;
        return expr.type;
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
        Symbol symbol = scopes.get(expr.name.lexeme);

        if(!(symbol.value instanceof StmtNode.Var))
            throw new TypeError(expr.name.lexeme + " is not a variable.");

        expr.declaration = (StmtNode.Var) symbol.value;
        expr.type = expr.declaration.type;
        return expr.type;
    }

    // endregion

    // region statement

    @Override
    public DataType visitBlockStmt(StmtNode.Block stmt) {
        if(scope_type == null) scopes.beginScope(stmt, Scope.Type.BLOCK);
        for(StmtNode node : stmt.items) {
            execute(node);
        }
        // FIXME: bad implementation, it is better to put endscope, beginscope in the same function.
        scopes.endScope();
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
        scopes.current.put(stmt.name.lexeme, new Symbol(DataType.FUNCTION, stmt));

        scopes.beginScope(stmt, Scope.Type.FUNCTION);
        scope_type = Scope.Type.FUNCTION;

        // Add arguments to current scope.
        // FIXME: bad implementation?
        for(int i=0; i<stmt.params.size(); i++) {
            DataType type = stmt.types.get(i);
            scopes.current.put(stmt.params.get(i).lexeme, new Symbol(type, new StmtNode.Var(stmt.params.get(i), null, type)));
        }
        // Execute body
        execute(stmt.body);

        scope_type = null;
//        scope.endScope();

        return null;
    }

    @Override
    public DataType visitIfStmt(StmtNode.If stmt) {
        this.scope_type = Scope.Type.IF;

        stmt.cond.type = evaluate(stmt.cond);

        scopes.beginScope(stmt.if_body, Scope.Type.IF);
        scope_type = Scope.Type.IF;
        execute(stmt.if_body);
        scope_type = null;
//        scope.endScope();

        if(stmt.else_body != null) {
            this.scope_type = Scope.Type.ELSE;
            scopes.beginScope(stmt.else_body, Scope.Type.ELSE);
            scope_type = Scope.Type.ELSE;
            execute(stmt.else_body);
            scope_type = null;
//            scope.endScope();
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

        // type of return value should match the return type in function definition.
        Scope scope = scopes.find(Scope.Type.FUNCTION);
        if(scope == null)
            throw new SyntaxError("'return' outside function");

        StmtNode.Function func = (StmtNode.Function) scope.code;
        if(func.return_type != stmt.value.type)
            throw new TypeError("return type of function '"+func.name.lexeme +
                    "' is " + func.return_type + ", but " + stmt.value.type + " were given.");

        stmt.func = scope.code;
        return null;
    }

    @Override
    public DataType visitVarStmt(StmtNode.Var stmt) {
        // TODO: it is better to check if var has already defined before resolving declaration.
        // Feature: type inference by initializer
        DataType type;
        DataType init_type = null;
        if(stmt.initializer != null) init_type = evaluate(stmt.initializer);
        if(stmt.type == null) type = init_type;
        else type = stmt.type;

        // a little tricky here, if there is no initializer, then we must explicit declare type.
        // so type should not be null.
        if(type == null)
            throw new TypeError("can not inference type of " + stmt.name.lexeme);

        if(init_type != null && !DataType.assignCompatible(type, init_type))
            throw new TypeError(init_type + " can not be assigned to " + type);

        stmt.type = type;

        scopes.current.put(stmt.name.lexeme, new Symbol(type, stmt));
        return null;
    }

    @Override
    public DataType visitWhileStmt(StmtNode.While stmt) {
        stmt.cond.type = evaluate(stmt.cond);

        scopes.beginScope(stmt, Scope.Type.LOOP);
        scope_type = Scope.Type.LOOP;
        execute(stmt.body);
        scope_type = null;
//        scope.endScope();

        return null;
    }

    @Override
    public DataType visitBreakStmt(StmtNode.Break stmt) {
        Scope scope = scopes.find(Scope.Type.LOOP);
        if(scope == null)
            throw new SyntaxError("'break' outside loop");
        stmt.loop = (StmtNode.While) scope.code;
        return null;
    }

    @Override
    public DataType visitContinueStmt(StmtNode.Continue stmt) {
        Scope scope = scopes.find(Scope.Type.LOOP);
        if(scope == null)
            throw new SyntaxError("'continue' not properly in loop");
        stmt.loop = (StmtNode.While) scope.code;
        return null;
    }

    @Override
    public DataType visitEmptyStmt(StmtNode.Empty stmt) {
        // TODO: warning
        return null;
    }
    // endregion
}

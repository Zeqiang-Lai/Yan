package compiler;

import frontend.DataType;
import frontend.Token;
import frontend.TokenType;
import frontend.ast.ExprNode;
import frontend.ast.StmtNode;

import javax.xml.crypto.Data;
import java.util.Vector;

public class ILGen implements StmtNode.Visitor<Object>, ExprNode.Visitor<String> {

    // region Inner Class
    // TODO: redundant
    static class ILVar {
        DataType type;
        String name;
        Object init_value;

        boolean is_param;
        boolean is_constant;

        public ILVar(DataType type, String name, Object init_value, boolean is_param, boolean is_constant) {
            this.type = type;
            this.name = name;
            this.init_value = init_value;
            this.is_param = is_param;
            this.is_constant = is_constant;
        }

        public boolean isString() {
            return type == DataType.STRING;
        }

        public boolean isArithm() {
            if(is_constant)
                return false;
            return !isString();
        }

        @Override
        public String toString() {
            if(is_constant)
                return name + ": " + type + " = " + init_value + " [constant]";
            if(is_param)
                return name + ": " + type + " [parameter]";
            return name + ": " + type + " = " + init_value;
        }
    }

    static class ILFunction {
        Vector<Command> commands = new Vector<>();
        Vector<ILVar> vars = new Vector<>();
        Vector<DataType> param_types;
        DataType return_type;
        String name;

        public ILFunction(String name, Vector<DataType> param_types, DataType return_type) {
            this.name = name;
            this.param_types = param_types;
            this.return_type = return_type;
        }

        public Vector<ILVar> getParameters() {
            return null;
        }

        public Vector<ILVar> getLocalVar() {
            return null;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("func ").append(name).append('(');
            if(param_types != null) {
                boolean first = true;
                for (DataType type : param_types) {
                    if (first) {
                        builder.append(type);
                        first = false;
                    } else builder.append(" ,").append(type);
                }
            }
            builder.append(")");
            if(return_type != null)
                builder.append(" -> ").append(return_type);
            return builder.toString();
        }

        public String toText() {
            StringBuilder builder = new StringBuilder();
            builder.append("[Begin Function ").append(name).append("]\n");
            builder.append("Signature: ").append(toString()).append('\n');
            builder.append("\n").append(".Data").append('\n');
            for(ILVar var : vars)
                builder.append(var).append('\n');

            builder.append("\n").append(".Text").append('\n');
            for(Command cmd : commands)
                builder.append(cmd).append('\n');
            builder.append('\n').append("[End Function ").append(name).append("]\n");
            return builder.toString();
        }
    }

    static class Command {
        ILOP op;
        String arg1;
        String arg2;
        String result;

        public Command(ILOP op, String arg1, String arg2, String result) {
            this.op = op;
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.result = result;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(op).append(' ');

            if(arg1 != null)
                builder.append(arg1).append(", ");
            if(arg2 != null)
                builder.append(arg2).append(", ");
            if(result != null)
                builder.append(result);
            if(builder.charAt(builder.length()-1) == ' ')
                builder.delete(builder.length()-2, builder.length()-1);
            return builder.toString();
        }
    }
    // endregion

    // region Global Counting Variable
    private int label_count = 0;
    private int tmp_count = 0;

    private String get_new_label() {
        label_count += 1;
        return "@label" + label_count;
    }

    private String get_new_tmp() {
        tmp_count += 1;
        return "@tmp" + tmp_count;
    }

    // endregion

    public Vector<ILFunction> functions = new Vector<>();
    private ILFunction current_func;

    public ILGen() {
        functions.add(new ILFunction("@global",null, null));
        current_func = functions.lastElement();
    }

    public String get_il_code() {
        StringBuilder builder = new StringBuilder();
        for(ILFunction func : functions)
            builder.append(func.toText()).append('\n');
        return builder.toString();
    }

    // region Utils
    private String emit(ILOP op, String arg1, String arg2) {
        String result = get_new_tmp();
        current_func.commands.add(new Command(op, arg1, arg2, result));
        return result;
    }

    private String emit(ILOP op, String arg1, String arg2, String result) {
        current_func.commands.add(new Command(op, arg1, arg2, result));
        return result;
    }

    private void emit(ILOP op, String arg) {
        current_func.commands.add(new Command(op, arg, null, null));
    }

    private String gen(ExprNode node) {
        return node.accept(this);
    }

    public void gen(StmtNode node) {
         node.accept(this);
    }

    private String gen_type_conversion(String data, DataType from, DataType to) {
        if(from == to)
            return data;
        ILOP op = ILOP.getConvertOP(from, to);
        return emit(op, data, null);
    }

    private boolean isVar(String x) {
        return x.startsWith("@tmp");
    }
    // endregion

    // region Expression
    // every visit method for expression will return the name of the result,

    @Override
    public String visitAssignExpr(ExprNode.Assign expr) {
        String value = gen(expr.value);
        value = gen_type_conversion(value, expr.value.type, expr.type);
        return emit(ILOP.assign, value, null, expr.name.lexeme);
    }

    @Override
    public String visitBinaryExpr(ExprNode.Binary expr) {
        String left = gen(expr.left);
        left = gen_type_conversion(left, expr.left.type, expr.type);
        String right = gen(expr.right);
        right = gen_type_conversion(right, expr.right.type, expr.type);

        return emit(ILOP.valueOf(expr.operator.type),left, right);
    }

    @Override
    public String visitCallExpr(ExprNode.FunCall expr) {
        String name;
        for(int i=0; i<expr.arguments.size(); i++) {
            name = gen(expr.arguments.get(i));
            name = gen_type_conversion(name, expr.arguments.get(i).type, expr.func.types.get(i));
            emit(ILOP.param, name, null, null);
        }
        return emit(ILOP.call, expr.name.lexeme, String.valueOf(expr.arguments.size()));
    }

    @Override
    public String visitGroupingExpr(ExprNode.Grouping expr) {
        return gen(expr.expression);
    }

    @Override
    public String visitLiteralExpr(ExprNode.Literal expr) {
        // TODO: temp
        String name = get_new_tmp();
        current_func.vars.add(new ILVar(expr.type, name, expr.value, false, true));
        return name;
//        return expr.value.toString();
    }

    @Override
    public String visitLogicalExpr(ExprNode.Logical expr) {
        String left = gen(expr.left);
        left = gen_type_conversion(left, expr.left.type, expr.type);
        String right = gen(expr.right);
        right = gen_type_conversion(right, expr.right.type, expr.type);
        return emit(ILOP.valueOf(expr.operator.type),left, right);
    }

    @Override
    public String visitRelationExpr(ExprNode.Relation expr) {
        String left = gen(expr.left);
        left = gen_type_conversion(left, expr.left.type, expr.type);
        String right = gen(expr.right);
        right = gen_type_conversion(right, expr.right.type, expr.type);
        return emit(ILOP.valueOf(expr.operator.type),left, right);
    }

    @Override
    public String visitUnaryExpr(ExprNode.Unary expr) {
        String right = gen(expr.right);
        right = gen_type_conversion(right, expr.right.type, expr.type);
        if(expr.operator.type == TokenType.REL_NOT)
            return emit(ILOP.not, right, null);
        else if(expr.operator.type == TokenType.SUB)
            return emit(ILOP.neg, right, null);
        else
            throw new RuntimeException("Invalid unary operator" + expr.operator);
    }

    @Override
    public String visitVariableExpr(ExprNode.Variable expr) {
        return expr.name.lexeme;
    }

    // endregion

    // region Statement

    @Override
    public Object visitBlockStmt(StmtNode.Block stmt) {
        for(StmtNode st : stmt.items)
            gen(st);
        return null;
    }

    @Override
    public Object visitEmptyStmt(StmtNode.Empty stmt) {
        return null;
    }

    @Override
    public Object visitExpressionStmt(StmtNode.Expression stmt) {
        gen(stmt.expr);
        return null;
    }

    @Override
    public Object visitFunctionStmt(StmtNode.Function stmt) {
        Vector<DataType> types = new Vector<>(stmt.types);
        functions.add(new ILFunction(stmt.name.lexeme, types, stmt.return_type));
        current_func = functions.lastElement();

        // add parameters
        for(int i=0; i<stmt.params.size(); i++) {
            current_func.vars.add(new ILVar(stmt.types.get(i), stmt.params.get(i).lexeme,
                    null, true, false));
        }

        gen(stmt.body);
        return null;
    }

    @Override
    public Object visitIfStmt(StmtNode.If stmt) {
        stmt.label_before_else = get_new_label();
        stmt.label_after_else = get_new_label();

        String cond = gen(stmt.cond);
        emit(ILOP.jf, cond, null, stmt.label_before_else);

        gen(stmt.if_body);
        if(stmt.else_body != null)
            emit(ILOP.jmp, null, null, stmt.label_after_else);

        emit(ILOP.label, stmt.label_before_else);

        if(stmt.else_body != null) {
            gen(stmt.else_body);
            emit(ILOP.label, stmt.label_after_else);
        }
        return null;
    }

    @Override
    public Object visitPrintStmt(StmtNode.Print stmt) {
        String result = gen(stmt.value);
        emit(ILOP.print, result);
        return null;
    }

    @Override
    public Object visitReturnStmt(StmtNode.Return stmt) {
        String result = null;
        if(stmt.value != null)
            result = gen(stmt.value);
        emit(ILOP.ret, result);
        return result;
    }

    @Override
    public Object visitVarStmt(StmtNode.Var stmt) {
        String result = null;
        if(stmt.initializer != null)
            result = gen(stmt.initializer);

        // we don't generate assign op for constant initialization
        if(result != null && !current_func.vars.lastElement().is_constant)
            emit(ILOP.assign, result, null, stmt.name.lexeme);

        current_func.vars.add(new ILVar(stmt.type, stmt.name.lexeme, result, false, false));
        return null;
    }

    @Override
    public Object visitWhileStmt(StmtNode.While stmt) {
        stmt.label_before_while = get_new_label();
        stmt.label_after_while = get_new_label();

        emit(ILOP.label, stmt.label_before_while);

        String cond = gen(stmt.cond);
        emit(ILOP.jf, cond, null, stmt.label_after_while);

        gen(stmt.body);

        emit(ILOP.jmp, stmt.label_before_while);
        emit(ILOP.label, stmt.label_after_while);
        return null;
    }

    @Override
    public Object visitBreakStmt(StmtNode.Break stmt) {
        emit(ILOP.jmp, stmt.loop.label_after_while);
        return null;
    }

    @Override
    public Object visitContinueStmt(StmtNode.Continue stmt) {
        emit(ILOP.jmp, stmt.loop.label_before_while);
        return null;
    }

    // endregion
}

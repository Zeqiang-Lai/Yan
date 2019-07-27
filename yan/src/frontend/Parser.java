package frontend;

import error.ErrorCollector;
import error.ParseError;
import frontend.ast.ExprNode;
import frontend.ast.StmtNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static error.ParseError.ErrorType.*;
import static frontend.TokenType.*;

public class Parser {
    // region Properties

    private List<Token> tokens;
    private int current;
    private ErrorCollector errorCollector = ErrorCollector.getInstance();

    // endregion

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
    }

    public List<StmtNode> parse() {
        List<StmtNode> statements = new ArrayList<>();
        while (!isAtEnd()) {
            try {
                statements.add(parseDeclaration());
            } catch (ParseError error) {
                errorCollector.add(error);
                recovery();
            }
        }
        return statements;
    }

    private void recovery() {
        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;
            switch (peek().type) {
                case FUNC:
                case VAR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            advance();
        }
    }

    private StmtNode parseDeclaration() throws ParseError {
        if (match(FUNC)) return parseFunction();
        if (match(VAR)) return parseVar();
        return parseStatement();
    }

    // region: Declaration

    private StmtNode.Var parseVar() throws ParseError {
        Token name = consume(IDENTIFIER);
        Token type = null;
        ExprNode initializer = null;

        if (match(COLON)) type = consume(FLOAT, INT, CHAR, STRING);
        if (match(ASSIGN)) initializer = parseExpression();

        consume(SEMICOLON);

        return new StmtNode.Var(name, initializer, type);
    }

    private StmtNode.Function parseFunction() throws ParseError {
        Token name = consume(IDENTIFIER);
        consume(LEFT_PAREN);
        List<Token> params = new Vector<>();
        List<Token> types = new Vector<>();

        if (!match(RIGHT_PAREN)) {
            do {
                Token param = consume(IDENTIFIER);
                consume(COLON);
                Token type = consume(FLOAT, INT, CHAR, STRING);
                params.add(param);
                types.add(type);
            } while (match(COMMA));
            consume(RIGHT_PAREN);
        }

        Token return_type = null;
        if (match(ARROW)) return_type = consume(FLOAT, INT, CHAR, STRING);

        StmtNode.Block body = parseBlock();
        return new StmtNode.Function(name, params, types, return_type, body);
    }

    private StmtNode parseStatement() throws ParseError {
        if (match(WHILE)) return parseWhile();
        if (match(IF)) return parseIf();
        if (match(LEFT_BRACE)) return parseBlock();
        if (match(CONTINUE)) return parseContinue();
        if (match(BREAK)) return parseBreak();
        if (match(RETURN)) return parseReturn();
        if (match(PRINT)) return parsePrint();
        return parseExpressionStmt();
    }

    // endregion

    // region: Statement

    private StmtNode parseExpressionStmt() throws ParseError {
        ExprNode expr = parseExpression();
        consume(SEMICOLON);
        return new StmtNode.Expression(expr);
    }

    private StmtNode.Return parseReturn() throws ParseError {
        ExprNode value = null;
        if (!check(SEMICOLON))
            value = parseExpression();
        consume(SEMICOLON);
        return new StmtNode.Return(value);
    }

    private StmtNode parseBreak() throws ParseError {
        consume(SEMICOLON);
        return new StmtNode.Break();
    }

    private StmtNode parseContinue() throws ParseError {
        consume(SEMICOLON);
        return new StmtNode.Continue();
    }

    private StmtNode.Block parseBlock() throws ParseError {
        consume(LEFT_BRACE);
        List<StmtNode> statements = new ArrayList<>();
        while (!check(RIGHT_BRACE)) {
            try {
                if (match(VAR)) statements.add(parseVar());
                else statements.add(parseStatement());
            } catch (ParseError error) {
                errorCollector.add(error);
                recovery();
            }
        }
        consume(RIGHT_BRACE);
        return new StmtNode.Block(statements);
    }

    private StmtNode parseIf() throws ParseError {
        consume(LEFT_PAREN);
        ExprNode condition = parseExpression();
        consume(RIGHT_PAREN);

        StmtNode.Block if_body = parseBlock();
        StmtNode.Block else_body = null;
        if (check(ELSE)) {
            else_body = parseBlock();
        }
        return new StmtNode.If(condition, if_body, else_body);
    }

    private StmtNode parseWhile() throws ParseError {
        consume(LEFT_PAREN);
        ExprNode condition = parseExpression();
        consume(RIGHT_PAREN);

        StmtNode.Block body = parseBlock();
        return new StmtNode.While(condition, body);
    }

    private StmtNode parsePrint() throws ParseError {
        consume(LEFT_PAREN);
        ExprNode value = parseExpression();
        consume(RIGHT_PAREN);
        consume(SEMICOLON);
        return new StmtNode.Print(value);
    }

    // endregion

    // region: Expression
    private ExprNode parseExpression() throws ParseError {
        return parseAssignment();
    }

    private ExprNode parseAssignment() throws ParseError {
        ExprNode expr = parseLogicalOr();

        if (match(ASSIGN, ADD_ASSIGN, DIV_ASSIGN, MULTI_ASSIGN, DIV_ASSIGN, MOD_ASSIGN)) {
            int assign = current - 1;
            Token type = previous();
            ExprNode value = parseExpression();
            if (expr instanceof ExprNode.Variable) {
                Token name = ((ExprNode.Variable) expr).name;
                return new ExprNode.Assign(name, type, value);
            }
            throw new ParseError(assign, "invalid assignment target", tokens, BEFORE);
        }
        return expr;
    }

    private ExprNode parseLogicalOr() throws ParseError {
        ExprNode left = parseLogicalAnd();
        while (check(REL_OR)) {
            Token operator = consume(REL_OR);
            ExprNode right = parseLogicalAnd();
            left = new ExprNode.Logical(left, operator, right);
        }
        return left;
    }

    private ExprNode parseLogicalAnd() throws ParseError {
        ExprNode left = parseEquality();
        while (check(REL_AND)) {
            Token operator = consume(REL_AND);
            ExprNode right = parseEquality();
            left = new ExprNode.Logical(left, operator, right);
        }
        return left;
    }

    private ExprNode parseEquality() throws ParseError {
        ExprNode left = parseComparsion();
        while (check(EQUAL) || check(NOT_EQUAL)) {
            Token operator = consume(EQUAL, NOT_EQUAL);
            ExprNode right = parseComparsion();
            left = new ExprNode.Relation(left, operator, right);
        }
        return left;
    }

    private ExprNode parseComparsion() throws ParseError {
        ExprNode left = parseAddition();
        while (check(GREATER) || check(GREATER_EQUAL) || check(LESS) || check(LESS_EQUAL)) {
            Token operator = consume(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);
            ExprNode right = parseAddition();
            left = new ExprNode.Relation(left, operator, right);
        }
        return left;
    }

    private ExprNode parseAddition() throws ParseError {
        ExprNode left = parseMultiplication();
        while (check(ADD) || check(SUB)) {
            Token operator = consume(ADD, SUB);
            ExprNode right = parseMultiplication();
            left = new ExprNode.Binary(left, operator, right);
        }
        return left;
    }

    private ExprNode parseMultiplication() throws ParseError {
        ExprNode left = parseUnary();
        while (check(MULTI) || check(DIV)) {
            Token operator = consume(MULTI, DIV);
            ExprNode right = parseUnary();
            left = new ExprNode.Binary(left, operator, right);
        }
        return left;
    }

    private ExprNode parseUnary() throws ParseError {
        if (check(SUB) || check(REL_NOT)) {
            Token operator = consume(SUB, REL_NOT);
            ExprNode right = parsePostfix();
            return new ExprNode.Unary(operator, right);
        }
        return parsePostfix();
    }

    private ExprNode parsePostfix() throws ParseError {
        ExprNode node = parsePrimary();

        // Function Call
        if (match(LEFT_PAREN)) {
            List<ExprNode> args = new Vector<>();
            if (match(RIGHT_PAREN)) {
                return new ExprNode.FunCall(node, null, args);
            }
            do {
                ExprNode arg = parseAssignment();
                args.add(arg);
            } while (match(COMMA));
            consume(RIGHT_PAREN);
            return new ExprNode.FunCall(node, null, args);
        }

        // Future: array sub

        return node;
    }

    private ExprNode parsePrimary() throws ParseError {
        if (match(INTEGER_CONSTANT, FLOAT_CONSTANT, STRING_CONSTANT))
            return new ExprNode.Literal(previous().literal);
        if (match(IDENTIFIER))
            return new ExprNode.Variable(previous());
        if (match(LEFT_PAREN)) {
            ExprNode expr = parseExpression();
            consume(RIGHT_PAREN);
            return new ExprNode.Grouping(expr);
        }
        throw new ParseError(current, "expect expression", tokens, AFTER);
    }

    // endregion

    //region Function Relates Tokens

    private Token consume(TokenType... types) throws ParseError {
        for (TokenType type : types)
            if (check(type)) return advance();
        throw new ParseError(current, "expect " + strRepr(types), tokens, AFTER);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return current >= tokens.size();
    }

    private String strRepr(TokenType... types) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < types.length; ++i) {
            if (i != 0)
                builder.append(" || ");
            if (tokenValue.containsKey(types[i]))
                builder.append("'").append(tokenValue.get(types[i])).append("'");
            else
                builder.append(types[i].toString().toLowerCase());
        }

        return builder.toString();
    }
    //endregion
}

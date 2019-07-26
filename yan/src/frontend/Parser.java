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
            statements.add(parseDeclaration());
        }
        return statements;
    }

    private StmtNode parseDeclaration() {
        try {
            if (match(FUNC)) return parseFunction();
            if (match(VAR)) return parseVar();
            return parseStatement();
        } catch (ParseError error) {
            // TODO: error recovery
            return null;
        }
    }

    // region: Declaration

    private StmtNode.Var parseVar() throws ParseError {
        Token name = consume(IDENTIFIER);
        consume(ASSIGN);
        Token type = null;
        if(check(COLON)) {
            type = consume(FLOAT, INT, CHAR, STRING);
        }
        ExprNode initializer = parseExpression();
        return new StmtNode.Var(name, initializer, type);
    }

    private StmtNode.Function parseFunction() throws ParseError {
        Token name = consume(IDENTIFIER);
        consume(LEFT_PAREN);
        List<Token> params = new Vector<>();
        List<Token> types = new Vector<>();

        while(!check(RIGHT_PAREN)) {
            Token param = consume(IDENTIFIER);
            consume(COLON);
            Token type = consume(FLOAT, INT, CHAR, STRING);
            consume(COMMA);
            params.add(param);
            types.add(type);
        }

        StmtNode.Block body = parseBlock();
        return new StmtNode.Function(name, params, types, body);
    }

    private StmtNode parseStatement() {
        if (match(WHILE)) return parseWhile();
        if (match(IF)) return parseIf();
        if (match(LEFT_BRACE)) return parseBlock();
        if (match(CONTINUE)) return parseContinue();
        if (match(BREAK)) return parseBreak();
        if (match(RETURN)) return parseReturn();
        return parseExpressionStmt();
    }

    // endregion

    // region: Statement

    private StmtNode parseExpressionStmt() {
        ExprNode expr = parseExpression();
        return new StmtNode.Expression(expr);
    }

    private StmtNode.Return parseReturn() throws ParseError {
        consume(SEMICOLON);
        return new StmtNode.Return();
    }

    private StmtNode parseBreak() {

        return StmtNode.Break();
    }

    private StmtNode parseContinue() {
        return null;
    }

    private StmtNode.Block parseBlock() {
        return null;
    }

    private StmtNode parseIf() {

    }

    private StmtNode parseWhile() {

    }

    // endregion

    private ExprNode parseExpression() {
    }

    //region Function Relates Tokens

    private Token consume(TokenType... types) throws ParseError {
        for(TokenType type : types)
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

        for(int i = 0; i<types.length; ++i) {
            if(i != 0)
                builder.append(" || ");
            if(tokenValue.containsKey(types[i]))
                builder.append("'" + tokenValue.get(types[i]) + "'");
            else
                builder.append(types[i].toString().toLowerCase());
        }

        return builder.toString();
    }
    //endregion
}

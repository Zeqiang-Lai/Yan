package repl;

import error.ErrorCollector;
import frontend.*;
import frontend.ast.StmtNode;
import interpreter.Interpreter;

import java.util.List;
import java.util.Vector;

public class YanEngine implements ScriptEngine {
    final Interpreter interpreter = new Interpreter();

    @Override
    public Object execute(String statement) throws Exception {
        return run(statement, "<stdin>");
    }

    public void interrupt() {

    }

    @Override
    public String getName() {
        return "Yan";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    private String run(String source, String file_name) {
        ErrorCollector errorCollector = ErrorCollector.getInstance();
        errorCollector.clear();
        errorCollector.setFile_name(file_name);

        Vector<Token> tokens = new Vector<>();
        assert source != null;
        SourceBuffer buff = new SourceBuffer(source);
        Lexer lexer = new Lexer(buff);
        Token token = lexer.scan();
        while (token.type != TokenType.EOF) {
            tokens.add(token);
            token = lexer.scan();
        }

        if (errorCollector.hasError()) {
            return errorCollector.summary();
        }

        Parser parser = new Parser(tokens);
        List<StmtNode> statements = parser.parse();

        if (errorCollector.hasError()) {
            return errorCollector.summary();
        }
        interpreter.interpret(statements);

        if (errorCollector.hasError()) {
            return errorCollector.summary();
        }
        return null;
    }
}

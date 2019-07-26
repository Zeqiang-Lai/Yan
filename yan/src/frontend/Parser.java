package frontend;

import error.ErrorCollector;

import java.util.List;

public class Parser {
    // region Properties

    private List<Token> tokens;
    private int last_index;
    private ErrorCollector errorCollector = ErrorCollector.getInstance();

    // endregion

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

}

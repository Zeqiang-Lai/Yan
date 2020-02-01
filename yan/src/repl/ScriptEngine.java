package repl;

public interface ScriptEngine {
    Object execute(String statement) throws Exception;

    void interrupt();

    String getName();

    String getVersion();
}

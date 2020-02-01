package repl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class ConsoleEngine {

    // region: Exceptions

    public static class QuitException extends Exception {}
    public static class DebugException extends Exception {
        public boolean shouldEnable;
        public DebugException(boolean shouldEnable) {
            this.shouldEnable = shouldEnable;
        }
    }
    public static class ClearException extends Exception {}

    // endregion

    private ScriptEngine scriptEngine;
    private Vector<String> scripts = new Vector<>();

    public ConsoleEngine(ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }

    public Object execute(String line) throws Exception {
        if(!line.startsWith(":")) {
            scripts.add(line);
            return scriptEngine.execute(line);
        } else {
            String cmd = line.substring(1);
            if(cmd.startsWith("save")) { return save2File(cmd); }
            switch (cmd) {
                case "help":
                    return help();
                case "quit":
                    throw new QuitException();
                case "debug":
                    throw new DebugException(true);
                case "quit debug":
                    throw new DebugException(false);
                case "clear":
                    throw new ClearException();
                default:
                    return unrecognized();
            }
        }
    }

    public void interrupt() {
        scriptEngine.interrupt();
    }

    private Object save2File(String cmd) {
        Date date = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss.txt");
        String filePath = f.format(date);

        // extract file path if exists
        if(cmd.length() > 5) filePath = cmd.substring(5);

        // save scripts into file
        try {
            File file = new File(filePath);
            if(!file.exists()){ file.createNewFile(); }
            FileWriter fileWritter = new FileWriter(file.getName(),true);
            fileWritter.write(String.join("\n", scripts));
            fileWritter.close();
            return "Successfully saved scripts into " + filePath;
        } catch (IOException e) {
            return "Fail to save scripts into " + filePath + "\n" + e.getMessage();
        }
    }

    private Object help() {
        return "\nThe REPL (Read-Eval-Print-Loop) acts like an interpreter.  " +
                "Valid statements, expressions, and declarations are immediately compiled and executed.\n" +
                "The complete set of commands are also available as described below.  " +
                "Commands must be prefixed with a colon at the REPL prompt (:quit for example.)\n\n" +
                "Commands: \n" +
                "  help               -- Show a list of all available commands.\n" +
                "  clear              -- Clear screen.\n" +
                "  save [filePath]    -- Save scripts into filePath (if provided).\n" +
                "  debug              -- Enter debug mode if you encounter any exception you don't know.\n" +
                "  quit               -- Quit the Yan interpreter.\n" +
                "  quit debug         -- Quit the debug mode.";
    }

    private Object unrecognized() {
        return "Unrecognized command --- Type :help for assistance.";
    }

    public String welcomeMessage() {
        Date date = new Date();
        String msg = "Welcome to " + scriptEngine.getName() + " version " + scriptEngine.getVersion() +
                "(default, " + date.toString() + ")" + "\n" +
                "Type :help for assistance.";
        return msg;
    }
}

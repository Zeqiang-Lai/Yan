package error;

import java.util.List;
import java.util.Vector;

public class ErrorCollector {
    private List<CompilerError> errors = new Vector<>();

    private String file_name;

    // region singleton pattern
    private static ErrorCollector instance = new ErrorCollector();

    private ErrorCollector(){}

    public static ErrorCollector getInstance(){
        return instance;
    }
    // endregion

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }

    public void add(CompilerError e) {
        errors.add(e);
    }

    public void show() {
        String bold_color = "\033[1m";
        for(CompilerError error : errors) {
            String msg = bold_color + file_name + ":" +
                    error.description;
            System.out.println(msg);
        }
        System.out.println(errors.size() + " errors generated.");
    }

    public void clear() {
        errors.clear();
    }
}
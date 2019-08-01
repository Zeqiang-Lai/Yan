package compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Scope {
    private Stack<Map<String, Object>> scopes = new Stack<>();
    public Map<String, Object> current;


}

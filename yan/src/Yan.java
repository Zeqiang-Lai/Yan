import error.ErrorCollector;
import frontend.*;
import frontend.ast.StmtNode;
import interpreter.Interpreter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;

public class Yan {
    public static void main(String[] args) throws IOException {
        String source = null;
        String out = null;

        Yan runner = new Yan();
        if (args.length == 0) {
            runner.runInterpreter();
        } else {
            for (int i = 0; i < args.length; ) {
                if (args[i].equals("-o")) {
                    out = args[i + 1];
                    i++;
                } else {
                    if (source == null)
                        source = args[i];
                    i++;
                }
            }
//            runner.run(source, out);
        }
    }

    private void runCompiler(String source_path, String out) {
        File f = new File(source_path);
        String file_name = f.getName();
        if (out == null) out = file_name;

        String source = null;
        try {
            source = readFile(source_path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ErrorCollector errorCollector = ErrorCollector.getInstance();
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
            errorCollector.show();
            return;
        }

        Parser parser = new Parser(tokens);
        List<StmtNode> statements = parser.parse();

        if (errorCollector.hasError()) {
            errorCollector.show();
        }
    }

    private String readConsole(BufferedReader reader) throws IOException {
        // TODO: if else ?
        StringBuilder input = new StringBuilder();
        String line = reader.readLine();
        input.append(line);
        if (line.startsWith("func") || line.startsWith("while") ||
                line.startsWith("if") || line.startsWith("{")) {
            do {
                System.out.print("... ");
                line = reader.readLine();
                input.append(line);
                // FIXME: {}
                if(line.endsWith("}")) {
                    System.out.print("... ");
                    String line1 = reader.readLine();
                    System.out.print("... ");
                    String line2 = reader.readLine();
                    if(line1.equals("") && line2.equals(""))
                        break;
                    else
                        input.append(line1).append(line2);
                }
            } while (true);
        }
        return input.toString();
    }

    private void runInterpreter() throws IOException {
        final Interpreter interpreter = new Interpreter();
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        while (true) {
            System.out.print(">>> ");
            run(interpreter, readConsole(reader), "Yan");
        }
    }

    private void run(Interpreter interpreter, String source, String file_name) {
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
            errorCollector.show();
            return;
        }

        Parser parser = new Parser(tokens);
        List<StmtNode> statements = parser.parse();

        if (errorCollector.hasError()) {
            errorCollector.show();
            return;
        }
        interpreter.interpret(statements);

        if (errorCollector.hasError()) {
            errorCollector.show();
        }
    }

    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private static void printUsage() {
        String usage = "OVERVIEW: Yan Compiler\n\n" +
                "USAGE: Yan [options] <input>\n\n" +
                "OPTIONS:\n" +
                "\t-o <file>\tWrite output to <file>.xml";
        System.out.println(usage);
    }
}

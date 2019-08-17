import compiler.ILGen;
import compiler.semantic.Resolver;
import error.ErrorCollector;
import frontend.*;
import frontend.ast.StmtNode;
import interpreter.Interpreter;
import interpreter.error.RuntimeError;

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
                } else if(args[i].equals("--help")) {
                    printUsage();
                } else {
                    if (source == null)
                        source = args[i];
                    i++;
                }
            }
            runner.runCompiler(source, out);
        }
    }

    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private static void saveFile(String path, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(content);
        writer.close();
    }

    private static void printUsage() {
        String usage = "OVERVIEW: Yan Compiler\n\n" +
                "USAGE: Yan [options] <input>\n\n" +
                "OPTIONS:\n" +
                "\t-o <file>\tWrite output to <file>.xml";
        System.out.println(usage);
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
            return;
        }

        Resolver resolver = new Resolver();
        for(StmtNode stmt : statements) {
            resolver.execute(stmt);
        }

        if (errorCollector.hasError()) {
            errorCollector.show();
        }

        ILGen il_generator = new ILGen();
        for(StmtNode stmt : statements) {
            il_generator.gen(stmt);
        }

        if (errorCollector.hasError()) {
            errorCollector.show();
        }
        String name = file_name.substring(0, file_name.lastIndexOf("."));
        String out_path = Paths.get(f.getParent(), name + ".il").toString();
        try {
            saveFile(out_path, il_generator.get_il_code());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // region interpreter

    private int countBrace(String line, int count) {
        for(int i=0; i<line.length(); ++i) {
            if(line.charAt(i) == '{')
                count += 1;
            else if(line.charAt(i) == '}')
            {
                if(count > 0) count --;
                else
                    throw new RuntimeError(null, "unmatched '{' .");
            }
        }
        return count;
    }

    private String readConsole(BufferedReader reader) throws IOException {
        // TODO: if else ?
        int count = 0;
        StringBuilder input = new StringBuilder();
        String line = reader.readLine();
        count = countBrace(line, count);

        input.append(line);

        if (line.startsWith("func") || line.startsWith("while") ||
                line.startsWith("if") || line.startsWith("{")) {
            do {
                System.out.print("... ");
                line = reader.readLine();
                input.append(line);
                count = countBrace(line, count);

                if(count == 0) {
                    System.out.print("... ");
                    String line1 = reader.readLine();
                    count = countBrace(line1, count);

                    if(line1.equals(""))
                        break;
                    else
                        input.append(line1);
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
            run(interpreter, readConsole(reader), "<stdin>");
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

    // endregion
}

import error.ErrorCollector;
import frontend.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

public class Yan {
    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    void run(String source_path, String out) {
        File f = new File(source_path);
        String file_name = f.getName();
        if(out == null) out = file_name;

        String source = null;
        try {
            source = readFile(source_path, Charset.forName("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ErrorCollector errorCollector = ErrorCollector.getInstance();
        errorCollector.setFile_name(file_name);

        Vector<Token> tokens = new Vector<>();
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
//        StmtNode.CompilationUnit tree = parser.parse();

        if (errorCollector.hasError()) {
            errorCollector.show();
        }
    }

    static void printUsage() {
        String usage = "OVERVIEW: Yan Compiler\n\n" +
                "USAGE: Yan [options] <input>\n\n" +
                "OPTIONS:\n" +
                "\t-o <file>\tWrite output to <file>.xml";
        System.out.println(usage);
    }

    public static void main(String[] args) {
        String source = null;
        String out = null;

        if(args.length == 0) {
            printUsage();
            return;
        } else {
            for(int i=0; i<args.length;) {
                if(args[i].equals("-o")) {
                    out = args[i+1];
                    i++;
                } else {
                    if(source == null)
                        source = args[i];
                    i++;
                }
            }
        }
        Yan runner = new Yan();
        runner.run(source, out);
    }
}

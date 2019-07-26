import frontend.Lexer;
import frontend.SourceBuffer;
import frontend.Token;
import frontend.TokenType;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Yan {
    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void main(String[] args) {
        String source_path = "test/power.yan";
        if(args.length == 0) {
            System.out.println("No specified source file. Use default: "+source_path);
        } else {
            source_path = args[0];
        }

        String source = null;
        try {
            source = readFile(source_path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert source != null;
        SourceBuffer buff = new SourceBuffer(source);

        Lexer lexer = new Lexer(buff);
        Token token = lexer.scan();
        System.out.println(token);
        while (token.type != TokenType.EOF) {
            token = lexer.scan();
            System.out.println(token);
        }
    }
}

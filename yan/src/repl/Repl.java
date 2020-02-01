package repl;

import org.jline.builtins.Widgets;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReader.Option;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.DefaultParser.Bracket;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import org.jline.utils.OSUtils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Repl {
    private static final String defaultPrompt = "  \u001b[38;5;250m%N> ";
    private static final String defaultSecondaryPrompt = "  \u001b[38;5;250m%N. ";

    private static final String debugPrompt = "\u001b[38;5;250mDebug %N> ";
    private static final String debugSecondaryPrompt = "debug\u001b[38;5;250Debug m%N. ";

    public static void run() {
        try {
            DefaultParser parser = new DefaultParser();
            parser.setEofOnUnclosedBracket(Bracket.CURLY, Bracket.ROUND, Bracket.SQUARE);
            parser.setEofOnUnclosedQuote(true);
            parser.setEscapeChars(null);

            Terminal terminal = TerminalBuilder.builder().system(true).build();

            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .parser(parser)
                    .variable(LineReader.SECONDARY_PROMPT_PATTERN, defaultSecondaryPrompt)
                    .variable(LineReader.INDENTATION, 2)
                    .option(Option.INSERT_TAB, true)
                    .build();

            if (OSUtils.IS_WINDOWS) {
                reader.setVariable(LineReader.BLINK_MATCHING_PAREN, 0); // if enabled cursor remains in begin parenthesis
            }

            // AutoPair
            Widgets.AutopairWidgets autopairWidgets = new Widgets.AutopairWidgets(reader);
            autopairWidgets.enable();

            //
            // REPL Loop
            //

            YanEngine yanEngine = new YanEngine();
            ConsoleEngine engine = new ConsoleEngine(yanEngine);

            String prompt = defaultPrompt;
            int countLine = 1;
            terminal.writer().println(engine.welcomeMessage());
            while (true) {
                String line = null;
                try {
                    reader.variable(LineReader.LINE_OFFSET, countLine);
                    line = reader.readLine(prompt);
                    Object result = engine.execute(line);
                    if(result != null) { terminal.writer().println(result); }

                } catch (UserInterruptException e) {
                    // Ignore
                } catch (EndOfFileException e) {
                    return;
                } catch (ConsoleEngine.QuitException e) {
                    break;
                } catch (ConsoleEngine.DebugException e) {

                    Logger.getLogger("org.jline").setLevel(e.shouldEnable ? Level.ALL : Level.WARNING);
                    terminal.writer().println(e.shouldEnable ? "Entered debug mode." : "Quited debug mode");
                    prompt = e.shouldEnable ? debugPrompt : defaultPrompt;
                    reader.variable(LineReader.SECONDARY_PROMPT_PATTERN,
                            e.shouldEnable ? debugSecondaryPrompt : defaultSecondaryPrompt);

                } catch (ConsoleEngine.ClearException e) {
                    terminal.puts(InfoCmp.Capability.clear_screen);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                countLine += line != null ? line.split("\r\n|\r|\n").length : 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package tech.jilge.server.log;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.function.Consumer;

public final class IOTerminal {

    public enum Output {
        INFO,
        WARN,
        ERROR,
    }

    private final LineReader lineReader = LineReaderBuilder.builder()
            .terminal(TerminalBuilder.builder()
                    .system(true)
                    .streams(System.in, System.out)
                    .encoding(StandardCharsets.UTF_8)
                    .dumb(true)
                    .build())
            .completer(new DefaultITerminalCompleter())
            .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
            .option(LineReader.Option.AUTO_REMOVE_SLASH, false)
            .option(LineReader.Option.INSERT_TAB, false)
            .build();
    private final LinkedList<Consumer<String>> handlers = new LinkedList<>();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final Thread thread = new Thread(() -> {
        String line;
        while (!Thread.currentThread().isInterrupted()) {
            final var prompt = "» ";
            line = lineReader.readLine(prompt);
            for (Consumer<String> handler : handlers) handler.accept(line);
        }
    });

    public IOTerminal() throws IOException {
        this.clear();
        lineReader.setAutosuggestion(LineReader.SuggestionType.COMPLETER);
    }

    public IOTerminal send(String output, Output level) {
        output = this._color(output);
        switch (level) {
            case INFO -> output = " | " + dateTimeFormatter.format(LocalDateTime.now()) + " | " + IOTerminalAnsiColor.GREEN.getAnsiCode() + "INFO" + IOTerminalAnsiColor.RESET + " | " + output + IOTerminalAnsiColor.RESET;
            case ERROR -> output = " | " +  dateTimeFormatter.format(LocalDateTime.now()) + " | " + IOTerminalAnsiColor.RED.getAnsiCode() + "ERRO" + IOTerminalAnsiColor.RESET +" | " + IOTerminalAnsiColor.RESET + output + IOTerminalAnsiColor.RESET;
            case WARN -> output = " | " +  dateTimeFormatter.format(LocalDateTime.now()) + " | " + IOTerminalAnsiColor.YELLOW.getAnsiCode() + "WARN" + IOTerminalAnsiColor.RESET + " | " + IOTerminalAnsiColor.RESET + output + IOTerminalAnsiColor.RESET;
        }
        lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
        lineReader.getTerminal().writer().println(output);
        lineReader.getTerminal().flush();
        if (lineReader.isReading()) {
            lineReader.callWidget(LineReader.REDRAW_LINE);
            lineReader.callWidget(LineReader.REDISPLAY);
        }
        return this;
    }

    private String name(String input) {
        if (input.length() >= 11) {
            return " " + input.substring(0, 11) + " ";
        } else {
            StringBuilder builder = new StringBuilder(" ");
            int size = 11;
            for (char c : input.toCharArray()) {
                builder.append(c);
                size--;
            }
            builder.append(" ".repeat(Math.max(0, size)));
            return builder.toString();
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public IOTerminal send(String output) {
        return send(output, Output.INFO);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void clear() {
        if (System.getProperty("os.name").contains("Windows")) {
            try {
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            } catch (RuntimeException exception) {
                exception.printStackTrace();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                ProcessBuilder pb = new ProcessBuilder("clear");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            } catch (RuntimeException exception) {
                exception.printStackTrace();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String _color(String code) {
        return code
                .replace("§c", IOTerminalAnsiColor.RED.getAnsiCode())
                .replace("§a", IOTerminalAnsiColor.GREEN.getAnsiCode())
                .replace("§e", IOTerminalAnsiColor.YELLOW.getAnsiCode())
                .replace("§6", IOTerminalAnsiColor.ORANGE.getAnsiCode())
                .replace("§r", IOTerminalAnsiColor.RESET.getAnsiCode())
                .replace("§7", IOTerminalAnsiColor.RESET.getAnsiCode())
                .replace("§b", IOTerminalAnsiColor.CYAN.getAnsiCode()
                );
    }

    public void run() {
        this.thread.start();
    }

    @SuppressWarnings("resource")
    public void close() {
        lineReader.getTerminal().reader().shutdown();
        lineReader.getTerminal().pause();
        this.thread.interrupt();
    }

    public void print() {
        send(" ");
        send("      Contributors # §aAlexander Jilge");
        send("      Phase        # §eVersion §r- 1.0.0");
        send(" ");
    }

    public IOTerminal push(Consumer<String> handler) {
        this.handlers.push(handler);
        return this;
    }


}

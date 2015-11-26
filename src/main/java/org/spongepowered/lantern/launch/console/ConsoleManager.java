package org.spongepowered.lantern.launch.console;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import org.fusesource.jansi.AnsiConsole;
import org.spongepowered.api.util.command.source.ConsoleSource;
import org.spongepowered.lantern.SpongeImpl;
import org.spongepowered.lantern.service.scheduler.LanternScheduler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

/**
 * A meta-class to handle all logging and input-related console improvements.
 * Portions are heavily based on CraftBukkit.
 */
public final class ConsoleManager {

    private static final String CONSOLE_DATE = "HH:mm:ss";
    private static final String FILE_DATE = "yyyy/MM/dd HH:mm:ss";
    private static final Logger logger = Logger.getLogger("");

    private ConsoleReader reader;
    private ConsoleSource sender;

    private boolean running = true;

    public ConsoleManager() {
        // install Ansi code handler, which makes colors work on Windows
        AnsiConsole.systemInstall();

        for (Handler h : logger.getHandlers()) {
            logger.removeHandler(h);
        }

        // add log handler which writes to console
        logger.addHandler(new FancyConsoleHandler());

        // reader must be initialized before standard streams are changed
        try {
            reader = new ConsoleReader();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Exception initializing console reader", ex);
        }
        reader.addCompleter(new ConsoleCommandCompleter());

        // set system output streams
        System.setOut(new PrintStream(new LoggerOutputStream(Level.INFO), true));
        System.setErr(new PrintStream(new LoggerOutputStream(Level.WARNING), true));
    }

    public ConsoleSource getSender() {
        return sender;
    }

    public void startConsole() {
        sender = new ColoredConsoleSource();
        Thread thread = new ConsoleCommandThread();
        thread.setName("ConsoleCommandThread");
        thread.setDaemon(true);
        thread.start();
    }

    public void startFile(String logfile) {
        File parent = new File(logfile).getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs()) {
            logger.warning("Could not create log folder: " + parent);
        }
        Handler fileHandler = new RotatingFileHandler(logfile);
        fileHandler.setFormatter(new DateOutputFormatter(FILE_DATE, false));
        logger.addHandler(fileHandler);
    }

    public void stop() {
        running = false;
        for (Handler handler : logger.getHandlers()) {
            handler.flush();
            handler.close();
        }
    }

//    private String colorize(String string) {
//        if (string.indexOf(ChatColor.COLOR_CHAR) < 0) {
//            return string;  // no colors in the message
//        } else if (!jLine || !reader.getTerminal().isAnsiSupported()) {
//            return ChatColor.stripColor(string);  // color not supported
//        } else {
//            // colorize or strip all colors
//            for (ChatColor color : colors) {
//                if (replacements.containsKey(color)) {
//                    string = string.replaceAll("(?i)" + color.toString(), replacements.get(color));
//                } else {
//                    string = string.replaceAll("(?i)" + color.toString(), "");
//                }
//            }
//            return string + Ansi.ansi().reset().toString();
//        }
//    }

    private class ConsoleCommandCompleter implements Completer {

        @Override
        public int complete(String buffer, int cursor, List<CharSequence> candidates) {
            try {
                LanternScheduler.getInstance().createTaskBuilder()
                        .execute(() -> {
                            candidates.addAll(SpongeImpl.getGame().getCommandDispatcher().getSuggestions(ConsoleManager.this.sender, buffer));
                        })
                        .submit(SpongeImpl.getSponge());

                return buffer.lastIndexOf(' ') + 1;
            } catch (Throwable t) {
                Logger.getLogger("").log(Level.WARNING, "Error while tab completing", t);
                return cursor;
            }
        }

    }

    private class ConsoleCommandThread extends Thread {
        @Override
        public void run() {
            String command = "";
            while (running) {
                try {
                    command = reader.readLine(">", null);

                    if (command == null || command.trim().length() == 0)
                        continue;

                    final String tempCommand = command.trim();
                    LanternScheduler.getInstance().createTaskBuilder()
                            .execute(() -> {
                                SpongeImpl.getGame().getCommandDispatcher().process(ConsoleManager.this.sender, tempCommand);
                            })
                            .submit(SpongeImpl.getSponge());

                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Error while executing command: " + command, ex);
                }
            }
        }
    }

    private static class LoggerOutputStream extends ByteArrayOutputStream {
        private final String separator = System.getProperty("line.separator");
        private final Level level;

        public LoggerOutputStream(Level level) {
            super();
            this.level = level;
        }

        @Override
        public synchronized void flush() throws IOException {
            super.flush();
            String record = this.toString();
            super.reset();

            if (record.length() > 0 && !record.equals(separator)) {
                logger.logp(level, "LoggerOutputStream", "log" + level, record);
            }
        }
    }

    private class FancyConsoleHandler extends ConsoleHandler {
        public FancyConsoleHandler() {
            setFormatter(new DateOutputFormatter(CONSOLE_DATE, true));
            setOutputStream(System.out);
        }

        @Override
        public synchronized void flush() {
            try {
                reader.print(ConsoleReader.RESET_LINE + "");
                reader.flush();
                super.flush();
                try {
                    reader.drawLine();
                } catch (Throwable ex) {
                    reader.getCursorBuffer().clear();
                }
                reader.flush();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "I/O exception flushing console output", ex);
            }
        }
    }

    private static class RotatingFileHandler extends StreamHandler {
        private final SimpleDateFormat dateFormat;
        private final String template;
        private final boolean rotate;
        private String filename;

        public RotatingFileHandler(String template) {
            this.template = template;
            rotate = template.contains("%D");
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            filename = calculateFilename();
            updateOutput();
        }

        private void updateOutput() {
            try {
                setOutputStream(new FileOutputStream(filename, true));
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to open " + filename + " for writing", ex);
            }
        }

        private void checkRotate() {
            if (rotate) {
                String newFilename = calculateFilename();
                if (!filename.equals(newFilename)) {
                    filename = newFilename;
                    // note that the console handler doesn't see this message
                    super.publish(new LogRecord(Level.INFO, "Log rotating to: " + filename));
                    updateOutput();
                }
            }
        }

        private String calculateFilename() {
            return template.replace("%D", dateFormat.format(new Date()));
        }

        @Override
        public synchronized void publish(LogRecord record) {
            if (!isLoggable(record)) {
                return;
            }
            checkRotate();
            super.publish(record);
            super.flush();
        }

        @Override
        public synchronized void flush() {
            checkRotate();
            super.flush();
        }
    }

    private class DateOutputFormatter extends Formatter {
        private final SimpleDateFormat date;
        private final boolean color;

        public DateOutputFormatter(String pattern, boolean color) {
            this.date = new SimpleDateFormat(pattern);
            this.color = color;
        }

        @Override
        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();

            builder.append(date.format(record.getMillis()));
            builder.append(" [");
            builder.append(record.getLevel().getLocalizedName().toUpperCase());
            builder.append("] ");

            //TODO Colors?
            builder.append(formatMessage(record));
            builder.append('\n');

            if (record.getThrown() != null) {
                // StringWriter's close() is trivial
                @SuppressWarnings("resource")
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                builder.append(writer.toString());
            }

            return builder.toString();
        }
    }

}

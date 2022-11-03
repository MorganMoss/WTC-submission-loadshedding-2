package wethinkcode.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class Logger {
    public static java.util.logging.Logger formatted(String name){
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(name);

        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            public synchronized String format(LogRecord lr) {
                return "[" + lr.getLoggerName() + "] " + lr.getLevel().getLocalizedName() + " " + lr.getMessage() + "\n";
            }
        });

        logger.addHandler(handler);

        return logger;
    }
}

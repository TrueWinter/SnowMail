package dev.truewinter.snowmail;

import org.slf4j.simple.SimpleLoggerFactory;

public class Logger {
    private static Logger logger;
    private static org.slf4j.Logger slf4j;

    private Logger() {
        slf4j = new SimpleLoggerFactory().getLogger(getClass().getPackageName());
    }

    public static Logger getInstance() {
        if (logger == null) {
            logger = new Logger();
        }

        return logger;
    }

    public org.slf4j.Logger getLogger() {
        return slf4j;
    }
}

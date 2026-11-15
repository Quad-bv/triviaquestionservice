package nl.bramv.triviaquestionservice.exception.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ApplicationExceptionLogger {

    private static final Logger log =
            LoggerFactory.getLogger(ApplicationExceptionLogger.class);

    public void log(ILoggableException exception, Throwable cause) {
        String message = "{} failed: {}";

        logAtLevel(exception.getSource(), exception.getLogLevel(), cause, message);
    }

    public void log(String source, LogLevel level, Throwable cause) {
        String message = "{} failed: {}";

        logAtLevel(source, level, cause, message);
    }

    private static void logAtLevel(String source, LogLevel level, Throwable cause, String message) {
        switch (level) {
            case DEBUG -> log.debug(message, source, cause.getMessage(), cause);
            case INFO -> log.info(message, source, cause.getMessage(), cause);
            case WARN -> log.warn(message, source, cause.getMessage(), cause);
            case ERROR -> log.error(message, source, cause.getMessage(), cause);
        }
    }
}
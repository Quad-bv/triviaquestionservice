package nl.bramv.triviaquestionservice.exception.logging;

import nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions.QuestionsProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExceptionLogHandler {
    private static final Logger log =
            LoggerFactory.getLogger(ExceptionLogHandler.class);

    public void log(QuestionsProviderException exception) {

        String message = "Questions provider '{}' failed: {}";

        switch (exception.getLogLevel()) {
            case DEBUG -> log.debug(
                    message,
                    exception.getProviderName(),
                    exception.getMessage(),
                    exception
            );

            case INFO -> log.info(
                    message,
                    exception.getProviderName(),
                    exception.getMessage(),
                    exception
            );

            case WARN -> log.warn(
                    message,
                    exception.getProviderName(),
                    exception.getMessage(),
                    exception
            );

            case ERROR -> log.error(
                    message,
                    exception.getProviderName(),
                    exception.getMessage(),
                    exception
            );
        }
    }
}

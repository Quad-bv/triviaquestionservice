package nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions;

import nl.bramv.triviaquestionservice.exception.logging.LogLevel;

// Indicates that the provider rejected the request because its rate limit was reached
public class QuestionsProviderRateLimitException extends QuestionsProviderException {

    public QuestionsProviderRateLimitException(String providerName, String message, Throwable cause) {
        super(providerName, message, LogLevel.INFO, cause);
    }

    public QuestionsProviderRateLimitException(String providerName, String message) {
        this(providerName, message, null);
    }
}

package nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions;

import nl.bramv.triviaquestionservice.exception.logging.LogLevel;

public class QuestionsProviderRateLimitException extends QuestionsProviderException {

    public QuestionsProviderRateLimitException(String providerName, String message, Throwable cause) {
        super(providerName, message, LogLevel.INFO, cause);
    }

    public QuestionsProviderRateLimitException(String providerName, String message) {
        super(providerName, message, LogLevel.INFO, null);
    }
}

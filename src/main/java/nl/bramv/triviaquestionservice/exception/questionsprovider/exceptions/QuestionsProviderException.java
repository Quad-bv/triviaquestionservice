package nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions;

import nl.bramv.triviaquestionservice.exception.logging.LogLevel;

// Base exception for failures originating from a question provider.
public abstract class QuestionsProviderException extends RuntimeException {
    private final String providerName;
    private final LogLevel logLevel;

    protected QuestionsProviderException(String providerName, String message, LogLevel logLevel, Throwable cause) {
        super(message, cause);
        this.providerName = providerName;
        this.logLevel = logLevel;
    }


    public String getProviderName() {
        return providerName;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }
}

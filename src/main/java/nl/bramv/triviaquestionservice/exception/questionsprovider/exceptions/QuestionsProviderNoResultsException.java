package nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions;

import nl.bramv.triviaquestionservice.exception.logging.LogLevel;

// Indicates that the provider could not find questions matching the request.
public class QuestionsProviderNoResultsException extends QuestionsProviderException {

    public QuestionsProviderNoResultsException(String providerName, String message, Throwable cause) {
        super(providerName, message, LogLevel.INFO, cause);
    }

    public QuestionsProviderNoResultsException(String providerName, String message) {
        this(providerName, message, null);
    }
}

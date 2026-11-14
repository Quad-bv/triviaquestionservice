package nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions;

import nl.bramv.triviaquestionservice.exception.logging.LogLevel;

public class QuestionsProviderNoResultsException extends QuestionsProviderException {

    public QuestionsProviderNoResultsException(String providerName, String message, Throwable cause) {
        super(providerName, message, LogLevel.INFO, cause);
    }

    public QuestionsProviderNoResultsException(String providerName, String message) {
        super(providerName, message, LogLevel.INFO, null);
    }
}

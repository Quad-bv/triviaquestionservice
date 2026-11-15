package nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions;

import nl.bramv.triviaquestionservice.exception.logging.LogLevel;

// Indicates that the provider rejected the request because of something the caller sent,
// such as an unknown category or an invalid session token.
public class QuestionsProviderBadRequestException extends QuestionsProviderException {

    public QuestionsProviderBadRequestException(String providerName, String message, Throwable cause) {
        super(providerName, message, LogLevel.INFO, cause);
    }

    public QuestionsProviderBadRequestException(String providerName, String message) {
        this(providerName, message, null);
    }
}
 
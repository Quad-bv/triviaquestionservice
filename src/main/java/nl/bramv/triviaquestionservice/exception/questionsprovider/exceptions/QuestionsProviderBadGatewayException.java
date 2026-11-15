package nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions;

import nl.bramv.triviaquestionservice.exception.logging.LogLevel;

// Indicates that the provider returned an invalid or unexpected response.
public class QuestionsProviderBadGatewayException extends QuestionsProviderException {

    public QuestionsProviderBadGatewayException(String providerName, String message, Throwable cause) {
        super(providerName, message, LogLevel.ERROR, cause);
    }

    public QuestionsProviderBadGatewayException(String providerName, String message) {
        this(providerName, message, null);
    }
}

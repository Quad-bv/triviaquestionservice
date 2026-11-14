package nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions;

import nl.bramv.triviaquestionservice.exception.logging.LogLevel;

// Indicates that the provider returned an invalid or unexpected response.
public class QuestionsProviderBadGatewayExceptionI extends QuestionsProviderExceptionI {

    public QuestionsProviderBadGatewayExceptionI(String providerName, String message, Throwable cause) {
        super(providerName, message, LogLevel.ERROR, cause);
    }

    public QuestionsProviderBadGatewayExceptionI(String providerName, String message) {
        this(providerName, message, null);
    }
}

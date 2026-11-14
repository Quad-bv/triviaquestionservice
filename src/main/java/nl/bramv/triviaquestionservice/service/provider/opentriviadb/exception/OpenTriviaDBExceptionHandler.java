package nl.bramv.triviaquestionservice.service.provider.opentriviadb.exception;

import nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions.QuestionsProviderBadGatewayException;
import nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions.QuestionsProviderNoResultsException;
import nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions.QuestionsProviderRateLimitException;
import nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions.QuestionsProviderUnavailableException;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.config.OpenTriviaDBProperties;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.model.OpenTriviaDBResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

@Component
public class OpenTriviaDBExceptionHandler {
    private final OpenTriviaDBProperties properties;

    public OpenTriviaDBExceptionHandler(OpenTriviaDBProperties properties) {
        this.properties = properties;
    }

    public RuntimeException handleHTTPException(HttpStatusCodeException exception) {
        OpenTriviaDBResponse errorResponse = getErrorResponse(exception);

        if (errorResponse != null && errorResponse.responseCode() != 0) {
            RuntimeException mappedException =
                    handleResponseCode(errorResponse.responseCode());

            if (mappedException != null) {
                return mappedException;
            }
        }

        return handleHttpStatus(exception.getStatusCode(), exception);
    }

    public RuntimeException handleResourceAccessException(ResourceAccessException exception) {
        return new QuestionsProviderUnavailableException(
                properties.providerName(),
                "Could not connect to " + properties.providerName() + ".",
                exception
        );
    }

    public RuntimeException handleRestClientException(RestClientException exception) {
        return new QuestionsProviderBadGatewayException(
                properties.providerName(),
                "Unexpected error communicating with " + properties.providerName() + ".",
                exception
        );
    }

    private OpenTriviaDBResponse getErrorResponse(HttpStatusCodeException exception) {
        try {
            return exception.getResponseBodyAs(OpenTriviaDBResponse.class);
        } catch (RestClientException ignored) {
            return null;
        }
    }

    private RuntimeException handleResponseCode(int responseCode) {
        return switch (responseCode) {
            case 1 -> new QuestionsProviderNoResultsException(
                    properties.providerName(),
                    "No questions found by " + properties.providerName()
            );

            case 2 -> new QuestionsProviderBadGatewayException(
                    properties.providerName(),
                    "Invalid request parameters sent to " + properties.providerName()
            );

            case 3, 4 -> new QuestionsProviderBadGatewayException(
                    properties.providerName(),
                    "Invalid or exhausted session token from " + properties.providerName()
            );

            case 5 -> new QuestionsProviderRateLimitException(
                    properties.providerName(),
                    "Rate limit reached for " + properties.providerName()
            );

            default -> null;
        };
    }

    private RuntimeException handleHttpStatus(
            HttpStatusCode statusCode,
            HttpStatusCodeException exception) {

        if (statusCode.value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
            return new QuestionsProviderRateLimitException(
                    properties.providerName(),
                    "Rate limit reached for " + properties.providerName()
            );
        }

        if (statusCode.is5xxServerError()) {
            return new QuestionsProviderUnavailableException(
                    properties.providerName(),
                    "The provider " + properties.providerName() + " is unavailable.",
                    exception
            );
        }

        return new QuestionsProviderBadGatewayException(
                properties.providerName(),
                "Unexpected HTTP error from " + properties.providerName() + ".",
                exception
        );
    }
}
package nl.bramv.triviaquestionservice.service.provider.opentriviadb.exception;

import nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions.*;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.config.OpenTriviaDBProperties;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.model.OpenTriviaDBResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

@Component
public class OpenTriviaDBExceptionMapper {

    // OpenTriviaDB documents response codes 1 to 5 as error codes (0 means success).
    private static final int FIRST_ERROR_CODE = 1;
    private static final int LAST_ERROR_CODE = 5;

    private final String providerName;

    public OpenTriviaDBExceptionMapper(OpenTriviaDBProperties properties) {
        this.providerName = properties.providerName();
    }

    // The provider returned an HTTP error; inspect the body before falling back to the HTTP status.
    public QuestionsProviderException handleHttpException(HttpStatusCodeException exception) {
        OpenTriviaDBResponse errorResponse = getErrorResponse(exception);

        if (errorResponse != null && isKnownErrorCode(errorResponse.responseCode())) {
            return handleResponseCode(errorResponse.responseCode());
        }

        return handleHttpStatus(exception.getStatusCode(), exception);
    }

    // No response at all: DNS failure, connection refused, timeout.
    public QuestionsProviderException handleResourceAccessException(ResourceAccessException exception) {
        return new QuestionsProviderUnavailableException(
                providerName,
                "Could not connect to " + providerName + ".",
                exception
        );
    }

    // Any other client-side error that was not handled above.
    public QuestionsProviderException handleRestClientException(RestClientException exception) {
        return new QuestionsProviderBadGatewayException(
                providerName,
                "Unexpected error communicating with " + providerName + ".",
                exception
        );
    }

    // Maps an OpenTriviaDB response code to an application-level exception.
    public QuestionsProviderException handleResponseCode(int responseCode) {
        return switch (responseCode) {
            case 1 -> new QuestionsProviderNoResultsException(
                    providerName,
                    "No questions found by " + providerName
            );

            case 2 -> new QuestionsProviderBadGatewayException(
                    providerName,
                    "Invalid request parameters sent to " + providerName
            );

            case 3, 4 -> new QuestionsProviderBadGatewayException(
                    providerName,
                    "Invalid or exhausted session token from " + providerName
            );

            case 5 -> new QuestionsProviderRateLimitException(
                    providerName,
                    "Rate limit reached for " + providerName
            );

            default -> new QuestionsProviderBadGatewayException(
                    providerName,
                    "Unexpected response code " + responseCode + " from " + providerName
            );
        };
    }

    // The provider returned no usable response.
    public QuestionsProviderException emptyResponse() {
        return new QuestionsProviderBadGatewayException(
                providerName,
                providerName + " returned an empty response."
        );
    }

    // The provider returned a valid response, but it contained no questions.
    public QuestionsProviderException noResults() {
        return new QuestionsProviderNoResultsException(
                providerName,
                "No questions found by " + providerName
        );
    }

    // The response structure was valid, but one or more question fields could not be mapped.
    public QuestionsProviderException invalidQuestionData(Throwable cause) {
        return new QuestionsProviderBadGatewayException(
                providerName,
                providerName + " returned question data that could not be understood.",
                cause
        );
    }

    // Uses the HTTP status only when the provider's response body has no usable error code.
    private QuestionsProviderException handleHttpStatus(
            HttpStatusCode statusCode,
            HttpStatusCodeException exception) {

        if (statusCode.value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
            return new QuestionsProviderRateLimitException(
                    providerName,
                    "Rate limit reached for " + providerName,
                    exception
            );
        }

        if (statusCode.is5xxServerError()) {
            return new QuestionsProviderUnavailableException(
                    providerName,
                    "The provider " + providerName + " is unavailable.",
                    exception
            );
        }

        return new QuestionsProviderBadGatewayException(
                providerName,
                "Unexpected HTTP error from " + providerName + ".",
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

    private boolean isKnownErrorCode(int responseCode) {
        return responseCode >= FIRST_ERROR_CODE && responseCode <= LAST_ERROR_CODE;
    }
}
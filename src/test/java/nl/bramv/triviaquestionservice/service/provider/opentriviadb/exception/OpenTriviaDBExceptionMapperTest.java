package nl.bramv.triviaquestionservice.service.provider.opentriviadb.exception;

import nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions.*;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.config.OpenTriviaDBProperties;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.model.OpenTriviaDBResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OpenTriviaDBExceptionMapperTest {

    private static final String PROVIDER_NAME = "OpenTriviaDB";

    private OpenTriviaDBExceptionMapper exceptionMapper;

    @BeforeEach
    void setUp() {
        // Arrange
        OpenTriviaDBProperties properties = new OpenTriviaDBProperties(
                "https://opentdb.com",
                PROVIDER_NAME
        );

        exceptionMapper = new OpenTriviaDBExceptionMapper(properties);
    }

    @ParameterizedTest
    @MethodSource("responseCodes")
    void handleResponseCode_knownOrUnknownCode_returnsExpectedException(
            int responseCode,
            Class<? extends QuestionsProviderExceptionI> expectedException
    ) {
        // Act
        QuestionsProviderExceptionI result = exceptionMapper.handleResponseCode(responseCode);

        // Assert
        assertInstanceOf(expectedException, result);
        assertEquals(PROVIDER_NAME, result.getSource());
    }

    static Stream<Arguments> responseCodes() {
        return Stream.of(
                Arguments.of(1, QuestionsProviderNoResultsExceptionI.class),
                Arguments.of(2, QuestionsProviderBadGatewayExceptionI.class),
                Arguments.of(3, QuestionsProviderBadGatewayExceptionI.class),
                Arguments.of(4, QuestionsProviderBadGatewayExceptionI.class),
                Arguments.of(5, QuestionsProviderRateLimitExceptionI.class),
                Arguments.of(99, QuestionsProviderBadGatewayExceptionI.class)
        );
    }

    @Test
    void handleHttpException_knownResponseCode_usesResponseCode() {
        // Arrange
        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);

        when(exception.getResponseBodyAs(OpenTriviaDBResponse.class))
                .thenReturn(new OpenTriviaDBResponse(5, List.of()));

        when(exception.getStatusCode()).thenReturn(HttpStatus.BAD_GATEWAY);

        // Act
        QuestionsProviderExceptionI result = exceptionMapper.handleHttpException(exception);

        // Assert
        assertInstanceOf(QuestionsProviderRateLimitExceptionI.class, result);
    }

    @ParameterizedTest
    @MethodSource("httpStatuses")
    void handleHttpException_unusableResponseBody_usesHttpStatus(
            HttpStatus status,
            Class<? extends QuestionsProviderExceptionI> expectedException
    ) {
        // Arrange
        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);

        when(exception.getResponseBodyAs(OpenTriviaDBResponse.class)).thenReturn(null);
        when(exception.getStatusCode()).thenReturn(status);

        // Act
        QuestionsProviderExceptionI result = exceptionMapper.handleHttpException(exception);

        // Assert
        assertInstanceOf(expectedException, result);
    }

    static Stream<Arguments> httpStatuses() {
        return Stream.of(
                Arguments.of(HttpStatus.TOO_MANY_REQUESTS, QuestionsProviderRateLimitExceptionI.class),
                Arguments.of(HttpStatus.SERVICE_UNAVAILABLE, QuestionsProviderUnavailableExceptionI.class),
                Arguments.of(HttpStatus.BAD_REQUEST, QuestionsProviderBadGatewayExceptionI.class)
        );
    }

    @Test
    void handleHttpException_unreadableResponseBody_usesHttpStatus() {
        // Arrange
        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);

        when(exception.getResponseBodyAs(OpenTriviaDBResponse.class))
                .thenThrow(new RestClientException("Invalid response body"));

        when(exception.getStatusCode()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);

        // Act
        QuestionsProviderExceptionI result = exceptionMapper.handleHttpException(exception);

        // Assert
        assertInstanceOf(QuestionsProviderUnavailableExceptionI.class, result);
    }

    @Test
    void handleResourceAccessException_connectionFailure_returnsUnavailableException() {
        // Arrange
        ResourceAccessException exception = new ResourceAccessException(
                "Connection refused",
                new IOException("Connection refused")
        );

        // Act
        QuestionsProviderExceptionI result =
                exceptionMapper.handleResourceAccessException(exception);

        // Assert
        assertAll(
                () -> assertInstanceOf(QuestionsProviderUnavailableExceptionI.class, result),
                () -> assertEquals(PROVIDER_NAME, result.getSource()),
                () -> assertSame(exception, result.getCause())
        );
    }

    @Test
    void handleRestClientException_clientFailure_returnsBadGatewayException() {
        // Arrange
        RestClientException exception = new RestClientException("Unexpected client error");

        // Act
        QuestionsProviderExceptionI result =
                exceptionMapper.handleRestClientException(exception);

        // Assert
        assertAll(
                () -> assertInstanceOf(QuestionsProviderBadGatewayExceptionI.class, result),
                () -> assertEquals(PROVIDER_NAME, result.getSource()),
                () -> assertSame(exception, result.getCause())
        );
    }

    @Test
    void emptyResponse_returnsBadGatewayException() {
        // Act
        QuestionsProviderExceptionI result = exceptionMapper.emptyResponse();

        // Assert
        assertInstanceOf(QuestionsProviderBadGatewayExceptionI.class, result);
    }

    @Test
    void noResults_returnsNoResultsException() {
        // Act
        QuestionsProviderExceptionI result = exceptionMapper.noResults();

        // Assert
        assertInstanceOf(QuestionsProviderNoResultsExceptionI.class, result);
    }

    @Test
    void invalidQuestionData_returnsBadGatewayExceptionWithCause() {
        // Arrange
        IllegalArgumentException cause = new IllegalArgumentException("Invalid question type");

        // Act
        QuestionsProviderExceptionI result = exceptionMapper.invalidQuestionData(cause);

        // Assert
        assertAll(
                () -> assertInstanceOf(QuestionsProviderBadGatewayExceptionI.class, result),
                () -> assertSame(cause, result.getCause())
        );
    }
}

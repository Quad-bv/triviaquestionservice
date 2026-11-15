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
                PROVIDER_NAME,
                java.time.Duration.ofSeconds(2),
                java.time.Duration.ofSeconds(5)
        );

        exceptionMapper = new OpenTriviaDBExceptionMapper(properties);
    }

    @ParameterizedTest
    @MethodSource("responseCodes")
    void handleResponseCode_knownOrUnknownCode_returnsExpectedException(
            int responseCode,
            Class<? extends QuestionsProviderException> expectedException
    ) {
        // Act
        QuestionsProviderException result = exceptionMapper.handleResponseCode(responseCode);

        // Assert
        assertInstanceOf(expectedException, result);
        assertEquals(PROVIDER_NAME, result.getSource());
    }

    static Stream<Arguments> responseCodes() {
        return Stream.of(
                Arguments.of(1, QuestionsProviderNoResultsException.class),
                Arguments.of(2, QuestionsProviderBadRequestException.class),
                Arguments.of(3, QuestionsProviderBadRequestException.class),
                Arguments.of(4, QuestionsProviderBadRequestException.class),
                Arguments.of(5, QuestionsProviderRateLimitException.class),
                Arguments.of(99, QuestionsProviderBadGatewayException.class)
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
        QuestionsProviderException result = exceptionMapper.handleHttpException(exception);

        // Assert
        assertInstanceOf(QuestionsProviderRateLimitException.class, result);
    }

    @ParameterizedTest
    @MethodSource("httpStatuses")
    void handleHttpException_unusableResponseBody_usesHttpStatus(
            HttpStatus status,
            Class<? extends QuestionsProviderException> expectedException
    ) {
        // Arrange
        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);

        when(exception.getResponseBodyAs(OpenTriviaDBResponse.class)).thenReturn(null);
        when(exception.getStatusCode()).thenReturn(status);

        // Act
        QuestionsProviderException result = exceptionMapper.handleHttpException(exception);

        // Assert
        assertInstanceOf(expectedException, result);
    }

    static Stream<Arguments> httpStatuses() {
        return Stream.of(
                Arguments.of(HttpStatus.TOO_MANY_REQUESTS, QuestionsProviderRateLimitException.class),
                Arguments.of(HttpStatus.SERVICE_UNAVAILABLE, QuestionsProviderUnavailableException.class),
                Arguments.of(HttpStatus.BAD_REQUEST, QuestionsProviderBadGatewayException.class)
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
        QuestionsProviderException result = exceptionMapper.handleHttpException(exception);

        // Assert
        assertInstanceOf(QuestionsProviderUnavailableException.class, result);
    }

    @Test
    void handleResourceAccessException_connectionFailure_returnsUnavailableException() {
        // Arrange
        ResourceAccessException exception = new ResourceAccessException(
                "Connection refused",
                new IOException("Connection refused")
        );

        // Act
        QuestionsProviderException result =
                exceptionMapper.handleResourceAccessException(exception);

        // Assert
        assertAll(
                () -> assertInstanceOf(QuestionsProviderUnavailableException.class, result),
                () -> assertEquals(PROVIDER_NAME, result.getSource()),
                () -> assertSame(exception, result.getCause())
        );
    }

    @Test
    void handleRestClientException_clientFailure_returnsBadGatewayException() {
        // Arrange
        RestClientException exception = new RestClientException("Unexpected client error");

        // Act
        QuestionsProviderException result =
                exceptionMapper.handleRestClientException(exception);

        // Assert
        assertAll(
                () -> assertInstanceOf(QuestionsProviderBadGatewayException.class, result),
                () -> assertEquals(PROVIDER_NAME, result.getSource()),
                () -> assertSame(exception, result.getCause())
        );
    }

    @Test
    void emptyResponse_returnsBadGatewayException() {
        // Act
        QuestionsProviderException result = exceptionMapper.emptyResponse();

        // Assert
        assertInstanceOf(QuestionsProviderBadGatewayException.class, result);
    }

    @Test
    void noResults_returnsNoResultsException() {
        // Act
        QuestionsProviderException result = exceptionMapper.noResults();

        // Assert
        assertInstanceOf(QuestionsProviderNoResultsException.class, result);
    }

    @Test
    void invalidQuestionData_returnsBadGatewayExceptionWithCause() {
        // Arrange
        IllegalArgumentException cause = new IllegalArgumentException("Invalid question type");

        // Act
        QuestionsProviderException result = exceptionMapper.invalidQuestionData(cause);

        // Assert
        assertAll(
                () -> assertInstanceOf(QuestionsProviderBadGatewayException.class, result),
                () -> assertSame(cause, result.getCause())
        );
    }
}

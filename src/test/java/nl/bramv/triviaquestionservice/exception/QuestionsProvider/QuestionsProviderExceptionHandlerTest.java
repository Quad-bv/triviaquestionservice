package nl.bramv.triviaquestionservice.exception.QuestionsProvider;

import nl.bramv.triviaquestionservice.exception.logging.ApplicationExceptionLogger;
import nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions.*;
import nl.bramv.triviaquestionservice.exception.questionsprovider.handler.QuestionsProviderExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionsProviderExceptionHandlerTest {

    private ApplicationExceptionLogger logHandler;
    private QuestionsProviderExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        // Arrange
        logHandler = mock(ApplicationExceptionLogger.class);
        exceptionHandler = new QuestionsProviderExceptionHandler(logHandler);
    }

    @ParameterizedTest
    @MethodSource("handlerScenarios")
    void handleException_returnsExpectedProblemDetailAndLogsException(
            HandlerScenario scenario
    ) {
        // Act
        ProblemDetail result = scenario.handler().apply(exceptionHandler);

        // Assert
        assertAll(
                () -> assertEquals(scenario.expectedStatus().value(), result.getStatus()),
                () -> assertEquals(scenario.exception().getMessage(), result.getDetail())
        );

        verify(logHandler).log(scenario.exception(), scenario.exception());
    }

    static Stream<HandlerScenario> handlerScenarios() {
        var badGateway = new QuestionsProviderBadGatewayException(
                "OpenTriviaDB", "Bad gateway");
        var unavailable = new QuestionsProviderUnavailableException(
                "OpenTriviaDB", "Unavailable");
        var rateLimit = new QuestionsProviderRateLimitException(
                "OpenTriviaDB", "Rate limit reached");
        var noResults = new QuestionsProviderNoResultsException(
                "OpenTriviaDB", "No results found");
        var generic = new GenericProviderException(
                "OpenTriviaDB", "Unexpected provider failure");

        return Stream.of(
                new HandlerScenario(
                        badGateway,
                        HttpStatus.BAD_GATEWAY,
                        handler -> handler.handleBadGateway(badGateway)
                ),
                new HandlerScenario(
                        unavailable,
                        HttpStatus.SERVICE_UNAVAILABLE,
                        handler -> handler.handleUnavailable(unavailable)
                ),
                new HandlerScenario(
                        rateLimit,
                        HttpStatus.TOO_MANY_REQUESTS,
                        handler -> handler.handleRateLimit(rateLimit)
                ),
                new HandlerScenario(
                        noResults,
                        HttpStatus.NOT_FOUND,
                        handler -> handler.handleNoResults(noResults)
                ),
                new HandlerScenario(
                        generic,
                        HttpStatus.BAD_GATEWAY,
                        handler -> handler.handleProviderFailure(generic)
                )
        );
    }

    private record HandlerScenario(
            QuestionsProviderException exception,
            HttpStatus expectedStatus,
            Function<QuestionsProviderExceptionHandler, ProblemDetail> handler
    ) {}

    private static class GenericProviderException extends QuestionsProviderException {
        GenericProviderException(String providerName, String message) {
            super(providerName, message, null, null);
        }
    }
}
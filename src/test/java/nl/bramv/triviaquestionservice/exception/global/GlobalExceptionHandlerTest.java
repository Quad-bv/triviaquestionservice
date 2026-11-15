
package nl.bramv.triviaquestionservice.exception.global;

import jakarta.validation.ConstraintViolationException;
import nl.bramv.triviaquestionservice.exception.global.handler.GlobalExceptionHandler;
import nl.bramv.triviaquestionservice.exception.logging.ApplicationExceptionLogger;
import nl.bramv.triviaquestionservice.exception.logging.LogLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private static final String SOURCE = "QuestionsController";

    private ApplicationExceptionLogger logHandler;
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        // Arrange
        logHandler = mock(ApplicationExceptionLogger.class);
        exceptionHandler = new GlobalExceptionHandler(logHandler);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "getQuestions.amount: must be greater than or equal to 1",
            "getQuestions.amount: must be less than or equal to 50",
            "getQuestions.amount: must not be null"
    })
    void handleConstraintViolation_returnsBadRequestProblemDetailAndLogsException(
            String message
    ) {
        // Arrange
        ConstraintViolationException exception =
                new ConstraintViolationException(message, null);

        // Act
        ProblemDetail result = exceptionHandler.handleConstraintViolation(exception);

        // Assert
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus()),
                () -> assertEquals(message, result.getDetail())
        );

        verify(logHandler).log(SOURCE, LogLevel.WARN, exception);
    }
}

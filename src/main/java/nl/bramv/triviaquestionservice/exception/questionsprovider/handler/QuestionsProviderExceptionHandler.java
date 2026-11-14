package nl.bramv.triviaquestionservice.exception.questionsprovider.handler;

import nl.bramv.triviaquestionservice.exception.logging.ExceptionLogHandler;
import nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class QuestionsProviderExceptionHandler {

    private final ExceptionLogHandler logHandler;

    public QuestionsProviderExceptionHandler(ExceptionLogHandler logHandler) {
        this.logHandler = logHandler;
    }

    // Handles provider exceptions that do not have a more specific HTTP mapping.
    @ExceptionHandler(QuestionsProviderException.class)
    public ProblemDetail handleProviderFailure(QuestionsProviderException exception) {
        return problem(HttpStatus.BAD_GATEWAY, exception);
    }

    @ExceptionHandler(QuestionsProviderUnavailableException.class)
    public ProblemDetail handleUnavailable(
            QuestionsProviderUnavailableException exception) {

        return problem(HttpStatus.SERVICE_UNAVAILABLE, exception);
    }

    @ExceptionHandler(QuestionsProviderRateLimitException.class)
    public ProblemDetail handleRateLimit(
            QuestionsProviderRateLimitException exception) {

        return problem(HttpStatus.TOO_MANY_REQUESTS, exception);
    }

    @ExceptionHandler(QuestionsProviderNoResultsException.class)
    public ProblemDetail handleNoResults(
            QuestionsProviderNoResultsException exception) {

        return problem(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(QuestionsProviderBadGatewayException.class)
    public ProblemDetail handleBadGateway(
            QuestionsProviderBadGatewayException exception) {

        return problem(HttpStatus.BAD_GATEWAY, exception);
    }

    // Logs the exception and creates the corresponding HTTP error response.
    private ProblemDetail problem(HttpStatus status, QuestionsProviderException exception) {

        logHandler.log(exception);

        return ProblemDetail.forStatusAndDetail(
                status,
                exception.getMessage()
        );
    }
}


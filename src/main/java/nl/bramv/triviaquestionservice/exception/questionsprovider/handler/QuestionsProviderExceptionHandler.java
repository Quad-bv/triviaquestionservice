package nl.bramv.triviaquestionservice.exception.questionsprovider.handler;

import nl.bramv.triviaquestionservice.controller.QuestionsController;
import nl.bramv.triviaquestionservice.exception.logging.ApplicationExceptionLogger;
import nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = QuestionsController.class)
public class QuestionsProviderExceptionHandler {

    private final ApplicationExceptionLogger logHandler;

    public QuestionsProviderExceptionHandler(ApplicationExceptionLogger logHandler) {
        this.logHandler = logHandler;
    }

    // Handles provider exceptions that do not have a more specific HTTP mapping.
    @ExceptionHandler(QuestionsProviderExceptionI.class)
    public ProblemDetail handleProviderFailure(QuestionsProviderExceptionI exception) {
        return problem(HttpStatus.BAD_GATEWAY, exception);
    }

    @ExceptionHandler(QuestionsProviderUnavailableExceptionI.class)
    public ProblemDetail handleUnavailable(
            QuestionsProviderUnavailableExceptionI exception) {

        return problem(HttpStatus.SERVICE_UNAVAILABLE, exception);
    }

    @ExceptionHandler(QuestionsProviderRateLimitExceptionI.class)
    public ProblemDetail handleRateLimit(
            QuestionsProviderRateLimitExceptionI exception) {

        return problem(HttpStatus.TOO_MANY_REQUESTS, exception);
    }

    @ExceptionHandler(QuestionsProviderNoResultsExceptionI.class)
    public ProblemDetail handleNoResults(
            QuestionsProviderNoResultsExceptionI exception) {

        return problem(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(QuestionsProviderBadGatewayExceptionI.class)
    public ProblemDetail handleBadGateway(
            QuestionsProviderBadGatewayExceptionI exception) {

        return problem(HttpStatus.BAD_GATEWAY, exception);
    }

    // Logs the exception and creates the corresponding HTTP error response.
    private ProblemDetail problem(HttpStatus status, QuestionsProviderExceptionI exception) {

        logHandler.log(exception, exception);

        return ProblemDetail.forStatusAndDetail(
                status,
                exception.getMessage()
        );
    }
}


package nl.bramv.triviaquestionservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TriviaDBExceptionHandler{

    private static final Logger log = LoggerFactory.getLogger(TriviaDBExceptionHandler.class);

    @ExceptionHandler (TriviaDBUnreachableException.class)
    public ProblemDetail handleUnreachable (TriviaDBUnreachableException exception){
        log.warn("OpenTriviaDB unreachable", exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, "OpenTriviaDB unreachable");
    }

    @ExceptionHandler (TriviaDBUnavailableException.class)
    public ProblemDetail handleBadResponse (TriviaDBUnavailableException exception){
        log.warn("OpenTriviaDB unavailable", exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, "OpenTriviaDB unavailable");
    }

    @ExceptionHandler(TriviaDBException.class)
    public ProblemDetail handleApiError(TriviaDBException exception) {

        log.warn("OpenTriviaDB API error: {}", exception.getResponseCode());

        HttpStatus status = switch (exception.getResponseCode()) {
            case 1 -> HttpStatus.NOT_FOUND;
            case 2 -> HttpStatus.BAD_REQUEST;
            case 3, 4 -> HttpStatus.BAD_REQUEST;
            case 5 -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.BAD_GATEWAY;
        };

        return ProblemDetail.forStatusAndDetail(
                status,
                exception.getMessage()
        );
    }
}


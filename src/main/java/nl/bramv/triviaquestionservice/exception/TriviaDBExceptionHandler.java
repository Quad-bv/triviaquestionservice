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

    @ExceptionHandler (TriviaDBUnavailableException.class)
    public ProblemDetail handleUnavailable (TriviaDBUnavailableException exception){
        log.warn("OpenTriviaDB unavailable", exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, "OpenTriviaDB unavailable");
    }

    @ExceptionHandler (TriviaDBRateLimitException.class)
    public ProblemDetail handeRateLimited (TriviaDBRateLimitException exception){
        log.warn("OpenTriviaDB received to many requests", exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, "OpenTriviaDB received to many requests");
    }

    @ExceptionHandler (TriviaDBBadGatewayException.class)
    public ProblemDetail handleUnreachable (TriviaDBBadGatewayException exception){
        log.warn("OpenTriviaDB unreachable", exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, "OpenTriviaDB bad Gateway");
    }
}


package nl.bramv.triviaquestionservice.exception;

public class TriviaDBRateLimitException extends RuntimeException {
    public TriviaDBRateLimitException(Throwable cause){
        super("OpenTriviaDB received to many requests", cause);
    }
}

package nl.bramv.triviaquestionservice.exception;

public class TriviaDBUnavailableException extends RuntimeException {
    public TriviaDBUnavailableException(Throwable cause){
        super("OpenTriviaDB is unavailable", cause);
    }
}

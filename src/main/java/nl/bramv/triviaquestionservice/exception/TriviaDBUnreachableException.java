package nl.bramv.triviaquestionservice.exception;

public class TriviaDBUnreachableException extends RuntimeException {
    public TriviaDBUnreachableException(Throwable cause){
        super("OpenTriviaDB is unreachable", cause);
    }
}

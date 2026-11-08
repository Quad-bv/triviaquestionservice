package nl.bramv.triviaquestionservice.exception;

public class TriviaDBBadGatewayException extends RuntimeException {
    public TriviaDBBadGatewayException(Throwable cause){
        super("OpenTriviaDB bad Gateway", cause);
    }
}

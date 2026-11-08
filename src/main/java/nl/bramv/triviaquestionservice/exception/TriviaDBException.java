package nl.bramv.triviaquestionservice.exception;

public class TriviaDBException extends RuntimeException{

    private final int responseCode;

    public TriviaDBException(int responseCode) {
        super("OpenTriviaDB returned response code: " + responseCode);
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }
}

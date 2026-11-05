package nl.bramv.triviaquestionservice.model;

public class CheckAnswersRequest {
    private final String selectedAnswer;
    private final String verificationToken;

    public CheckAnswersRequest (String selectedAnswer, String verificationToken) {
        this.selectedAnswer = selectedAnswer;
        this.verificationToken = verificationToken;
    }
}

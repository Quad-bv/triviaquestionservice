package nl.bramv.triviaquestionservice.model;

import java.util.List;

public class QuestionResponse {
    private final String type;
    private final String difficulty;
    private final String category;
    private final String question;
    private final String verificationToken;
    private final List<String> answers;
    public QuestionResponse(
            String type,
            String difficulty,
            String category,
            String question,
            String verificationToken,
            List<String> answers) {

        this.type = type;
        this.difficulty = difficulty;
        this.category = category;
        this.question = question;
        this.verificationToken = verificationToken;
        this.answers = answers;
    }
}

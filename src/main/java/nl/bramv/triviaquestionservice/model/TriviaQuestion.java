package nl.bramv.triviaquestionservice.model;

import java.util.List;

public class TriviaQuestion {
    private final String type;
    private final String difficulty;
    private final String category;
    private final String question;
    private final String correctAnswer;
    private final List<String> incorrectAnswers;

    public TriviaQuestion(
            String type,
            String difficulty,
            String category,
            String question,
            String correctAnswer,
            List<String> incorrectAnswers) {

        this.type = type;
        this.difficulty = difficulty;
        this.category = category;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
    }
}



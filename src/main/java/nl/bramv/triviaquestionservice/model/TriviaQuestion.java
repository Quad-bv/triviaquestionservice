package nl.bramv.triviaquestionservice.model;

import java.util.List;

public record TriviaQuestion(
        String type,
        String difficulty,
        String category,
        String question,
        String correctAnswer,
        List<String> incorrectAnswers
) {
}
package nl.bramv.triviaquestionservice.model;

import nl.bramv.triviaquestionservice.enums.Difficulty;
import nl.bramv.triviaquestionservice.enums.QuestionType;

import java.util.List;

public record Question(
        QuestionType type,
        Difficulty difficulty,
        String category,
        String question,
        String correctAnswer,
        List<String> incorrectAnswers){
    public Question {
        incorrectAnswers = List.copyOf(incorrectAnswers);
    }
}

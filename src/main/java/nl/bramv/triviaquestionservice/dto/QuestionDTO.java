package nl.bramv.triviaquestionservice.dto;

import nl.bramv.triviaquestionservice.enums.Difficulty;
import nl.bramv.triviaquestionservice.enums.QuestionType;

import java.util.List;

public record QuestionDTO(
        QuestionType type,
        Difficulty difficulty,
        String category,
        String question,
        String correctAnswer,
        List<String> incorrectAnswers) {
}

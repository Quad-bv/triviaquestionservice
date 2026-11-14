package nl.bramv.triviaquestionservice.model;

import nl.bramv.triviaquestionservice.enums.Difficulty;
import nl.bramv.triviaquestionservice.enums.QuestionType;

import java.util.List;

public record QuestionsResponse(
        QuestionType type,
        Difficulty difficulty,
        String category,
        String question,
        String verificationToken,
        List<String> answers
) {
}

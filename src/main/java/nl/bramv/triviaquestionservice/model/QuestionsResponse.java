package nl.bramv.triviaquestionservice.model;

import nl.bramv.triviaquestionservice.model.enums.Difficulty;
import nl.bramv.triviaquestionservice.model.enums.QuestionType;

import java.util.List;

public record QuestionsResponse(
        QuestionType type,
        Difficulty difficulty,
        String category,
        String question,
        String verificationToken,
        List<String> answers
) {
    public QuestionsResponse{
        answers = List.copyOf(answers);
    }
}

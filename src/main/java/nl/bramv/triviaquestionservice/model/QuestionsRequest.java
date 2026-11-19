package nl.bramv.triviaquestionservice.model;

import nl.bramv.triviaquestionservice.model.enums.Difficulty;
import nl.bramv.triviaquestionservice.model.enums.QuestionType;

public record QuestionsRequest(
        int amount,
        Integer category,
        Difficulty difficulty,
        QuestionType type,
        String token
)
{}

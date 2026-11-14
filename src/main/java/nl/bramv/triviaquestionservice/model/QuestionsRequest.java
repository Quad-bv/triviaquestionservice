package nl.bramv.triviaquestionservice.model;

import nl.bramv.triviaquestionservice.enums.Difficulty;
import nl.bramv.triviaquestionservice.enums.QuestionType;

public record QuestionsRequest(
        int amount,
        Integer category,
        Difficulty difficulty,
        QuestionType type,
        String token
)
{}

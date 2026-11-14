package nl.bramv.triviaquestionservice.service.provider.opentriviadb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenTriviaDBQuestion(
        String type,
        String difficulty,
        String category,
        String question,
        @JsonProperty("correct_answer") String correctAnswer,
        @JsonProperty("incorrect_answers") List<String> incorrectAnswers)
{}

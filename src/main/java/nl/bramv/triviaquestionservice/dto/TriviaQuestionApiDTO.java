package nl.bramv.triviaquestionservice.dto;

import java.util.List;

public record TriviaQuestionApiDTO(
        String type,
        String difficulty,
        String category,
        String question,
        String correct_answer,
        List<String> incorrect_answers) {

}

package nl.bramv.triviaquestionservice.model;

import java.util.List;

public record QuestionsResponse(
        String type,
        String difficulty,
        String category,
        String question,
        String verificationToken,
        List<String> answers
) {
}

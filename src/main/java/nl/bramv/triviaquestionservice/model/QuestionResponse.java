package nl.bramv.triviaquestionservice.model;

import java.util.List;

public record QuestionResponse(
        String type,
        String difficulty,
        String category,
        String question,
        String verificationToken,
        List<String> answers
) {
}

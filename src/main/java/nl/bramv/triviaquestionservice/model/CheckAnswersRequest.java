package nl.bramv.triviaquestionservice.model;

import jakarta.validation.constraints.NotBlank;

public record CheckAnswersRequest(
        @NotBlank(message = "selectedAnswer is required")
        String selectedAnswer,
        @NotBlank(message = "verificationToken is required")
        String verificationToken
) {
}

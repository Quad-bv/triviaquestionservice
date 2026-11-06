package nl.bramv.triviaquestionservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TriviaApiResponseDTO(
        @JsonProperty("response_code") int responseCode,
        List<TriviaQuestionApiDTO> results) {
}

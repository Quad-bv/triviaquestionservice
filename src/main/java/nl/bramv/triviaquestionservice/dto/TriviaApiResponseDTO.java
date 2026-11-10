package nl.bramv.triviaquestionservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TriviaApiResponseDTO(
        @JsonProperty("results") List<TriviaQuestionApiDTO> questions) {
}

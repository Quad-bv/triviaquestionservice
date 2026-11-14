package nl.bramv.triviaquestionservice.service.provider.opentriviadb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenTriviaDBResponse(
        @JsonProperty("response_code") int responseCode, List<OpenTriviaDBQuestion> results)
{}


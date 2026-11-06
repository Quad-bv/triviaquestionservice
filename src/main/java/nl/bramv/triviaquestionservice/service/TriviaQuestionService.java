package nl.bramv.triviaquestionservice.service;

import nl.bramv.triviaquestionservice.dto.TriviaApiResponseDTO;
import nl.bramv.triviaquestionservice.dto.TriviaQuestionApiDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class TriviaQuestionService {

    private final RestClient restClient;

    public TriviaQuestionService(
            RestClient.Builder builder,
            @Value("${opentdb.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public List<TriviaQuestionApiDTO> fetchQuestions(int amount) {
        TriviaApiResponseDTO response = restClient.get()
                .uri("/api.php?amount={amount}", amount)
                .retrieve()
                .body(TriviaApiResponseDTO.class);

        return response.results();
    }
}

package nl.bramv.triviaquestionservice.service;

import nl.bramv.triviaquestionservice.dto.TriviaApiResponseDTO;
import nl.bramv.triviaquestionservice.dto.TriviaQuestionApiDTO;
import nl.bramv.triviaquestionservice.exception.TriviaDBException;
import nl.bramv.triviaquestionservice.exception.TriviaDBUnavailableException;
import nl.bramv.triviaquestionservice.exception.TriviaDBUnreachableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Service
public class TriviaDBService {

    private final RestClient restClient;

    public TriviaDBService(

            // setup Restclient connection to TriviaDB
            RestClient.Builder builder,
            @Value("${opentdb.base-url}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public List<TriviaQuestionApiDTO> fetchQuestions(int amount) {

        TriviaApiResponseDTO response;

        try {
             response = restClient.get()
                    .uri("/api.php?amount={amount}", amount)
                    .retrieve()
                    .body(TriviaApiResponseDTO.class);
        } catch (ResourceAccessException exception){
            throw new TriviaDBUnavailableException(exception);
        } catch (RestClientResponseException exception){
            throw new TriviaDBUnreachableException(exception);
        }

        if (response.responseCode() != 0) {
            throw new TriviaDBException(response.responseCode());
        }

        assert response != null;
        return response.questions();
    }

    public boolean CheckFor200ErrorCodes(TriviaApiResponseDTO responseDTO)
    {
        return true;
    }
}

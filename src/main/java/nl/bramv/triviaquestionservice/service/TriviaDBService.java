package nl.bramv.triviaquestionservice.service;

import nl.bramv.triviaquestionservice.dto.TriviaApiResponseDTO;
import nl.bramv.triviaquestionservice.dto.TriviaQuestionApiDTO;
import nl.bramv.triviaquestionservice.exception.TriviaDBBadGatewayException;
import nl.bramv.triviaquestionservice.exception.TriviaDBRateLimitException;
import nl.bramv.triviaquestionservice.exception.TriviaDBUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

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


        } catch (ResourceAccessException exception) {
            throw new TriviaDBUnavailableException(exception);

        } catch (HttpClientErrorException.TooManyRequests exception) {
            throw new TriviaDBRateLimitException(exception);

        } catch (RestClientException exception) {
            throw new TriviaDBBadGatewayException(exception);
        }

        if (response == null || response.questions() == null)
        {
            throw new TriviaDBBadGatewayException("OpenTriviaDB returned no questions");
        }

        return response.questions();
    }
}

package nl.bramv.triviaquestionservice.service.provider.opentriviadb.client;

import nl.bramv.triviaquestionservice.service.provider.opentriviadb.model.OpenTriviaDBResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/")
public interface IOpenTriviaDBClient {

    @GetExchange("api.php")
    OpenTriviaDBResponse getQuestions(
            @RequestParam int amount,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String token);
}

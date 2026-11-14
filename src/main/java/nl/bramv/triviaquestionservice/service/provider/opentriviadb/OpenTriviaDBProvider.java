package nl.bramv.triviaquestionservice.service.provider.opentriviadb;
import nl.bramv.triviaquestionservice.dto.QuestionDTO;
import nl.bramv.triviaquestionservice.model.QuestionsRequest;
import nl.bramv.triviaquestionservice.service.provider.IQuestionsProvider;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.client.IOpenTriviaDBClient;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.exception.OpenTriviaDBExceptionHandler;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.mapper.OpenTriviaDBQuestionMapper;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.model.OpenTriviaDBResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class OpenTriviaDBProvider implements IQuestionsProvider {

    private final IOpenTriviaDBClient client;
    private final OpenTriviaDBQuestionMapper mapper;
    private final OpenTriviaDBExceptionHandler exceptionHandler;

    public OpenTriviaDBProvider(IOpenTriviaDBClient client, OpenTriviaDBQuestionMapper mapper, OpenTriviaDBExceptionHandler exceptionHandler) {
        this.client = client;
        this.mapper = mapper;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public List<QuestionDTO> getQuestions(QuestionsRequest request) {

        OpenTriviaDBResponse response = null;

        try {
            response = client.getQuestions(
                    request.amount(),
                    request.category(),
                    request.difficulty() != null
                            ? request.difficulty().name().toLowerCase()
                            : null,
                    request.type() != null
                            ? request.type().name().toLowerCase()
                            : null,
                    request.token()
            );

        } catch (HttpStatusCodeException exception) {
            throw exceptionHandler.handleHTTPException(exception);

        } catch (ResourceAccessException exception) {
            throw exceptionHandler.handleResourceAccessException(exception);

        } catch (RestClientException exception) {
            throw exceptionHandler.handleRestClientException(exception);
        }

        return response.results()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }
}

package nl.bramv.triviaquestionservice.service.provider.opentriviadb;
import nl.bramv.triviaquestionservice.model.Question;
import nl.bramv.triviaquestionservice.model.QuestionsRequest;
import nl.bramv.triviaquestionservice.service.provider.IQuestionsProvider;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.client.IOpenTriviaDBClient;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.exception.OpenTriviaDBExceptionMapper;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.mapper.OpenTriviaDBQuestionMapper;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.model.OpenTriviaDBResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Locale;

@Service
@ConditionalOnProperty(name = "questions.provider", havingValue = "opentriviadb", matchIfMissing = true)
public class OpenTriviaDBProvider implements IQuestionsProvider {

    private final IOpenTriviaDBClient client;
    private final OpenTriviaDBQuestionMapper mapper;
    private final OpenTriviaDBExceptionMapper exceptionMapper;

    public OpenTriviaDBProvider(IOpenTriviaDBClient client, OpenTriviaDBQuestionMapper mapper, OpenTriviaDBExceptionMapper exceptionMapper) {
        this.client = client;
        this.mapper = mapper;
        this.exceptionMapper = exceptionMapper;
    }

    @Override
    public List<Question> getQuestions(QuestionsRequest request) {

        OpenTriviaDBResponse response = fetch(request);

        checkForErrorCode(response);

        try {
            return response.results().stream().map(mapper::toQuestionModel).toList();
        } catch (IllegalArgumentException exception) {
            throw exceptionMapper.invalidQuestionData(exception);
        }
    }

    // Calls OpenTriviaDB and translates client-level failures into application exceptions.
    private OpenTriviaDBResponse fetch(QuestionsRequest request) {
        try {
            return client.getQuestions(
                    request.amount(),
                    request.category(),
                    toWireValue(request.difficulty()),
                    toWireValue(request.type()),
                    request.token()
            );

        } catch (HttpStatusCodeException exception) {
            throw exceptionMapper.handleHttpException(exception);

        } catch (ResourceAccessException exception) {
            throw exceptionMapper.handleResourceAccessException(exception);

        } catch (RestClientException exception) {
            throw exceptionMapper.handleRestClientException(exception);
        }
    }

    // A successful HTTP response can still contain an OpenTriviaDB error code.
    private void checkForErrorCode(OpenTriviaDBResponse response) {
        if (response == null) {
            throw exceptionMapper.emptyResponse();
        }
        if (response.responseCode() != 0) {
            throw exceptionMapper.handleResponseCode(response.responseCode());
        }
        if (response.results().isEmpty()) {
            throw exceptionMapper.noResults();
        }
    }

    // OpenTriviaDB expects lowercase enum values such as "easy" and "boolean".
    private static String toWireValue(Enum<?> value) {
        return value == null ? null : value.name().toLowerCase(Locale.ROOT);
    }
}

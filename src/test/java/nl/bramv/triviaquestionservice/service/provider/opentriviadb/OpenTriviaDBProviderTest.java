package nl.bramv.triviaquestionservice.service.provider.opentriviadb;

import nl.bramv.triviaquestionservice.enums.Difficulty;
import nl.bramv.triviaquestionservice.enums.QuestionType;
import nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions.*;
import nl.bramv.triviaquestionservice.model.Question;
import nl.bramv.triviaquestionservice.model.QuestionsRequest;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.client.IOpenTriviaDBClient;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.config.OpenTriviaDBProperties;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.exception.OpenTriviaDBExceptionMapper;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.mapper.OpenTriviaDBQuestionMapper;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.model.OpenTriviaDBQuestion;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.model.OpenTriviaDBResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OpenTriviaDBProviderTest {

    private IOpenTriviaDBClient client;
    private OpenTriviaDBProvider provider;

    @BeforeEach
    void setUp() {
        client = mock(IOpenTriviaDBClient.class);

        OpenTriviaDBProperties properties =
                new OpenTriviaDBProperties("https://opentdb.com", "OpenTriviaDB");

        OpenTriviaDBQuestionMapper questionMapper = new OpenTriviaDBQuestionMapper();
        OpenTriviaDBExceptionMapper exceptionMapper =
                new OpenTriviaDBExceptionMapper(properties);

        provider = new OpenTriviaDBProvider(client, questionMapper, exceptionMapper);
    }

    @Test
    void getQuestions_validResponse_returnsMappedQuestions() {
        // Arrange
        QuestionsRequest request = new QuestionsRequest(
                1, 9, Difficulty.HARD, QuestionType.MULTIPLE, "session-token"
        );

        OpenTriviaDBQuestion externalQuestion = new OpenTriviaDBQuestion(
                "multiple",
                "hard",
                "General Knowledge",
                "What is the capital of France?",
                "Paris",
                List.of("London", "Berlin", "Madrid")
        );

        when(client.getQuestions(1, 9, "hard", "multiple", "session-token"))
                .thenReturn(new OpenTriviaDBResponse(0, List.of(externalQuestion)));

        // Act
        List<Question> result = provider.getQuestions(request);

        // Assert
        assertEquals(1, result.size());
        assertEquals(QuestionType.MULTIPLE, result.getFirst().type());
        assertEquals(Difficulty.HARD, result.getFirst().difficulty());
        assertEquals("Paris", result.getFirst().correctAnswer());

        verify(client).getQuestions(1, 9, "hard", "multiple", "session-token");
    }

    @Test
    void getQuestions_optionalParametersAreNull_sendsNullValuesToClient() {
        // Arrange
        QuestionsRequest request = new QuestionsRequest(2, null, null, null, null);

        OpenTriviaDBQuestion question = new OpenTriviaDBQuestion(
                "boolean", "easy", "General Knowledge",
                "Question?", "True", List.of("False")
        );

        when(client.getQuestions(2, null, null, null, null))
                .thenReturn(new OpenTriviaDBResponse(0, List.of(question)));

        // Act
        List<Question> result = provider.getQuestions(request);

        // Assert
        assertEquals(1, result.size());
        verify(client).getQuestions(2, null, null, null, null);
    }

    @ParameterizedTest
    @MethodSource("errorResponseCodes")
    void getQuestions_errorResponseCode_throwsExpectedException(
            int responseCode,
            Class<? extends QuestionsProviderExceptionI> expectedException
    ) {
        // Arrange
        QuestionsRequest request = new QuestionsRequest(1, null, null, null, null);

        when(client.getQuestions(1, null, null, null, null))
                .thenReturn(new OpenTriviaDBResponse(responseCode, List.of()));

        // Act & Assert
        assertThrows(
                expectedException,
                () -> provider.getQuestions(request)
        );
    }

    static Stream<Arguments> errorResponseCodes() {
        return Stream.of(
                Arguments.of(1, QuestionsProviderNoResultsExceptionI.class),
                Arguments.of(2, QuestionsProviderBadGatewayExceptionI.class),
                Arguments.of(3, QuestionsProviderBadGatewayExceptionI.class),
                Arguments.of(4, QuestionsProviderBadGatewayExceptionI.class),
                Arguments.of(5, QuestionsProviderRateLimitExceptionI.class),
                Arguments.of(99, QuestionsProviderBadGatewayExceptionI.class)
        );
    }

    @Test
    void getQuestions_nullResponse_throwsBadGatewayException() {
        // Arrange
        QuestionsRequest request = new QuestionsRequest(1, null, null, null, null);

        when(client.getQuestions(1, null, null, null, null)).thenReturn(null);

        // Act & Assert
        assertThrows(
                QuestionsProviderBadGatewayExceptionI.class,
                () -> provider.getQuestions(request)
        );
    }

    @Test
    void getQuestions_nullResults_throwsBadGatewayException() {
        // Arrange
        QuestionsRequest request = new QuestionsRequest(1, null, null, null, null);

        when(client.getQuestions(1, null, null, null, null))
                .thenReturn(new OpenTriviaDBResponse(0, null));

        // Act & Assert
        assertThrows(
                QuestionsProviderBadGatewayExceptionI.class,
                () -> provider.getQuestions(request)
        );
    }

    @Test
    void getQuestions_emptyResults_throwsNoResultsException() {
        // Arrange
        QuestionsRequest request = new QuestionsRequest(1, null, null, null, null);

        when(client.getQuestions(1, null, null, null, null))
                .thenReturn(new OpenTriviaDBResponse(0, List.of()));

        // Act & Assert
        assertThrows(
                QuestionsProviderNoResultsExceptionI.class,
                () -> provider.getQuestions(request)
        );
    }

    @Test
    void getQuestions_invalidQuestionData_throwsBadGatewayException() {
        // Arrange
        QuestionsRequest request = new QuestionsRequest(1, null, null, null, null);

        OpenTriviaDBQuestion invalidQuestion = new OpenTriviaDBQuestion(
                "unknown", "easy", "Category",
                "Question?", "Correct", List.of("Wrong")
        );

        when(client.getQuestions(1, null, null, null, null))
                .thenReturn(new OpenTriviaDBResponse(0, List.of(invalidQuestion)));

        // Act & Assert
        assertThrows(
                QuestionsProviderBadGatewayExceptionI.class,
                () -> provider.getQuestions(request)
        );
    }

    @Test
    void getQuestions_httpError_throwsMappedException() {
        // Arrange
        QuestionsRequest request = new QuestionsRequest(1, null, null, null, null);

        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
        when(exception.getResponseBodyAs(OpenTriviaDBResponse.class)).thenReturn(null);
        when(exception.getStatusCode()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);

        when(client.getQuestions(1, null, null, null, null)).thenThrow(exception);

        // Act & Assert
        assertThrows(
                QuestionsProviderUnavailableExceptionI.class,
                () -> provider.getQuestions(request)
        );
    }

    @Test
    void getQuestions_connectionFailure_throwsUnavailableException() {
        // Arrange
        QuestionsRequest request = new QuestionsRequest(1, null, null, null, null);

        when(client.getQuestions(1, null, null, null, null))
                .thenThrow(new ResourceAccessException("Connection refused"));

        // Act & Assert
        assertThrows(
                QuestionsProviderUnavailableExceptionI.class,
                () -> provider.getQuestions(request)
        );
    }

    @Test
    void getQuestions_unexpectedClientFailure_throwsBadGatewayException() {
        // Arrange
        QuestionsRequest request = new QuestionsRequest(1, null, null, null, null);

        when(client.getQuestions(1, null, null, null, null))
                .thenThrow(new RestClientException("Unexpected client error"));

        // Act & Assert
        assertThrows(
                QuestionsProviderBadGatewayExceptionI.class,
                () -> provider.getQuestions(request)
        );
    }
}

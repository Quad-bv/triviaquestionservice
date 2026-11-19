
package nl.bramv.triviaquestionservice.service;

import nl.bramv.triviaquestionservice.model.enums.Difficulty;
import nl.bramv.triviaquestionservice.model.enums.QuestionType;
import nl.bramv.triviaquestionservice.model.Question;
import nl.bramv.triviaquestionservice.model.QuestionsRequest;
import nl.bramv.triviaquestionservice.model.QuestionsResponse;
import nl.bramv.triviaquestionservice.service.mapping.QuestionsResponseMapper;
import nl.bramv.triviaquestionservice.service.provider.IQuestionsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionsServiceTest {

    private IQuestionsProvider provider;
    private QuestionsResponseMapper responseMapper;
    private QuestionsService questionsService;

    @BeforeEach
    void setUp() {
        provider = mock(IQuestionsProvider.class);
        responseMapper = mock(QuestionsResponseMapper.class);
        questionsService = new QuestionsService(provider, responseMapper);
    }

    @ParameterizedTest
    @MethodSource("questionScenarios")
    void get_returnsMappedResponses(QuestionScenario scenario) {
        // Arrange
        when(provider.getQuestions(scenario.request())).thenReturn(scenario.questions());

        for (int i = 0; i < scenario.questions().size(); i++) {
            when(responseMapper.toResponse(scenario.questions().get(i)))
                    .thenReturn(scenario.expectedResponses().get(i));
        }

        // Act
        List<QuestionsResponse> result = questionsService.get(scenario.request());

        // Assert
        assertEquals(scenario.expectedResponses(), result);
        verify(provider).getQuestions(scenario.request());

        for (Question question : scenario.questions()) {
            verify(responseMapper).toResponse(question);
        }
    }

    private record QuestionScenario(
            QuestionsRequest request,
            List<Question> questions,
            List<QuestionsResponse> expectedResponses
    ) {}

    static Stream<QuestionScenario> questionScenarios() {
        Question firstQuestion = new Question(
                QuestionType.MULTIPLE, Difficulty.HARD,
                "General Knowledge", "What is the capital of France?",
                "Paris", List.of("London", "Berlin", "Madrid")
        );

        QuestionsResponse firstResponse = new QuestionsResponse(
                QuestionType.MULTIPLE, Difficulty.HARD,
                "General Knowledge", "What is the capital of France?",
                "token-1", List.of("Paris", "London", "Berlin", "Madrid")
        );

        Question secondQuestion = new Question(
                QuestionType.BOOLEAN, Difficulty.EASY,
                "General Knowledge", "The Earth is round.",
                "True", List.of("False")
        );

        QuestionsResponse secondResponse = new QuestionsResponse(
                QuestionType.BOOLEAN, Difficulty.EASY,
                "General Knowledge", "The Earth is round.",
                "token-2", List.of("True", "False")
        );

        return Stream.of(
                new QuestionScenario(
                        new QuestionsRequest(1, 9, Difficulty.HARD, QuestionType.MULTIPLE, null),
                        List.of(firstQuestion),
                        List.of(firstResponse)
                ),
                new QuestionScenario(
                        new QuestionsRequest(2, null, null, null, null),
                        List.of(firstQuestion, secondQuestion),
                        List.of(firstResponse, secondResponse)
                )
        );
    }

    @Test
    void get_emptyProviderResult_returnsEmptyList() {
        // Arrange
        QuestionsRequest request = new QuestionsRequest(1, null, null, null, null);
        when(provider.getQuestions(request)).thenReturn(List.of());

        // Act
        List<QuestionsResponse> result = questionsService.get(request);

        // Assert
        assertTrue(result.isEmpty());
        verify(provider).getQuestions(request);
        verifyNoInteractions(responseMapper);
    }
}

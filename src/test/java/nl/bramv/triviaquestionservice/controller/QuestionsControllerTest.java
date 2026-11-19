package nl.bramv.triviaquestionservice.controller;

import nl.bramv.triviaquestionservice.model.enums.Difficulty;
import nl.bramv.triviaquestionservice.model.enums.QuestionType;
import nl.bramv.triviaquestionservice.model.QuestionsRequest;
import nl.bramv.triviaquestionservice.model.QuestionsResponse;
import nl.bramv.triviaquestionservice.service.QuestionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionsControllerTest {

    private QuestionsService questionsService;
    private QuestionsController questionsController;

    @BeforeEach
    void setUp() {
        // Arrange
        questionsService = mock(QuestionsService.class);
        questionsController = new QuestionsController(questionsService);
    }

    @ParameterizedTest
    @MethodSource("requestScenarios")
    void getQuestions_passesRequestToServiceAndReturnsResponse(QuestionsRequest request) {
        // Arrange
        List<QuestionsResponse> expectedResponse = List.of();

        when(questionsService.get(request)).thenReturn(expectedResponse);

        // Act
        List<QuestionsResponse> result = questionsController.getQuestions(
                request.amount(),
                request.category(),
                request.difficulty(),
                request.type(),
                request.token()
        );

        // Assert
        assertSame(expectedResponse, result);
        verify(questionsService).get(request);
    }

    static Stream<QuestionsRequest> requestScenarios() {
        return Stream.of(
                new QuestionsRequest(1, null, null, null, null),
                new QuestionsRequest(10, 9, Difficulty.EASY, QuestionType.MULTIPLE, null),
                new QuestionsRequest(50, 21, Difficulty.HARD, QuestionType.BOOLEAN, "session-token")
        );
    }
}

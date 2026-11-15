package nl.bramv.triviaquestionservice.controller;

import nl.bramv.triviaquestionservice.model.CheckAnswerResponse;
import nl.bramv.triviaquestionservice.model.CheckAnswersRequest;
import nl.bramv.triviaquestionservice.service.AnswerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnswersControllerTest {

    private AnswerService answerService;
    private AnswersController answersController;

    @BeforeEach
    void setUp() {
        // Arrange
        answerService = mock(AnswerService.class);
        answersController = new AnswersController(answerService);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void checkAnswer_returnsServiceResponse(boolean correct) {
        // Arrange
        CheckAnswersRequest request = new CheckAnswersRequest(
                "What is the capital of France?",
                "Paris",
                "verification-token"
        );
        CheckAnswerResponse expectedResponse = new CheckAnswerResponse(correct);

        when(answerService.checkAnswer(request)).thenReturn(expectedResponse);

        // Act
        CheckAnswerResponse result = answersController.checkAnswer(request);

        // Assert
        assertEquals(expectedResponse, result);
        verify(answerService).checkAnswer(request);
    }
}

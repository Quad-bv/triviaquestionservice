
package nl.bramv.triviaquestionservice.service;

import nl.bramv.triviaquestionservice.model.CheckAnswerResponse;
import nl.bramv.triviaquestionservice.model.CheckAnswersRequest;
import nl.bramv.triviaquestionservice.service.helper.IAnswerVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AnswerServiceTest {

    private IAnswerVerificationService answerVerificationService;
    private AnswerService answerService;

    @BeforeEach
    void setUp() {
        answerVerificationService = mock(IAnswerVerificationService.class);
        answerService = new AnswerService(answerVerificationService);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void checkAnswer_returnsVerificationResult(boolean correct) {
        // Arrange
        CheckAnswersRequest request = new CheckAnswersRequest(
                "What is the capital of France?",
                "Paris",
                "verification-token"
        );

        when(answerVerificationService.checkAnswer(
                request.question(),
                request.selectedAnswer(),
                request.verificationToken()
        )).thenReturn(correct);

        // Act
        CheckAnswerResponse result = answerService.checkAnswer(request);

        // Assert
        assertEquals(new CheckAnswerResponse(correct), result);
        verify(answerVerificationService).checkAnswer(
                request.question(),
                request.selectedAnswer(),
                request.verificationToken()
        );
    }
}

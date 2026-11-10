package nl.bramv.triviaquestionservice.service;

import nl.bramv.triviaquestionservice.model.CheckAnswerResponse;
import nl.bramv.triviaquestionservice.model.CheckAnswersRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AnswerServiceTest {
    private HashService hashService;
    private AnswerService answerService;

    @BeforeEach
    void setUp() {
        hashService = mock(HashService.class);
        answerService = new AnswerService(hashService);
    }

    @ParameterizedTest
    @CsvSource({
            "hash-of-4, hash-of-4, true",
            "hash-of-4, hash-of-5, false"
    })
    void checkAnswer_returnsWhetherAnswerIsCorrect(
            String hashedAnswer,
            String verificationToken,
            boolean expected
    ) {

        // Arrange
        CheckAnswersRequest request = new CheckAnswersRequest(
                "4",
                verificationToken
        );

        when(hashService.hash("4")).thenReturn(hashedAnswer);

        // Act
        CheckAnswerResponse result = answerService.checkAnswer(request);

        // Assert
        assertEquals(expected, result.correct());
        verify(hashService).hash("4");
    }
}

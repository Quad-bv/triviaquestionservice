
package nl.bramv.triviaquestionservice.service.mapping;

import nl.bramv.triviaquestionservice.model.enums.Difficulty;
import nl.bramv.triviaquestionservice.model.enums.QuestionType;
import nl.bramv.triviaquestionservice.model.Question;
import nl.bramv.triviaquestionservice.model.QuestionsResponse;
import nl.bramv.triviaquestionservice.service.helper.IAnswerVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionsResponseMapperTest {

    private IAnswerVerificationService answerVerificationService;
    private QuestionsResponseMapper mapper;

    @BeforeEach
    void setUp() {
        answerVerificationService = mock(IAnswerVerificationService.class);
        mapper = new QuestionsResponseMapper(answerVerificationService);
    }

    @ParameterizedTest
    @MethodSource("questionScenarios")
    void toResponse_mapsQuestionCorrectly(QuestionScenario scenario) {
        // Arrange
        String expectedToken = "verification-token";

        when(answerVerificationService.generateToken(
                scenario.question().question(),
                scenario.question().correctAnswer()
        )).thenReturn(expectedToken);

        // Act
        QuestionsResponse result = mapper.toResponse(scenario.question());

        // Assert
        assertAll(
                () -> assertEquals(scenario.question().type(), result.type()),
                () -> assertEquals(scenario.question().difficulty(), result.difficulty()),
                () -> assertEquals(scenario.question().category(), result.category()),
                () -> assertEquals(scenario.question().question(), result.question()),
                () -> assertEquals(expectedToken, result.verificationToken()),
                () -> assertEquals(
                        Set.copyOf(scenario.expectedAnswers()),
                        Set.copyOf(result.answers())
                )
        );

        verify(answerVerificationService).generateToken(
                scenario.question().question(),
                scenario.question().correctAnswer()
        );
    }

    static Stream<QuestionScenario> questionScenarios() {
        return Stream.of(
                new QuestionScenario(
                        new Question(
                                QuestionType.MULTIPLE,
                                Difficulty.HARD,
                                "General Knowledge",
                                "What is the capital of France?",
                                "Paris",
                                List.of("London", "Berlin", "Madrid")
                        ),
                        List.of("Paris", "London", "Berlin", "Madrid")
                ),
                new QuestionScenario(
                        new Question(
                                QuestionType.BOOLEAN,
                                Difficulty.EASY,
                                "General Knowledge",
                                "The Earth is round.",
                                "True",
                                List.of("False")
                        ),
                        List.of("True", "False")
                )
        );
    }

    private record QuestionScenario(
            Question question,
            List<String> expectedAnswers
    ) {}
}

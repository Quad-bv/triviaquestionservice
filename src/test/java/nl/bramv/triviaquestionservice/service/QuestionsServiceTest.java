//package nl.bramv.triviaquestionservice.service;
//
//import nl.bramv.triviaquestionservice.model.Question;
//import nl.bramv.triviaquestionservice.model.QuestionsResponse;
//import nl.bramv.triviaquestionservice.service.provider.opentriviadb.OpenTriviaDBProvider;
//import nl.bramv.triviaquestionservice.service.helper.AnswerVerificationService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//public class QuestionsServiceTest {
//
//    private OpenTriviaDBProvider openTriviaDBProvider;
//    private AnswerVerificationService hashService;
//    private QuestionsService questionsService;
//
//    private Question mathQuestion() {
//        return new Question(
//                "multiple",
//                "easy",
//                "General Knowledge",
//                "What is 2 + 2?",
//                "4",
//                List.of("3", "5", "6")
//        );
//    }
//
//    @BeforeEach
//    void setUp() {
//        openTriviaDBProvider = mock(OpenTriviaDBProvider.class);
//        hashService = mock(AnswerVerificationService.class);
//        questionsService = new QuestionsService(openTriviaDBProvider, hashService);
//    }
//
//    @Test
//    void get_mapsQuestionFields_andRequestsTheGivenAmount() {
//
//        // Arrange
//        when(openTriviaDBProvider.fetchQuestions(1)).thenReturn(List.of(mathQuestion()));
//
//        // Act
//        List<QuestionsResponse> result = questionsService.get(1);
//
//        // Assert
//        assertEquals(1, result.size());
//
//        QuestionsResponse response = result.getFirst();
//        assertEquals("multiple", response.type());
//        assertEquals("easy", response.difficulty());
//        assertEquals("General Knowledge", response.category());
//        assertEquals("What is 2 + 2?", response.question());
//
//        verify(openTriviaDBProvider).fetchQuestions(1);
//    }
//
//    @ParameterizedTest
//    @ValueSource(ints = {1, 2, 5, 10, 25, 50})
//    void get_returnsOneResponsePerRequestedQuestion(int amount) {
//
//        // Arrange: the same test question, repeated `amount` times
//        when(openTriviaDBProvider.fetchQuestions(amount))
//                .thenReturn(Collections.nCopies(amount, mathQuestion()));
//
//        // Act
//        List<QuestionsResponse> result = questionsService.get(amount);
//
//        // Assert
//        assertEquals(amount, result.size());
//        assertTrue(result.stream()
//                .allMatch(response -> response.question().equals("What is 2 + 2?")));
//
//        verify(openTriviaDBProvider).fetchQuestions(amount);
//    }
//
//    @Test
//    void get_usesHashOfCorrectAnswer_asVerificationToken() {
//
//        // Arrange
//        when(hashService.hash("4")).thenReturn("hash-of-4");
//        when(openTriviaDBProvider.fetchQuestions(1)).thenReturn(List.of(mathQuestion()));
//
//        // Act
//        QuestionsResponse response = questionsService.get(1).getFirst();
//
//        // Assert
//        assertEquals("hash-of-4", response.verificationToken());
//    }
//
//    @Test
//    void get_includesCorrectAndIncorrectAnswers() {
//
//        // Arrange
//        when(openTriviaDBProvider.fetchQuestions(1)).thenReturn(List.of(mathQuestion()));
//
//        // Act
//        List<String> answers = questionsService.get(1).getFirst().answers();
//
//        // Assert
//        assertEquals(4, answers.size());
//        assertTrue(answers.containsAll(List.of("3", "5", "6", "4")));
//    }
//}
//
//

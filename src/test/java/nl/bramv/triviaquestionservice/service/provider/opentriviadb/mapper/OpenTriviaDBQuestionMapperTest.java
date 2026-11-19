
package nl.bramv.triviaquestionservice.service.provider.opentriviadb.mapper;

import nl.bramv.triviaquestionservice.model.Question;
import nl.bramv.triviaquestionservice.model.enums.Difficulty;
import nl.bramv.triviaquestionservice.model.enums.QuestionType;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.model.OpenTriviaDBQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class OpenTriviaDBQuestionMapperTest {

    private OpenTriviaDBQuestionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OpenTriviaDBQuestionMapper();
    }

    @Test
    void toQuestionModel_validQuestion_mapsAllFields() {
        // Arrange
        OpenTriviaDBQuestion input = new OpenTriviaDBQuestion(
                "multiple",
                "easy",
                "General Knowledge",
                "What is the capital of France?",
                "Paris",
                List.of("London", "Berlin", "Madrid")
        );

        // Act
        Question result = mapper.toQuestionModel(input);

        // Assert
        assertAll(
                () -> assertEquals(QuestionType.MULTIPLE, result.type()),
                () -> assertEquals(Difficulty.EASY, result.difficulty()),
                () -> assertEquals("General Knowledge", result.category()),
                () -> assertEquals("What is the capital of France?", result.question()),
                () -> assertEquals("Paris", result.correctAnswer()),
                () -> assertEquals(List.of("London", "Berlin", "Madrid"), result.incorrectAnswers())
        );
    }

    @Test
    void toQuestionModel_htmlEncodedValues_decodesValues() {
        // Arrange
        OpenTriviaDBQuestion input = new OpenTriviaDBQuestion(
                "multiple",
                "easy",
                "Science &amp; Nature",
                "What does &quot;HTML&quot; stand for?",
                "HyperText Markup Language",
                List.of("HyperText &amp; Markup", "Other answer", "Another answer")
        );

        // Act
        Question result = mapper.toQuestionModel(input);

        // Assert
        assertAll(
                () -> assertEquals("Science & Nature", result.category()),
                () -> assertEquals("What does \"HTML\" stand for?", result.question()),
                () -> assertEquals("HyperText & Markup", result.incorrectAnswers().getFirst())
        );
    }

    @Test
    void toQuestionModel_nullQuestion_throwsIllegalArgumentException() {
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.toQuestionModel(null)
        );
    }

    @ParameterizedTest
    @MethodSource("missingFields")
    void toQuestionModel_missingField_throwsIllegalArgumentException(OpenTriviaDBQuestion input) {
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.toQuestionModel(input)
        );
    }

    static Stream<OpenTriviaDBQuestion> missingFields() {
        return Stream.of(
                new OpenTriviaDBQuestion(null, "easy", "Category", "Question", "Correct", List.of("Wrong")),
                new OpenTriviaDBQuestion("multiple", null, "Category", "Question", "Correct", List.of("Wrong")),
                new OpenTriviaDBQuestion("multiple", "easy", null, "Question", "Correct", List.of("Wrong")),
                new OpenTriviaDBQuestion("multiple", "easy", "Category", null, "Correct", List.of("Wrong")),
                new OpenTriviaDBQuestion("multiple", "easy", "Category", "Question", null, List.of("Wrong")),
                new OpenTriviaDBQuestion("multiple", "easy", "Category", "Question", "Correct", null)
        );
    }

    @ParameterizedTest
    @MethodSource("unknownEnumValues")
    void toQuestionModel_unknownEnumValue_throwsIllegalArgumentException(OpenTriviaDBQuestion input) {
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.toQuestionModel(input)
        );
    }

    static Stream<OpenTriviaDBQuestion> unknownEnumValues() {
        return Stream.of(
                new OpenTriviaDBQuestion("unknown", "easy", "Category", "Question", "Correct", List.of("Wrong")),
                new OpenTriviaDBQuestion("multiple", "unknown", "Category", "Question", "Correct", List.of("Wrong"))
        );
    }

    @Test
    void toQuestionModel_nullIncorrectAnswer_throwsIllegalArgumentException() {
        // Arrange
        OpenTriviaDBQuestion input = new OpenTriviaDBQuestion(
                "multiple",
                "easy",
                "Category",
                "Question",
                "Correct",
                Arrays.asList("Wrong", null)
        );

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.toQuestionModel(input)
        );
    }
}

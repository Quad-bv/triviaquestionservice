package nl.bramv.triviaquestionservice.service.provider.opentriviadb.mapper;

import nl.bramv.triviaquestionservice.model.enums.Difficulty;
import nl.bramv.triviaquestionservice.model.enums.QuestionType;
import nl.bramv.triviaquestionservice.model.Question;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.model.OpenTriviaDBQuestion;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Locale;


/// Maps OpenTriviaDB questions to the application's Question model.
///
/// Invalid or missing question data is reported using
/// [IllegalArgumentException], allowing the provider to translate
/// the failure into an application-level exception.
@Component
public class OpenTriviaDBQuestionMapper {

    public Question toQuestionModel(OpenTriviaDBQuestion question) {

        if (question == null) {
            throw new IllegalArgumentException("Question data is missing");
        }

        return new Question(
                parse(QuestionType.class, question.type(), "type"),
                parse(Difficulty.class, question.difficulty(), "difficulty"),
                decode(question.category(), "category"),
                decode(question.question(), "question"),
                decode(question.correctAnswer(), "correct answer"),
                decodeAll(question.incorrectAnswers())
        );
    }

    // Converts the provider's string value into one of the application enums.
    private <E extends Enum<E>> E parse(Class<E> enumType, String value, String field) {
        if (value == null) {
            throw new IllegalArgumentException("Missing " + field);
        }
        try {
            return Enum.valueOf(enumType, value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unknown " + field + ": " + value, exception);
        }
    }

    // OpenTriviaDB returns HTML-encoded text that needs to be decoded.
    private String decode(String value, String field) {
        if (value == null) {
            throw new IllegalArgumentException("Missing " + field);
        }
        return HtmlUtils.htmlUnescape(value);
    }


    private List<String> decodeAll(List<String> values) {
        if (values == null) {
            throw new IllegalArgumentException("Missing incorrect answers");
        }
        return values.stream()
                .map(value -> decode(value, "incorrect answer"))
                .toList();
    }
}

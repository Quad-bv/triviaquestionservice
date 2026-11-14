package nl.bramv.triviaquestionservice.service.provider.opentriviadb.mapper;

import nl.bramv.triviaquestionservice.dto.QuestionDTO;
import nl.bramv.triviaquestionservice.enums.Difficulty;
import nl.bramv.triviaquestionservice.enums.QuestionType;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.model.OpenTriviaDBQuestion;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Component
public class OpenTriviaDBQuestionMapper {

    public QuestionDTO toDTO(OpenTriviaDBQuestion question) {

        List<String> decodedIncorrectAnswers =
                question.incorrectAnswers()
                        .stream()
                        .map(HtmlUtils::htmlUnescape)
                        .toList();

        return new QuestionDTO(
                QuestionType.valueOf(question.type().toUpperCase()),
                Difficulty.valueOf(question.difficulty().toUpperCase()),
                HtmlUtils.htmlUnescape(question.category()),
                HtmlUtils.htmlUnescape(question.question()),
                HtmlUtils.htmlUnescape(question.correctAnswer()),
                decodedIncorrectAnswers
        );
    }
}

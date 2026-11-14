package nl.bramv.triviaquestionservice.service.mapping;

import nl.bramv.triviaquestionservice.dto.QuestionDTO;
import nl.bramv.triviaquestionservice.model.QuestionsResponse;
import nl.bramv.triviaquestionservice.service.helper.HashService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class QuestionsResponseMapper {

    private final HashService hashService;

    public QuestionsResponseMapper(HashService hashService) {
        this.hashService = hashService;
    }

    public QuestionsResponse toResponse(QuestionDTO question) {


        List<String> answers = new ArrayList<>(question.incorrectAnswers());

        // Add the correct answer and shuffle the list
        answers.add(question.correctAnswer());
        Collections.shuffle(answers);

        return new QuestionsResponse(
                question.type(),
                question.difficulty(),
                question.category(),
                question.question(),

                // Hash the correct answer as a verification token so it can be verified later
                hashService.hash(question.correctAnswer()),
                answers
        );
    }
}

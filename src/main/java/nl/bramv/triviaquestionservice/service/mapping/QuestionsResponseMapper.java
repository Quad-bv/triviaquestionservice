package nl.bramv.triviaquestionservice.service.mapping;

import nl.bramv.triviaquestionservice.model.Question;
import nl.bramv.triviaquestionservice.model.QuestionsResponse;
import nl.bramv.triviaquestionservice.service.helper.IAnswerVerificationService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class QuestionsResponseMapper {

    private final IAnswerVerificationService answerVerificationService;

    public QuestionsResponseMapper(IAnswerVerificationService answerVerificationService) {
        this.answerVerificationService = answerVerificationService;
    }

    public QuestionsResponse toResponse(Question question) {

        List<String> answers = new ArrayList<>(question.incorrectAnswers());

        // Add the correct answer and shuffle the list
        answers.add(question.correctAnswer());
        Collections.shuffle(answers);

        return new QuestionsResponse(
                question.type(),
                question.difficulty(),
                question.category(),
                question.question(),
                answerVerificationService.generateToken(
                        question.question(),
                        question.correctAnswer()),
                answers
        );
    }
}
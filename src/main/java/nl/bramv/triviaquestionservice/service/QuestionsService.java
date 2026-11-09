package nl.bramv.triviaquestionservice.service;

import nl.bramv.triviaquestionservice.dto.TriviaQuestionApiDTO;
import nl.bramv.triviaquestionservice.model.QuestionsResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionsService {

    private final TriviaDBService triviaDBService;
    private final HashService hashService;

    public QuestionsService(TriviaDBService triviaDBService, HashService hashService) {
        this.triviaDBService = triviaDBService;
        this.hashService = hashService;
    }

    public List<QuestionsResponse> get(int amount){

        List<QuestionsResponse> responses = new ArrayList<>();

        for (TriviaQuestionApiDTO question : triviaDBService.fetchQuestions(amount)) {
            responses.add(convertToResponse(question));
        }

        return responses;
    }

    private QuestionsResponse convertToResponse(TriviaQuestionApiDTO question) {
        String verificationToken = hashService.hash(question.correct_answer());

        List<String> answers = new ArrayList<>(question.incorrect_answers());
        answers.add(question.correct_answer());

        return new QuestionsResponse(
                question.type(),
                question.difficulty(),
                question.category(),
                question.question(),
                verificationToken,
                answers
        );
    }
}

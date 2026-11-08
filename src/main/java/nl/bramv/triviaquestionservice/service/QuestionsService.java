package nl.bramv.triviaquestionservice.service;

import nl.bramv.triviaquestionservice.model.QuestionsResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionsService {

    private final TriviaDBService triviaDBService;

    public QuestionsService(TriviaDBService triviaDBService) {
        this.triviaDBService = triviaDBService;
    }

    public List<QuestionsResponse> get(int amount){

        triviaDBService.fetchQuestions(amount);

        return new ArrayList<>();
    }
}

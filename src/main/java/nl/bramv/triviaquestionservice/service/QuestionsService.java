package nl.bramv.triviaquestionservice.service;

import nl.bramv.triviaquestionservice.dto.QuestionDTO;
import nl.bramv.triviaquestionservice.model.QuestionsRequest;
import nl.bramv.triviaquestionservice.model.QuestionsResponse;
import nl.bramv.triviaquestionservice.service.provider.IQuestionsProvider;
import nl.bramv.triviaquestionservice.service.mapping.QuestionsResponseMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionsService {

    private final IQuestionsProvider questionsProvider;
    private final QuestionsResponseMapper questionsResponseMapper;

    public QuestionsService(IQuestionsProvider questionsProvider, QuestionsResponseMapper questionsResponseMapper) {
        this.questionsProvider = questionsProvider;
        this.questionsResponseMapper = questionsResponseMapper;
    }

    public List<QuestionsResponse> get(QuestionsRequest request) {
        List<QuestionsResponse> responses = new ArrayList<>();

        // Get questions from the Questions service and convert them to a response list and add it to the response
        for (QuestionDTO question : questionsProvider.getQuestions(request)) {
            responses.add(questionsResponseMapper.toResponse(question));
        }

        return responses;
    }
}

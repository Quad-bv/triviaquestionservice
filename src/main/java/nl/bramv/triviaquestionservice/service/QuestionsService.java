package nl.bramv.triviaquestionservice.service;

import nl.bramv.triviaquestionservice.model.Question;
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

    public QuestionsService(
            IQuestionsProvider questionsProvider,
            QuestionsResponseMapper questionsResponseMapper
    ) {
        this.questionsProvider = questionsProvider;
        this.questionsResponseMapper = questionsResponseMapper;
    }

    // Retrieves questions from the configured provider and maps them to API responses.
    public List<QuestionsResponse> get(QuestionsRequest request) {
        return questionsProvider.getQuestions(request)
                .stream()
                .map(questionsResponseMapper::toResponse)
                .toList();
    }
}

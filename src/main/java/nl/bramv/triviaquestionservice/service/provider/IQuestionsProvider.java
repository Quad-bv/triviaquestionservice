package nl.bramv.triviaquestionservice.service.provider;

import nl.bramv.triviaquestionservice.dto.QuestionDTO;
import nl.bramv.triviaquestionservice.model.QuestionsRequest;

import java.util.List;

public interface IQuestionsProvider {
    List<QuestionDTO> getQuestions(QuestionsRequest request
    );

//TODO implement token functionality
//    String getToken();
//    String resetToken(
//            Optional<String> token
//    );

}

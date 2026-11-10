package nl.bramv.triviaquestionservice.service;

import nl.bramv.triviaquestionservice.model.CheckAnswerResponse;
import nl.bramv.triviaquestionservice.model.CheckAnswersRequest;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {
    private final HashService hashService;

    public AnswerService(HashService hashService) {
        this.hashService = hashService;
    }


    public CheckAnswerResponse checkAnswer(CheckAnswersRequest request){
        return new CheckAnswerResponse(
                hashService.hash(request.selectedAnswer()).equals(request.verificationToken())
        );
    }
}

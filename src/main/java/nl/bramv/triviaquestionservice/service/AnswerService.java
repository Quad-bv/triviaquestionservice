package nl.bramv.triviaquestionservice.service;

import nl.bramv.triviaquestionservice.model.CheckAnswerResponse;
import nl.bramv.triviaquestionservice.model.CheckAnswersRequest;
import nl.bramv.triviaquestionservice.service.helper.IAnswerVerificationService;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {
    private final IAnswerVerificationService answerVerificationService;

    public AnswerService(IAnswerVerificationService answerVerificationService) {
        this.answerVerificationService = answerVerificationService;
    }

    // Checks the submitted answer using the configured verification strategy.
    public CheckAnswerResponse checkAnswer(CheckAnswersRequest request){
        return new CheckAnswerResponse(
                answerVerificationService.checkAnswer(request.question(), request.selectedAnswer(), request.verificationToken())
        );
    }
}

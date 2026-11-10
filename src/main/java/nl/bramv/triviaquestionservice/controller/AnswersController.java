package nl.bramv.triviaquestionservice.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import nl.bramv.triviaquestionservice.model.CheckAnswerResponse;
import nl.bramv.triviaquestionservice.model.CheckAnswersRequest;
import nl.bramv.triviaquestionservice.service.AnswerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnswersController {

    private final AnswerService answerService;

    public AnswersController(AnswerService answerService) {
        this.answerService = answerService;
    }


    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Answer checked. 'correct' says whether the selected answer was right"),
            @ApiResponse(responseCode = "400", description = "Invalid request: selectedAnswer or verificationToken is missing or blank")
    })
    @PostMapping("/checkanswers")
    public CheckAnswerResponse checkAnswers(@Valid @RequestBody CheckAnswersRequest request){
        return answerService.checkAnswer(request);
    }
}

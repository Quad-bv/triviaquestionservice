package nl.bramv.triviaquestionservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nl.bramv.triviaquestionservice.model.CheckAnswerResponse;
import nl.bramv.triviaquestionservice.model.CheckAnswersRequest;
import nl.bramv.triviaquestionservice.service.AnswerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// Handles requests for checking submitted answers.
@RestController
@Tag(name = "Answers", description = "Check submitted trivia answers")
public class AnswersController {

    private final AnswerService answerService;

    public AnswersController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/checkanswers")
    @Operation(summary = "Check a trivia answer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Answer checked. 'correct' says whether the selected answer was right"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing request fields")
    })
    public CheckAnswerResponse checkAnswer(@Valid @RequestBody CheckAnswersRequest request){
        return answerService.checkAnswer(request);
    }
}

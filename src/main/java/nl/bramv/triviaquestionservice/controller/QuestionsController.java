package nl.bramv.triviaquestionservice.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import nl.bramv.triviaquestionservice.model.QuestionsResponse;
import nl.bramv.triviaquestionservice.service.QuestionsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QuestionsController {

    private final QuestionsService questionsService;

    public QuestionsController(QuestionsService questionsService) {
        this.questionsService = questionsService;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Questions returned"),
            @ApiResponse(responseCode = "400", description = "Invalid amount"),
            @ApiResponse(responseCode = "429", description = "Open Trivia DB rate limit reached, retry shortly"),
            @ApiResponse(responseCode = "502", description = "Open Trivia DB returned an error or an unusable response"),
            @ApiResponse(responseCode = "503", description = "Open Trivia DB is unreachable")
    })
    @GetMapping("/questions")
    public List<QuestionsResponse> GET(
            @Positive
            @Max(50)
            @RequestParam(defaultValue = "1") int amount
    ) {
        return questionsService.get(amount);
    }
}

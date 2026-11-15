package nl.bramv.triviaquestionservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import nl.bramv.triviaquestionservice.enums.Difficulty;
import nl.bramv.triviaquestionservice.enums.QuestionType;
import nl.bramv.triviaquestionservice.model.QuestionsRequest;
import nl.bramv.triviaquestionservice.model.QuestionsResponse;
import nl.bramv.triviaquestionservice.service.QuestionsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Handles requests for retrieving trivia questions.
@RestController
@Validated
@Tag(name = "Questions", description = "Retrieve trivia questions")
public class QuestionsController {

    private final QuestionsService questionsService;

    public QuestionsController(QuestionsService questionsService) {
        this.questionsService = questionsService;
    }

    @GetMapping("/questions")
    @Operation(summary = "Get trivia questions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "No questions found"),
            @ApiResponse(responseCode = "429", description = "Provider rate limit exceeded"),
            @ApiResponse(responseCode = "502", description = "Provider error"),
            @ApiResponse(responseCode = "503", description = "Provider unavailable")
    })
    public List<QuestionsResponse> getQuestions(
            @RequestParam(defaultValue = "1") @Min(1) @Max(50) int amount,
            @RequestParam(required = false) @Min(1) Integer category,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) QuestionType type,
            @Parameter(description = "OpenTDB session token (optional).Planned for a future release.")
            @RequestParam(required = false) String token
    ) {
        // Convert HTTP query parameters into the application request model.
        return questionsService.get(new QuestionsRequest(amount, category, difficulty, type, token));
    }
}

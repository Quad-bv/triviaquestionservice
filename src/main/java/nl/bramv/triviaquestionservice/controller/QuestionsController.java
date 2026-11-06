package nl.bramv.triviaquestionservice.controller;

import nl.bramv.triviaquestionservice.dto.TriviaQuestionApiDTO;
import nl.bramv.triviaquestionservice.model.QuestionResponse;
import nl.bramv.triviaquestionservice.service.TriviaQuestionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QuestionsController {

    private final TriviaQuestionService triviaQuestionService;

    public QuestionsController(TriviaQuestionService triviaQuestionService) {
        this.triviaQuestionService = triviaQuestionService;
    }

    @GetMapping("/questions")
    public List<TriviaQuestionApiDTO> GET() {

        return triviaQuestionService.fetchQuestions(10);
    }
}

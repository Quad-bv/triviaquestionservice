package nl.bramv.triviaquestionservice.controller;

import nl.bramv.triviaquestionservice.model.QuestionResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
public class QuestionsController {

    @GetMapping("/questions")
    public QuestionResponse GET() {
        return new QuestionResponse("test", "easy", "easy", "easy", "easy", Arrays.asList("Rood", "Groen", "Blauw"));
    }
}

package nl.bramv.triviaquestionservice.controller;

import jakarta.validation.Valid;
import nl.bramv.triviaquestionservice.model.CheckAnswerResponse;
import nl.bramv.triviaquestionservice.model.CheckAnswersRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnswersController {

    @PostMapping("/checkanswers")
    public CheckAnswerResponse POST(@Valid @RequestBody CheckAnswersRequest request){
        return new CheckAnswerResponse(true);
    }
}

package nl.bramv.triviaquestionservice.controller;

import nl.bramv.triviaquestionservice.exception.logging.ApplicationExceptionLogger;
import nl.bramv.triviaquestionservice.model.CheckAnswerResponse;
import nl.bramv.triviaquestionservice.service.AnswerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnswersController.class)
class AnswersControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnswerService answerService;
    @MockitoBean
    private ApplicationExceptionLogger applicationExceptionLogger;

    @BeforeEach
    void setUp() {
        // Arrange
        reset(answerService);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void checkAnswer_returnsServiceResponse(boolean correct) throws Exception {
        // Arrange
        when(answerService.checkAnswer(any()))
                .thenReturn(new CheckAnswerResponse(correct));

        String request = """
                {
                    "question": "What is the capital of France?",
                    "selectedAnswer": "Paris",
                    "verificationToken": "verification-token"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/checkanswers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(correct));

        verify(answerService).checkAnswer(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
            {
                "selectedAnswer": "Paris",
                "verificationToken": "verification-token"
            }
            """,
            """
            {
                "question": "",
                "selectedAnswer": "Paris",
                "verificationToken": "verification-token"
            }
            """,
            """
            {
                "question": "What is the capital of France?",
                "verificationToken": "verification-token"
            }
            """,
            """
            {
                "question": "What is the capital of France?",
                "selectedAnswer": "",
                "verificationToken": "verification-token"
            }
            """,
            """
            {
                "question": "What is the capital of France?",
                "selectedAnswer": "Paris"
            }
            """,
            """
            {
                "question": "What is the capital of France?",
                "selectedAnswer": "Paris",
                "verificationToken": ""
            }
            """
    })
    void checkAnswer_returnsBadRequestForInvalidRequest(String request) throws Exception {
        // Arrange

        // Act & Assert
        mockMvc.perform(post("/checkanswers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(answerService);
    }
}

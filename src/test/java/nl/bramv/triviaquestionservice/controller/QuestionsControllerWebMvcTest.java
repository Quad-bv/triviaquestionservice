package nl.bramv.triviaquestionservice.controller;

import nl.bramv.triviaquestionservice.model.enums.Difficulty;
import nl.bramv.triviaquestionservice.model.enums.QuestionType;
import nl.bramv.triviaquestionservice.exception.logging.ApplicationExceptionLogger;
import nl.bramv.triviaquestionservice.model.QuestionsRequest;
import nl.bramv.triviaquestionservice.service.QuestionsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuestionsController.class)
class QuestionsControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuestionsService questionsService;

    @MockitoBean
    private ApplicationExceptionLogger applicationExceptionLogger;

    @Test
    void getQuestions_usesDefaultAmountWhenNoParametersAreProvided() throws Exception {
        // Arrange
        when(questionsService.get(any())).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/questions"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));

        verify(questionsService).get(new QuestionsRequest(1, null, null, null, null));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 50})
    void getQuestions_returnsOkForValidAmount(int amount) throws Exception {
        // Arrange
        when(questionsService.get(any())).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/questions")
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(questionsService).get(new QuestionsRequest(amount, null, null, null, null));
    }

    @Test
    void getQuestions_bindsOptionalParameters() throws Exception {
        // Arrange
        when(questionsService.get(any())).thenReturn(List.of());

        Difficulty difficulty = Difficulty.values()[0];
        QuestionType type = QuestionType.values()[0];

        // Act & Assert
        mockMvc.perform(get("/questions")
                        .param("amount", "10")
                        .param("category", "9")
                        .param("difficulty", difficulty.name())
                        .param("type", type.name())
                        .param("token", "session-token"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(questionsService).get(
                new QuestionsRequest(10, 9, difficulty, type, "session-token")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "51", "not-a-number"})
    void getQuestions_returnsBadRequestForInvalidAmount(String amount) throws Exception {
        // Arrange

        // Act & Assert
        mockMvc.perform(get("/questions")
                        .param("amount", amount))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(questionsService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "not-a-number"})
    void getQuestions_returnsBadRequestForInvalidCategory(String category) throws Exception {
        // Arrange

        // Act & Assert
        mockMvc.perform(get("/questions")
                        .param("category", category))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(questionsService);
    }

    @ParameterizedTest
    @CsvSource({
            "difficulty, INVALID",
            "type, INVALID"
    })
    void getQuestions_returnsBadRequestForInvalidEnumParameter(
            String parameter,
            String value
    ) throws Exception {
        // Arrange

        // Act & Assert
        mockMvc.perform(get("/questions")
                        .param(parameter, value))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(questionsService);
    }
}

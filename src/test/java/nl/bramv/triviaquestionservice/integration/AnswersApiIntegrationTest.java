package nl.bramv.triviaquestionservice.integration;

import nl.bramv.triviaquestionservice.model.CheckAnswersRequest;
import nl.bramv.triviaquestionservice.service.helper.IAnswerVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "verification.secret=integration-test-secret")
@AutoConfigureMockMvc
class AnswersApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IAnswerVerificationService answerVerificationService;

    private String question;
    private String correctAnswer;
    private String verificationToken;

    @BeforeEach
    void setUp() {
        // Arrange
        question = "What is the capital of France?";
        correctAnswer = "Paris";

        verificationToken = answerVerificationService.generateToken(
                question,
                correctAnswer
        );
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void checkAnswer_returnsExpectedResult(boolean correct) throws Exception {
        // Arrange
        String selectedAnswer = correct ? correctAnswer : "London";

        CheckAnswersRequest request = new CheckAnswersRequest(
                question,
                selectedAnswer,
                verificationToken
        );

        // Act & Assert
        mockMvc.perform(post("/checkanswers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.correct").value(correct));
    }
}

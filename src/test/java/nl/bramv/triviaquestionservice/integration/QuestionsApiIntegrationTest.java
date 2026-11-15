package nl.bramv.triviaquestionservice.integration;

import nl.bramv.triviaquestionservice.service.provider.opentriviadb.client.IOpenTriviaDBClient;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.model.OpenTriviaDBQuestion;
import nl.bramv.triviaquestionservice.service.provider.opentriviadb.model.OpenTriviaDBResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "verification.secret=integration-test-secret")
@AutoConfigureMockMvc
class QuestionsApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IOpenTriviaDBClient client;

    private List<OpenTriviaDBQuestion> providerQuestions;

    @BeforeEach
    void setUp() {
        // Arrange
        providerQuestions = IntStream.range(0, 10)
                .mapToObj(i -> new OpenTriviaDBQuestion(
                        "multiple",
                        "easy",
                        "Geography",
                        "Question " + i,
                        "Correct answer " + i,
                        List.of("Wrong 1", "Wrong 2", "Wrong 3")
                ))
                .toList();

        when(client.getQuestions(anyInt(), any(), any(), any(), any()))
                .thenAnswer(invocation -> {
                    int amount = invocation.getArgument(0);
                    return new OpenTriviaDBResponse(
                            0,
                            providerQuestions.subList(0, amount)
                    );
                });
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10})
    void getQuestions_returnsRequestedNumberOfQuestions(int amount) throws Exception {
        // Act & Assert
        mockMvc.perform(get("/questions")
                        .param("amount", String.valueOf(amount))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(amount))
                .andExpect(jsonPath("$[0].question").value("Question 0"))
                .andExpect(jsonPath("$[0].category").value("Geography"))
                .andExpect(jsonPath("$[0].answers.length()").value(4))
                .andExpect(jsonPath("$[0].verificationToken").isNotEmpty());

        verify(client).getQuestions(amount, null, null, null, null);
    }
}
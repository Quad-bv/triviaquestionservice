//
//package nl.bramv.triviaquestionservice.service;
//
//import nl.bramv.triviaquestionservice.dto.QuestionDTO;
//import nl.bramv.triviaquestionservice.exception.TriviaDBBadGatewayException;
//import nl.bramv.triviaquestionservice.exception.TriviaDBRateLimitException;
//import nl.bramv.triviaquestionservice.exception.TriviaDBUnavailableException;
//
//import nl.bramv.triviaquestionservice.service.provider.opentriviadb.OpenTriviaDBProvider;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.client.MockRestServiceServer;
//import org.springframework.web.client.RestClient;
//
//import java.io.IOException;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
//import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
//
//class TriviaDBServiceTest {
//
//    private MockRestServiceServer mockServer;
//    private OpenTriviaDBProvider openTriviaDBProvider;
//
//    @BeforeEach
//    void setUp() {
//        RestClient.Builder builder = RestClient.builder();
//
//        mockServer = MockRestServiceServer.bindTo(builder).build();
//
//        openTriviaDBProvider = new OpenTriviaDBProvider(builder, "http://localhost");
//    }
//
//    @Test
//    void fetchQuestions_returnsQuestions_whenRequestIsSuccessful() {
//
//        // Arrange
//        QuestionDTO expectedQuestion = new QuestionDTO(
//                "multiple",
//                "easy",
//                "General Knowledge",
//                "What is 2 + 2?",
//                "4",
//                List.of("3", "5", "6")
//        );
//
//        mockServer.expect(requestTo("http://localhost/api.php?amount=1"))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(withSuccess(
//                        """
//                        {
//                            "response_code": 0,
//                            "results": [
//                                {
//                                    "type": "multiple",
//                                    "difficulty": "easy",
//                                    "category": "General Knowledge",
//                                    "question": "What is 2 + 2?",
//                                    "correct_answer": "4",
//                                    "incorrect_answers": ["3", "5", "6"]
//                                }
//                            ]
//                        }
//                        """,
//                        MediaType.APPLICATION_JSON
//                ));
//
//        // Act
//        List<QuestionDTO> result =
//                openTriviaDBProvider.fetchQuestions(1);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(expectedQuestion, result.getFirst());
//
//        mockServer.verify();
//    }
//
//    @Test
//    void fetchQuestions_returnsDecodedQuestions_whenRequestIsSuccessful() {
//
//        // Arrange
//        QuestionDTO expectedQuestion = new QuestionDTO(
//                "multiple",
//                "easy",
//                "General Knowledge",
//                "What is 2 + 2?",
//                "4",
//                List.of("3", "5", "π ≈ 3.14")   // π ≈ 3.14
//        );
//
//        mockServer.expect(requestTo("http://localhost/api.php?amount=1"))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(withSuccess(
//                        """
//                        {
//                            "response_code": 0,
//                            "results": [
//                                {
//                                    "type": "multiple",
//                                    "difficulty": "easy",
//                                    "category": "General Knowledge",
//                                    "question": "What is 2 &#43; 2?",
//                                    "correct_answer": "&#52;",
//                                    "incorrect_answers": ["3", "5", "&pi; &asymp; 3.14"]
//                                }
//                            ]
//                        }
//                        """,
//                        MediaType.APPLICATION_JSON
//                ));
//
//        // Act
//        List<QuestionDTO> result =
//                openTriviaDBProvider.fetchQuestions(1);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(expectedQuestion, result.getFirst());
//
//        mockServer.verify();
//    }
//
//    @Test
//    void fetchQuestions_throwsUnavailable_whenConnectionFails() {
//
//        // Arrange
//        mockServer.expect(requestTo("http://localhost/api.php?amount=1"))
//                .andRespond(withException(new IOException("Connection failed")));
//
//        // Act & Assert
//        assertThrows(
//                TriviaDBUnavailableException.class,
//                () -> openTriviaDBProvider.fetchQuestions(1)
//        );
//
//        mockServer.verify();
//    }
//
//    @Test
//    void fetchQuestions_throwsRateLimit_whenTriviaDBReturns429() {
//
//        // Arrange
//        mockServer.expect(requestTo("http://localhost/api.php?amount=1"))
//                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
//
//        // Act & Assert
//        assertThrows(
//                TriviaDBRateLimitException.class,
//                () -> openTriviaDBProvider.fetchQuestions(1)
//        );
//
//        mockServer.verify();
//    }
//
//    @ParameterizedTest
//    @ValueSource(ints = {400, 401, 403, 404, 500, 502, 503})
//    void fetchQuestions_throwsBadGateway_whenTriviaDBReturnsHttpError(int statusCode) {
//
//        // Arrange
//        mockServer.expect(requestTo("http://localhost/api.php?amount=1"))
//                .andRespond(withStatus(HttpStatusCode.valueOf(statusCode)));
//
//        // Act
//        TriviaDBBadGatewayException exception = assertThrows(
//                TriviaDBBadGatewayException.class,
//                () -> openTriviaDBProvider.fetchQuestions(1)
//        );
//
//        // Assert
//        assertNotNull(exception.getCause());
//
//        mockServer.verify();
//    }
//
//    @Test
//    void fetchQuestions_throwsBadGateway_whenTriviaDBReturnsNullResponse() {
//
//        // Arrange
//        mockServer.expect(requestTo("http://localhost/api.php?amount=1"))
//                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));
//
//        // Act & Assert
//        assertThrows(
//                TriviaDBBadGatewayException.class,
//                () -> openTriviaDBProvider.fetchQuestions(1)
//        );
//
//        mockServer.verify();
//    }
//
//    @Test
//    void fetchQuestions_throwsBadGateway_whenTriviaDBReturnsNullQuestions() {
//
//        // Arrange
//        mockServer.expect(requestTo("http://localhost/api.php?amount=1"))
//                .andRespond(withSuccess(
//                        """
//                        {
//                            "response_code": 0,
//                            "results": null
//                        }
//                        """,
//                        MediaType.APPLICATION_JSON
//                ));
//
//        // Act & Assert
//        assertThrows(
//                TriviaDBBadGatewayException.class,
//                () -> openTriviaDBProvider.fetchQuestions(1)
//        );
//
//        mockServer.verify();
//    }
//}

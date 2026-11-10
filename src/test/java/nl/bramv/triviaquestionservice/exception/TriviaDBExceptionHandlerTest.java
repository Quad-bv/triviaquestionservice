package nl.bramv.triviaquestionservice.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class TriviaDBExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new TestController())
                .setControllerAdvice(new TriviaDBExceptionHandler())
                .build();
    }

    @Test
    void handleUnavailable_returns503() throws Exception {

        // Arrange
        String url = "/test/unavailable";

        // Act
        var result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.detail")
                        .value("OpenTriviaDB unavailable"));
    }

    @Test
    void handleRateLimited_returns429() throws Exception {

        // Arrange
        String url = "/test/rate-limited";

        // Act
        var result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.detail")
                        .value("OpenTriviaDB received to many requests"));
    }

    @Test
    void handleUnreachable_returns502() throws Exception {

        // Arrange
        String url = "/test/unreachable";

        // Act
        var result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.detail")
                        .value("OpenTriviaDB bad Gateway"));
    }

    @RestController
    static class TestController {

        @GetMapping("/test/unavailable")
        void unavailable() {
            throw new TriviaDBUnavailableException(
                    new RuntimeException("Connection failed")
            );
        }

        @GetMapping("/test/rate-limited")
        void rateLimited() {
            throw new TriviaDBRateLimitException(
                    new RuntimeException("Too many requests")
            );
        }

        @GetMapping("/test/unreachable")
        void unreachable() {
            throw new TriviaDBBadGatewayException(
                    new RuntimeException("Bad gateway")
            );
        }
    }
}

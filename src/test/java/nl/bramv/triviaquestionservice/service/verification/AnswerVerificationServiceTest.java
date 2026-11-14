package nl.bramv.triviaquestionservice.service.verification;

import nl.bramv.triviaquestionservice.service.helper.AnswerVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AnswerVerificationServiceTest {

    private static final String SECRET = "development-only-secret";

    private AnswerVerificationService answerVerificationService;

    @BeforeEach
    void setUp() {
        answerVerificationService = new AnswerVerificationService(SECRET);
    }

    @ParameterizedTest
    @MethodSource("verificationScenarios")
    void generateToken_returnsExpectedToken(VerificationScenario scenario) {
        // Arrange

        // Act
        String result = answerVerificationService.generateToken(
                scenario.question(), scenario.answer()
        );

        // Assert
        assertEquals(scenario.token(), result);
    }

    @ParameterizedTest
    @MethodSource("verificationScenarios")
    void checkAnswer_returnsTrueForValidToken(VerificationScenario scenario) {
        // Arrange

        // Act
        boolean result = answerVerificationService.checkAnswer(
                scenario.question(), scenario.answer(), scenario.token()
        );

        // Assert
        assertTrue(result);
    }

    @ParameterizedTest
    @MethodSource("invalidScenarios")
    void checkAnswer_returnsFalseForInvalidInput(VerificationScenario scenario) {
        // Arrange

        // Act
        boolean result = answerVerificationService.checkAnswer(
                scenario.question(), scenario.answer(), scenario.token()
        );

        // Assert
        assertFalse(result);
    }

    static Stream<VerificationScenario> verificationScenarios() {
        return Stream.of(
                new VerificationScenario(
                        "What is the name of the protagonist of the 2017 anime \"Land of the Lustrous\"?",
                        "Phosphophyllite",
                        "0eb9c88fddee969cc7ab1bc5caf61fa7edcbc194dc42fa8dad387c54e69b2765"
                ),
                new VerificationScenario(
                        "In the game \"Cave Story,\" what is the character Balrog's catchphrase?",
                        "Huzzah!",
                        "26984acef5da97a22a137698bebd132a30164535349c7c5a11d2e887a4ab7e74"
                ),
                new VerificationScenario(
                        "Which musical group won the Brit Awards in 1985, with George Michael as a member?",
                        "Wham!",
                        "2fe0ed194fa95b9e974bfcb21c24fae272e378b44d5a1215f8926d99f0f3dc18"
                ),
                new VerificationScenario(
                        "When someone is cowardly, they are said to have what color belly?",
                        "Yellow",
                        "5d2e6fb344b7639c6f6de91fb46185d7029c690ffbd1da6e8dfd6166192f5582"
                ),
                new VerificationScenario(
                        "What is the name of the island introduced in the ARMA III: APEX expansion pack?",
                        "Tanoa",
                        "0bdc84e3f271a48df66a6dd537a695a3c678f8730ca86cf48ba328aaf79ca6a9"
                ),
                new VerificationScenario(
                        "The greek god Poseidon was the god of what?",
                        "The Sea",
                        "82272be009c6bae57c57645491a9fdad93fa7b5aa908dd2040e5d53ea5ad0b77"
                ),
                new VerificationScenario(
                        "The decimal number 31 in hexadecimal would be what?",
                        "1F",
                        "139db45fcd592db43e877859d73f1e083738ed777a7dc665f965df5731c854c5"
                ),
                new VerificationScenario(
                        "The 2011 movie \"The Adventures of Tintin\" was directed by Steven Spielberg.",
                        "True",
                        "d8603ff01c1a7a9c8a298fcad8222c8d91e2e0cfb2981ae8b347669a824d477c"
                ),
                new VerificationScenario(
                        "In DC comics, what is Owlman's real name?",
                        "Thomas Wayne Jr.",
                        "ecbde30cb80f1924817563cc21af18d87be7ca7b5b622cd015e62acf45b2142a"
                ),
                new VerificationScenario(
                        "An average human can go two weeks without water.",
                        "False",
                        "430ccfe7201966c3d07f9b77d1efffa8a2cc67d2d8e201e411da3048722931d6"
                )
        );
    }

    static Stream<VerificationScenario> invalidScenarios() {
        VerificationScenario valid = verificationScenarios().findFirst().orElseThrow();

        return Stream.of(
                // Incorrect answer
                new VerificationScenario(
                        valid.question(), "London", valid.token()
                ),
                // Different question
                new VerificationScenario(
                        "What is the capital of Germany?",
                        valid.answer(), valid.token()
                ),
                // Invalid token
                new VerificationScenario(
                        valid.question(), valid.answer(), "invalid-token"
                )
        );
    }

    private record VerificationScenario(
            String question,
            String answer,
            String token
    ) {}
}
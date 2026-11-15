package nl.bramv.triviaquestionservice.service.helper;

// Contract for every implementation:
// - generateToken() returns a token tied to the supplied question and correct answer.
// - checkAnswer() returns true only when the submitted answer matches the token.
// - verification tokens must not expose the correct answer.
// - implementations must not expose or return the server-side secret.
// - methods must not return null.
public interface IAnswerVerificationService {

    String generateToken(String question, String correctAnswer);

    boolean checkAnswer(String question, String answer, String verificationToken);
}

package nl.bramv.triviaquestionservice.service.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;

@Service
@ConditionalOnProperty(name = "verification.type", havingValue = "hashVerification", matchIfMissing = true)
public class AnswerVerificationService implements IAnswerVerificationService{
    private static final String ALGORITHM = "HmacSHA256";
    // The secret is used as the HMAC key and is never exposed to the client.
    private final SecretKeySpec key;

    public AnswerVerificationService(@Value("${verification.secret}") String secretKey) {
        this.key = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                ALGORITHM
        );
    }

    // Creates a token that is tied to both the question and its correct answer.
    @Override
    public String generateToken(String question, String correctAnswer) {
        return generateHash(question, correctAnswer);
    }

    // Generates a token from the submitted answer and checks whether it matches.
    @Override
    public boolean checkAnswer(String question, String answer, String verificationToken)
    {

        // MessageDigest.isEqual() avoids a regular string comparison and is appropriate for comparing verification tokens.
        return MessageDigest.isEqual(
                verificationToken.getBytes(StandardCharsets.US_ASCII),
                generateHash(question, answer).getBytes(StandardCharsets.US_ASCII)
        );
    }

    // Generates the HMAC so the token cannot be reproduced without the server-side secret
    private String generateHash(String question, String answer) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(this.key);

            String value = question + "|" + answer;

            byte[] hash = mac.doFinal(
                    value.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException(
                    "Unable to generate verification token",
                    exception
            );
        }
    }
}
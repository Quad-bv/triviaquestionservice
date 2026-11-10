package nl.bramv.triviaquestionservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;

@Service
public class HashService {
    private static final String ALGORITHM = "HmacSHA256";
    private final SecretKeySpec key;

    public HashService(@Value("${app.hash.secret}") String secretKey) {
        this.key = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                ALGORITHM
        );
    }

    public String hash(String value) {

        try{
            Mac mac = Mac.getInstance(ALGORITHM);

            mac.init(this.key);

            byte[] hash = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (GeneralSecurityException exception){
            throw new IllegalStateException("Unable to generate hash", exception);
        }
    }
}
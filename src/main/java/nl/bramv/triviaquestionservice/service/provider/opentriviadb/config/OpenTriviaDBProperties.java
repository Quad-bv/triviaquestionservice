package nl.bramv.triviaquestionservice.service.provider.opentriviadb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "opentriviadb")
public record OpenTriviaDBProperties(
        String baseUrl,
        String providerName,
        Duration connectTimeout,
        Duration readTimeout
) {}
package nl.bramv.triviaquestionservice.service.provider.opentriviadb.config;

import nl.bramv.triviaquestionservice.service.provider.opentriviadb.client.IOpenTriviaDBClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@EnableConfigurationProperties(OpenTriviaDBProperties.class)
public class OpenTriviaDBConfig {

    // Creates and registers a RestClient bean in Spring's application context.
    @Bean
    @Qualifier("openTriviaDBRestClient")
    public RestClient openTriviaDBRestClient(
            RestClient.Builder builder,
            OpenTriviaDBProperties properties) {

        return builder
                .baseUrl(properties.baseUrl())
                .build();
    }

    // Creates and registers an implementation of IOpenTriviaDBClient.
    @Bean
    public IOpenTriviaDBClient openTriviaDBClient(
            @Qualifier("openTriviaDBRestClient")
            RestClient openTriviaDBRestClient
    )
    {
        // Adapts RestClient so it can be used by Spring's HTTP service proxy.
        RestClientAdapter adapter =
                RestClientAdapter.create(openTriviaDBRestClient);

        // Creates a factory responsible for generating HTTP service proxies.
        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory
                        .builderFor(adapter)
                        .build();

        // Generates an implementation of IOpenTriviaDBClient.
        return factory.createClient(IOpenTriviaDBClient.class);
    }
}

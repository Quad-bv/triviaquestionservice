package nl.bramv.triviaquestionservice.startup;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Profile({"dev", "debug"})
public class StartupInfo implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupInfo.class);

    private final ServletWebServerApplicationContext context;
    private final Environment environment;

    public StartupInfo(
            ServletWebServerApplicationContext context,
            Environment environment
    ) {
        this.context = context;
        this.environment = environment;
    }

    @Override
    public void run(@NonNull ApplicationArguments args) {
        int port = Objects.requireNonNull(context.getWebServer()).getPort();
        String contextPath = environment.getProperty(
                "server.servlet.context-path", ""
        );

        String baseUrl = "http://localhost:" + port + contextPath;

        log.info("");
        log.info("======================================");
        log.info("       TRIVIA API IS RUNNING!");
        log.info("======================================");
        log.info(" API:     {}", baseUrl);
        log.info(" Swagger: {}/swagger-ui/index.html", baseUrl);
        log.info("======================================");
    }
}
package nl.bramv.triviaquestionservice.startup;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class StartupInfo implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupInfo.class);

    private final ServletWebServerApplicationContext context;

    public StartupInfo(ServletWebServerApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(@NonNull ApplicationArguments args) {
        int port = Objects.requireNonNull(context.getWebServer()).getPort();
        String baseUrl = "http://localhost:" + port;

        log.info("");
        log.info("======================================");
        log.info("       TRIVIA API IS RUNNING!");
        log.info("======================================");
        log.info(" API:     {}", baseUrl);
        log.info(" Swagger: {}/swagger-ui/index.html", baseUrl);
        log.info("======================================");
    }
}
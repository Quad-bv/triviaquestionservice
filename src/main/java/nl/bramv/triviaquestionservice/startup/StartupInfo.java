package nl.bramv.triviaquestionservice.startup;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class StartupInfo implements ApplicationRunner {

    private final ServletWebServerApplicationContext context;

    public StartupInfo(ServletWebServerApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) {

        int port = context.getWebServer().getPort();
        String baseUrl = "http://localhost:" + port;

        System.out.println("");
        System.out.println("======================================");
        System.out.println("       TRIVIA API IS RUNNING!");
        System.out.println("======================================");
        System.out.println(" API:     " + baseUrl);
        System.out.println(" Swagger: " + baseUrl + "/swagger-ui/index.html");
        System.out.println("======================================");
        System.out.println("");
    }
}

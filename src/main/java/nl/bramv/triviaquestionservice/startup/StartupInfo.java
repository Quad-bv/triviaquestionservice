package nl.bramv.triviaquestionservice.startup;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class StartupInfo implements ApplicationRunner {

    private final ApplicationContext context;

    public StartupInfo(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) {

        if (!(context instanceof ServletWebServerApplicationContext webContext)) {
            System.out.println("No servlet web server context available.");
            return;
        }

        int port = webContext.getWebServer().getPort();
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

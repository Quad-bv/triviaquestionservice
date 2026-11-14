
package nl.bramv.triviaquestionservice.exception.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationExceptionLoggerTest {

    private static final String PROVIDER_NAME = "Provider";

    private ApplicationExceptionLogger applicationExceptionLogger;
    private Logger logger;
    private ListAppender<ILoggingEvent> logAppender;
    private Level originalLevel;

    @BeforeEach
    void setUp() {
        // Arrange
        applicationExceptionLogger = new ApplicationExceptionLogger();

        logger = (Logger) LoggerFactory.getLogger(ApplicationExceptionLogger.class);
        originalLevel = logger.getLevel();
        logger.setLevel(Level.DEBUG);

        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(logAppender);
        logger.setLevel(originalLevel);
        logAppender.stop();
    }

    @ParameterizedTest
    @MethodSource("loggingScenarios")
    void log_ignoringNoMetadata_logsExceptionAtExpectedLevel(LoggingScenario scenario) {
        // Act
        applicationExceptionLogger.log(scenario.exception(), scenario.exception());

        // Assert
        assertLogEvent(scenario.exception(), scenario.expectedLevel());
    }

    static Stream<LoggingScenario> loggingScenarios() {
        return Stream.of(
                new LoggingScenario(
                        new DebugProviderExceptionI(PROVIDER_NAME, "Debug failure"),
                        Level.DEBUG
                ),
                new LoggingScenario(
                        new QuestionsProviderRateLimitExceptionI(
                                PROVIDER_NAME, "Rate limit reached"),
                        Level.INFO
                ),
                new LoggingScenario(
                        new WarnProviderExceptionI(PROVIDER_NAME, "Warning"),
                        Level.WARN
                ),
                new LoggingScenario(
                        new QuestionsProviderUnavailableExceptionI(
                                PROVIDER_NAME, "Unavailable"),
                        Level.ERROR
                )
        );
    }

    @ParameterizedTest
    @MethodSource("explicitLoggingScenarios")
    void log_withExplicitSourceAndLevel_logsExceptionAtExpectedLevel(
            LoggingScenario scenario
    ) {
        // Act
        applicationExceptionLogger.log(
                scenario.exception().getSource(),
                toLogLevel(scenario.expectedLevel()),
                scenario.exception()
        );

        // Assert
        assertLogEvent(scenario.exception(), scenario.expectedLevel());
    }

    static Stream<LoggingScenario> explicitLoggingScenarios() {
        return loggingScenarios();
    }

    private void assertLogEvent(Throwable exception, Level expectedLevel) {
        assertEquals(1, logAppender.list.size());

        ILoggingEvent event = logAppender.list.getFirst();

        assertAll(
                () -> assertEquals(expectedLevel, event.getLevel()),
                () -> assertEquals("{} failed: {}", event.getMessage()),
                () -> assertArrayEquals(
                        new Object[]{PROVIDER_NAME, exception.getMessage()},
                        event.getArgumentArray()
                ),
                () -> assertNotNull(event.getThrowableProxy()),
                () -> assertEquals(
                        exception.getClass().getName(),
                        event.getThrowableProxy().getClassName()
                ),
                () -> assertEquals(
                        exception.getMessage(),
                        event.getThrowableProxy().getMessage()
                )
        );
    }

    private static LogLevel toLogLevel(Level level) {
        return switch (level.levelInt) {
            case Level.DEBUG_INT -> LogLevel.DEBUG;
            case Level.INFO_INT -> LogLevel.INFO;
            case Level.WARN_INT -> LogLevel.WARN;
            case Level.ERROR_INT -> LogLevel.ERROR;
            default -> throw new IllegalArgumentException("Unsupported level: " + level);
        };
    }

    private record LoggingScenario(
            QuestionsProviderExceptionI exception,
            Level expectedLevel
    ) {}

    // Test-only exceptions to cover DEBUG and WARN logging levels,
    // which are not represented by the production provider exceptions.
    private static class DebugProviderExceptionI extends QuestionsProviderExceptionI {
        DebugProviderExceptionI(String providerName, String message) {
            super(providerName, message, LogLevel.DEBUG, null);
        }
    }

    private static class WarnProviderExceptionI extends QuestionsProviderExceptionI {
        WarnProviderExceptionI(String providerName, String message) {
            super(providerName, message, LogLevel.WARN, null);
        }
    }
}

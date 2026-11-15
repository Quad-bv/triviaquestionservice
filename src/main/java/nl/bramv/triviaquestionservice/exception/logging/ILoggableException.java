package nl.bramv.triviaquestionservice.exception.logging;

public interface ILoggableException {
    String getSource();

    LogLevel getLogLevel();
}
package nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions;

import nl.bramv.triviaquestionservice.exception.logging.LogLevel;

public class QuestionsProviderUnavailableException extends QuestionsProviderException {

  public QuestionsProviderUnavailableException(String providerName, String message, Throwable cause) {
    super(providerName, message, LogLevel.ERROR, cause);
  }

  public QuestionsProviderUnavailableException(String providerName, String message) {
    super(providerName, message, LogLevel.ERROR, null);
  }
}

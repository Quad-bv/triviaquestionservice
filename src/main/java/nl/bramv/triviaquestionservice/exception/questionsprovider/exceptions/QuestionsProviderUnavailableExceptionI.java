package nl.bramv.triviaquestionservice.exception.questionsprovider.exceptions;

import nl.bramv.triviaquestionservice.exception.logging.LogLevel;

// Indicates that the provider could not be reached or is temporarily unavailable.
public class QuestionsProviderUnavailableExceptionI extends QuestionsProviderExceptionI {

  public QuestionsProviderUnavailableExceptionI(String providerName, String message, Throwable cause) {
    super(providerName, message, LogLevel.ERROR, cause);
  }

  public QuestionsProviderUnavailableExceptionI(String providerName, String message) {
    this(providerName, message, null);
  }
}

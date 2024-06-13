package com.tech.engg5.metrics.statistics.processor.metrics.exception;

public class DatabaseException extends RuntimeException {

  public DatabaseException(String message, Throwable t) {
    super(message, t);
  }
}

package io.github.stu2301681017.MyooosicAPI.core;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.Set;

public class ServerConstraintViolationException extends ConstraintViolationException {
  public ServerConstraintViolationException(String message, Set<ConstraintViolation<?>> constraintViolations) {
    super(message, constraintViolations);
  }
}

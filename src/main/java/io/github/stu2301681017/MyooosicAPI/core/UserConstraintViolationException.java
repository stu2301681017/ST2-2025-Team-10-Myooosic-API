package io.github.stu2301681017.MyooosicAPI.core;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.Set;

public class UserConstraintViolationException extends ConstraintViolationException {
  public UserConstraintViolationException(String message, Set<ConstraintViolation<?>> constraintViolations) {
    super(message, constraintViolations);
  }
}

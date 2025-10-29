package io.github.stu2301681017.MyooosicAPI;

import io.github.stu2301681017.MyooosicAPI.app.ai.AIUnavailableException;
import io.github.stu2301681017.MyooosicAPI.app.auth.NotLoggedInException;
import io.github.stu2301681017.MyooosicAPI.app.auth.UserNotFoundException;
import io.github.stu2301681017.MyooosicAPI.app.auth.UsernameTakenException;
import io.github.stu2301681017.MyooosicAPI.app.auth.WrongPasswordException;
import io.github.stu2301681017.MyooosicAPI.core.*;
import io.github.stu2301681017.MyooosicAPI.app.ai.AIResponseException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericRuntimeException(RuntimeException ex) {
        return createExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown server error");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex) {

        Set<ConstraintViolation<?>> apiViolations = ex.getConstraintViolations()
                .stream()
                .filter(constraintViolation ->
                    constraintViolation.getConstraintDescriptor()
                            .getGroups()
                            .stream()
                            .anyMatch(g -> g == ApiConstraints.class)
                )
                .collect(Collectors.toSet());

        if (apiViolations.isEmpty()) {
            throw new ServerConstraintViolationException("Server violated given constraints", ex.getConstraintViolations());
        } else {
            throw new UserConstraintViolationException("User violated given constraints", apiViolations);
        }


    }

    @ExceptionHandler(UserConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserConstraintViolationException(UserConstraintViolationException ex) {
            String message = ex.getConstraintViolations()
                    .stream()
                    .map(cv -> cv.getPropertyPath() + " " + cv.getMessage())
                    .collect(Collectors.joining(", "));
        System.out.println(message);
            return createExceptionResponse(HttpStatus.BAD_REQUEST, "Invalid parameters: " + message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        // Collect all field errors into a single message
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return createExceptionResponse(HttpStatus.BAD_REQUEST, "Invalid parameters: " + message);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiResponse<Void>> handleServiceUnavailableException(ServiceUnavailableException ex) {
        return createExceptionResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service unavailable");
    }

    @ExceptionHandler(AIUnavailableException.class)
    public ResponseEntity<ApiResponse<Void>> handleAIUnavailableException(AIUnavailableException ex) {
        return createExceptionResponse(HttpStatus.SERVICE_UNAVAILABLE, "AI service unavailable");
    }

    @ExceptionHandler(AIResponseException.class)
    public ResponseEntity<ApiResponse<Void>> handleAIResponseException(AIResponseException ex) {
        logger.error("Error:", ex);
        return createExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Bad AI response");
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ApiResponse<Void>> handleWrongPasswordException(WrongPasswordException ex) {
        return createExceptionResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException ex) {
        return createExceptionResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    @ExceptionHandler(UsernameTakenException.class)
    public ResponseEntity<ApiResponse<Void>> handleUsernameTakenException(UsernameTakenException ex) {
        return createExceptionResponse(HttpStatus.CONFLICT, "Username is taken");
    }

    @ExceptionHandler(NotLoggedInException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotLoggedInException(NotLoggedInException ex) {
        return createExceptionResponse(HttpStatus.UNAUTHORIZED, "You are not logged in");
    }

    private ResponseEntity<ApiResponse<Void>> createExceptionResponse(HttpStatus status, String error) {
        return new ResponseEntity<>(new ApiResponse<Void>(null, status, error), status);
    }

}

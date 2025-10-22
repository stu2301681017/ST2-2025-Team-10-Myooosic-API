package io.github.stu2301681017.MyooosicAPI;

import io.github.stu2301681017.MyooosicAPI.ai.BadAIJsonException;
import io.github.stu2301681017.MyooosicAPI.core.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericRuntimeException(RuntimeException ex) {
        return createExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown server error");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getPropertyPath() + " " + cv.getMessage())
                .collect(Collectors.joining(", "));
        return createExceptionResponse(HttpStatus.BAD_REQUEST, "Invalid parameters: " + message);
    }


    @ExceptionHandler(BadAIJsonException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadAIJsonException(BadAIJsonException ex) {
        return createExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, "LLM returned bad Json");
    }

    @ExceptionHandler(BadAIJsonException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadAIResponseException(BadAIJsonException ex) {
        return createExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, "LLM returned bad data");
    }

    private ResponseEntity<ApiResponse<Void>> createExceptionResponse(HttpStatus status, String error) {
        return new ResponseEntity<>(new ApiResponse<Void>(null, status, error), status);
    }
}

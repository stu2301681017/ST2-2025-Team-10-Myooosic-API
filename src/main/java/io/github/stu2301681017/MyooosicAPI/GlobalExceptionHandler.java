package io.github.stu2301681017.MyooosicAPI;

import io.github.stu2301681017.MyooosicAPI.ai.BadAIJsonException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleGenericRuntimeException(RuntimeException ex) {
        return createExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Generic server error");
    }

    @ExceptionHandler(BadAIJsonException.class)
    public ResponseEntity<Map<String, Object>> handleBadAIJsonException(BadAIJsonException ex) {
        return createExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, "LLM returned bad Json");
    }

    private ResponseEntity<Map<String, Object>> createExceptionResponse(HttpStatus status, String error) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status);
        body.put("error", error);

        return new ResponseEntity<>(body, status);
    }
}

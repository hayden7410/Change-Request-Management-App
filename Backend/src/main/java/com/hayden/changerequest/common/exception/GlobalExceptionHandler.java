package com.hayden.changerequest.common.exception;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request) {

        Map<String, Object> response = Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Not Found",
                "message", exception.getMessage(),
                "path", request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
    @ExceptionHandler(InvalidRequestStateException.class)
public ResponseEntity<Map<String, Object>> handleInvalidRequestState(
        InvalidRequestStateException exception,
        HttpServletRequest request) {

    Map<String, Object> response = Map.of(
            "timestamp", Instant.now(),
            "status", HttpStatus.CONFLICT.value(),
            "error", "Conflict",
            "message", exception.getMessage(),
            "path", request.getRequestURI()
    );

    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(response);
}
}
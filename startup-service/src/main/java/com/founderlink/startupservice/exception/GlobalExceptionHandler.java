package com.founderlink.startupservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception,
                                                   HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return response(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
    }

    @ExceptionHandler(StartupNotFoundException.class)
    ResponseEntity<ErrorResponse> handleNotFound(StartupNotFoundException exception,
                                                 HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedAccessException exception,
                                                     HttpServletRequest request) {
        return response(HttpStatus.FORBIDDEN, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler({DataIntegrityViolationException.class, IllegalArgumentException.class})
    ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException exception,
                                                   HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "Invalid request", request, Map.of());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException exception,
                                                   HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "Invalid request body", request, Map.of());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnexpected(HttpServletRequest request) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred",
                request, Map.of());
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, String message,
                                                   HttpServletRequest request,
                                                   Map<String, String> fieldErrors) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(Instant.now(), status.value(), message,
                        request.getRequestURI(), fieldErrors));
    }
}

package com.founderlink.authservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e, HttpServletRequest r) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(f.getField(), f.getDefaultMessage());
        }
        return response(HttpStatus.BAD_REQUEST, "Validation failed", r, errors);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    ResponseEntity<ErrorResponse> duplicate(DuplicateEmailException e, HttpServletRequest r) {
        return response(HttpStatus.CONFLICT, e.getMessage(), r, Map.of());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseEntity<ErrorResponse> invalidCredentials(InvalidCredentialsException e, HttpServletRequest r) {
        return response(HttpStatus.UNAUTHORIZED, e.getMessage(), r, Map.of());
    }

    @ExceptionHandler(BadRequestException.class)
    ResponseEntity<ErrorResponse> bad(BadRequestException e, HttpServletRequest r) {
        return response(HttpStatus.BAD_REQUEST, e.getMessage(), r, Map.of());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> unexpected(Exception e, HttpServletRequest r) {
        InvalidCredentialsException invalidCredentials = invalidCredentialsCause(e);
        if (invalidCredentials != null) {
            return response(HttpStatus.UNAUTHORIZED, invalidCredentials.getMessage(), r, Map.of());
        }
        logger.error("Unhandled request failure", e);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", r, Map.of());
    }

    private InvalidCredentialsException invalidCredentialsCause(Throwable e) {
        Throwable current = e;
        while (current != null) {
            if (current instanceof InvalidCredentialsException invalidCredentials) {
                return invalidCredentials;
            }
            current = current.getCause();
        }
        return null;
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus s, String m, HttpServletRequest r,
                                                    Map<String, String> fields) {
        return ResponseEntity.status(s)
                .body(new ErrorResponse(Instant.now(), s.value(), m, r.getRequestURI(), fields));
    }
}

package com.governai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException exception
    ) {

        Map<String, String> validationErrors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        validationErrors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", OffsetDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation failed");
        response.put("errors", validationErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(
            HttpMessageNotReadableException exception
    ) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", OffsetDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Invalid request body");
        response.put(
                "message",
                "The JSON request contains an invalid value or format."
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(OrganizationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrganizationNotFound(
            OrganizationNotFoundException exception
    ) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", OffsetDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Organization not found");
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(PolicyDocumentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePolicyDocumentNotFound(
            PolicyDocumentNotFoundException exception
    ) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", OffsetDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Policy document not found");
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(PolicySectionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePolicySectionNotFound(
            PolicySectionNotFoundException exception
    ) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", OffsetDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Policy section not found");
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception exception
    ) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", OffsetDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal server error");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "Resource not found", exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException exception) {
        return error(HttpStatus.BAD_REQUEST, "Invalid request", exception.getMessage());
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String error, String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", OffsetDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }
}

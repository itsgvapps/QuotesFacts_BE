package com.gvapps.quotesfacts.exception;

import com.gvapps.quotesfacts.dto.response.APIResponse;
import com.gvapps.quotesfacts.util.ResponseUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<APIResponse> handleApiException(ApiException ex) {
        log.warn("[GlobalExceptionHandler] ApiException: {} - {}", ex.getCode(), ex.getMessage());
        APIResponse response = ResponseUtils.error(ex.getCode(), ex.getDescription(), ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<APIResponse> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("[GlobalExceptionHandler] EntityNotFoundException: {}", ex.getMessage());
        APIResponse response = ResponseUtils.error("404", "Entity Not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<APIResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.error("[GlobalExceptionHandler] DataIntegrityViolationException: {}", ex.getMessage());
        APIResponse response = ResponseUtils.error("409", "Data Integrity Violation", "Duplicate or invalid data.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("[GlobalExceptionHandler] Validation Error: {}", ex.getMessage());
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed.");
        APIResponse response = ResponseUtils.error("400", "Validation Error", message);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<APIResponse> handleRuntime(RuntimeException ex) {
        log.error("[GlobalExceptionHandler] RuntimeException: {}", ex.getMessage(), ex);
        APIResponse response = ResponseUtils.error("500", "Runtime Error", ex.getMessage());
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse> handleGeneric(Exception ex) {
        log.error("[GlobalExceptionHandler] Exception: {}", ex.getMessage(), ex);
        APIResponse response = ResponseUtils.error("500", "Unexpected Error", ex.getMessage());
        return ResponseEntity.internalServerError().body(response);
    }
}

package sesi.petvita.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        System.err.println("Tentativa de acesso negado: " + ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Acesso Negado. Você não tem permissão para realizar esta ação.");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN); // Retorna o erro 403
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Um ou mais campos já estão em uso. Por favor, verifique seus dados.";
        String rootMessage = ex.getMostSpecificCause().getMessage().toLowerCase();

        if (rootMessage.contains("email")) {
            message = "Este e-mail já está cadastrado.";
        } else if (rootMessage.contains("phone")) {
            message = "Este telefone já está cadastrado.";
        } else if (rootMessage.contains("rg")) {
            message = "Este RG já está cadastrado.";
        } else if (rootMessage.contains("crmv")) {
            message = "Este CRMV já está cadastrado.";
        }

        return new ResponseEntity<>(new ErrorResponse(message), HttpStatus.CONFLICT);
    }
}
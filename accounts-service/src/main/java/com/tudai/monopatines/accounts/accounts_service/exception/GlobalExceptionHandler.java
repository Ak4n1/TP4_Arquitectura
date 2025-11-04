package com.tudai.monopatines.accounts.accounts_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manejador global de excepciones para el servicio de cuentas.
 * Captura y formatea las excepciones para devolver respuestas HTTP apropiadas.
 * 
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de tipo AccountNotFoundException.
     * Retorna una respuesta HTTP 404 (Not Found) cuando no se encuentra una cuenta.
     * 
     * @param ex Excepción de cuenta no encontrada
     * @return ResponseEntity con código HTTP 404 y mensaje de error
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Account Not Found",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de tipo UserNotFoundException.
     * Retorna una respuesta HTTP 404 (Not Found) cuando no se encuentra un usuario.
     * 
     * @param ex Excepción de usuario no encontrado
     * @return ResponseEntity con código HTTP 404 y mensaje de error
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "User Not Found",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de tipo AccountAlreadyExistsException.
     * Retorna una respuesta HTTP 409 (Conflict) cuando se intenta crear una cuenta con un número identificatorio que ya existe.
     * 
     * @param ex Excepción de cuenta ya existente
     * @return ResponseEntity con código HTTP 409 y mensaje de error
     */
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAccountAlreadyExistsException(AccountAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Account Already Exists",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Maneja excepciones de tipo UserAlreadyExistsException.
     * Retorna una respuesta HTTP 409 (Conflict) cuando se intenta crear un usuario con un email que ya existe.
     * 
     * @param ex Excepción de usuario ya existente
     * @return ResponseEntity con código HTTP 409 y mensaje de error
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "User Already Exists",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Maneja excepciones de tipo InsufficientBalanceException.
     * Retorna una respuesta HTTP 400 (Bad Request) cuando se intenta descontar un saldo mayor al disponible.
     * 
     * @param ex Excepción de saldo insuficiente
     * @return ResponseEntity con código HTTP 400 y mensaje de error
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Insufficient Balance",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de tipo AccountInactiveException.
     * Retorna una respuesta HTTP 400 (Bad Request) cuando se intenta realizar una operación en una cuenta inactiva (anulada).
     * 
     * @param ex Excepción de cuenta inactiva
     * @return ResponseEntity con código HTTP 400 y mensaje de error
     */
    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<ErrorResponse> handleAccountInactiveException(AccountInactiveException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Account Inactive",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de validación de Spring (MethodArgumentNotValidException).
     * Retorna una respuesta HTTP 400 (Bad Request) con detalles de los errores de validación.
     * 
     * @param ex Excepción de validación de argumentos
     * @return ResponseEntity con código HTTP 400 y mapa de errores de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        List<org.springframework.validation.ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        for (org.springframework.validation.ObjectError error : allErrors) {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("message", "Invalid input data");
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones genéricas no capturadas por los otros handlers.
     * Retorna una respuesta HTTP 500 (Internal Server Error) para errores inesperados.
     * 
     * @param ex Excepción genérica
     * @return ResponseEntity con código HTTP 500 y mensaje de error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Clase para representar errores en las respuestas HTTP.
     */
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;

        public ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
        }

        // Getters
        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }
}


package ma.jaouad.accountservice.exceptions;

import lombok.extern.slf4j.Slf4j;
import ma.jaouad.coreapi.exceptions.AccountNotFoundException;
import ma.jaouad.coreapi.exceptions.InsufficientBalanceException;
import ma.jaouad.coreapi.exceptions.InvalidAccountStatusException;
import ma.jaouad.coreapi.exceptions.NegativeBalanceException;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.queryhandling.QueryExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

/**
 * Gestionnaire global d'exceptions pour l'API REST
 * 
 * Utilise String pour les timestamps au lieu de Instant
 * pour eviter les problemes de serialisation
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private String getCurrentTimestamp() {
        return Instant.now().toString();
    }
    
    /**
     * Extrait la cause racine d'une exception wrappee par Axon
     * et cherche aussi les exceptions de type AccountNotFoundException dans toute la chaine
     */
    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }
    
    /**
     * Cherche une exception d'un type specifique dans toute la chaine d'exceptions
     */
    private <T extends Throwable> T findExceptionInChain(Throwable throwable, Class<T> exceptionClass) {
        Throwable current = throwable;
        while (current != null) {
            if (exceptionClass.isInstance(current)) {
                return exceptionClass.cast(current);
            }
            current = current.getCause();
            if (current == throwable) {
                break; // Eviter boucle infinie
            }
        }
        return null;
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(
            AccountNotFoundException ex, WebRequest request) {
        log.error("Account not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                getCurrentTimestamp(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(
            InsufficientBalanceException ex, WebRequest request) {
        log.error("Insufficient balance: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                getCurrentTimestamp(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAccountStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAccountStatusException(
            InvalidAccountStatusException ex, WebRequest request) {
        log.error("Invalid account status: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                getCurrentTimestamp(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NegativeBalanceException.class)
    public ResponseEntity<ErrorResponse> handleNegativeBalanceException(
            NegativeBalanceException ex, WebRequest request) {
        log.error("Negative balance: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                getCurrentTimestamp(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(QueryExecutionException.class)
    public ResponseEntity<ErrorResponse> handleQueryExecutionException(
            QueryExecutionException ex, WebRequest request) {
        log.error("Query execution failed: {}", ex.getMessage());
        
        // Chercher AccountNotFoundException dans toute la chaine d'exceptions
        AccountNotFoundException accountNotFoundException = findExceptionInChain(ex, AccountNotFoundException.class);
        if (accountNotFoundException != null) {
            log.info("Found AccountNotFoundException in exception chain, returning 404");
            return handleAccountNotFoundException(accountNotFoundException, request);
        }
        
        // Fallback: verifier le message d'erreur pour AccountNotFoundException
        // (cas ou Axon Server serialise l'exception et perd le type original)
        Throwable rootCause = getRootCause(ex);
        String message = rootCause.getMessage() != null ? rootCause.getMessage() : ex.getMessage();
        
        if (message != null && message.contains("Account not found")) {
            log.info("Detected 'Account not found' in error message, returning 404");
            ErrorResponse error = new ErrorResponse(
                    getCurrentTimestamp(),
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    message.split("\nCaused by")[0], // Prendre seulement la premiere ligne
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        
        // Sinon retourner une erreur 500
        log.warn("No specific handler found for exception chain. Root cause type: {}", rootCause.getClass().getName());
        
        ErrorResponse error = new ErrorResponse(
                getCurrentTimestamp(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Query Execution Error",
                message,
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CommandExecutionException.class)
    public ResponseEntity<ErrorResponse> handleCommandExecutionException(
            CommandExecutionException ex, WebRequest request) {
        log.error("Command execution failed: {}", ex.getMessage());
        
        // Chercher les exceptions metier dans toute la chaine
        InsufficientBalanceException insufficientBalanceException = 
            findExceptionInChain(ex, InsufficientBalanceException.class);
        if (insufficientBalanceException != null) {
            return handleInsufficientBalanceException(insufficientBalanceException, request);
        }
        
        InvalidAccountStatusException invalidAccountStatusException = 
            findExceptionInChain(ex, InvalidAccountStatusException.class);
        if (invalidAccountStatusException != null) {
            return handleInvalidAccountStatusException(invalidAccountStatusException, request);
        }
        
        NegativeBalanceException negativeBalanceException = 
            findExceptionInChain(ex, NegativeBalanceException.class);
        if (negativeBalanceException != null) {
            return handleNegativeBalanceException(negativeBalanceException, request);
        }
        
        AccountNotFoundException accountNotFoundException = 
            findExceptionInChain(ex, AccountNotFoundException.class);
        if (accountNotFoundException != null) {
            return handleAccountNotFoundException(accountNotFoundException, request);
        }
        
        // Sinon retourner une erreur 400 generique
        Throwable rootCause = getRootCause(ex);
        String message = rootCause.getMessage() != null ? rootCause.getMessage() : ex.getMessage();
        ErrorResponse error = new ErrorResponse(
                getCurrentTimestamp(),
                HttpStatus.BAD_REQUEST.value(),
                "Command Execution Error",
                message,
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        log.error("Illegal argument: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                getCurrentTimestamp(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        log.error("Runtime exception: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
                getCurrentTimestamp(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
                getCurrentTimestamp(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred: " + ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

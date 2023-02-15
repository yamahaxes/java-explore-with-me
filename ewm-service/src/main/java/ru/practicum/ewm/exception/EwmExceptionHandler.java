package ru.practicum.ewm.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class EwmExceptionHandler {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class,
            BadRequestException.class})
    public ResponseEntity<ErrorMessage> badRequest(RuntimeException ex) {

        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class,
            DataIntegrityViolationException.class})
    public ResponseEntity<ErrorMessage> conflict(RuntimeException ex) {

        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.CONFLICT.name(),
                "Integrity constraint has been violated.",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<ErrorMessage> notFound(RuntimeException ex) {

        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.NOT_FOUND.name(),
                "The required object was not found.",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(value = {ForbiddenException.class})
    public ResponseEntity<ErrorMessage> forbidden(RuntimeException ex) {
        ErrorMessage errorMessage = new ErrorMessage(
                "FORBIDDEN",
                "For the requested operation the conditions are not met.",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
    }
}

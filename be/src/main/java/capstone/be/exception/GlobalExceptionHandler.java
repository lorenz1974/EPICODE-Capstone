package capstone.be.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    //
    // USERNAME_NOT_FOUND is here for securiy reasons to avoid giving hints to
    // hackers about the existence of a username
    //
    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class,
            HttpMessageNotReadableException.class, MethodArgumentNotValidException.class,
            BadCredentialsException.class })
    public ResponseEntity<ExceptionMessage> handleBadRequestExceptions(Exception e) {
        ExceptionMessage exceptionMessage = new ExceptionMessage();
        exceptionMessage.setMessage(e.getMessage());
        exceptionMessage.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        exceptionMessage.setError("Bad Request");
        exceptionMessage.setTimestamp(LocalDateTime.now());

        log.error("IllegalArgumentException or IllegalStateException or many other exceptions: {}", e.getMessage());

        return new ResponseEntity<>(exceptionMessage, HttpStatus.BAD_REQUEST);
    }

    //
    // BAD REQUEST for ConstraintViolationException has a different algorithm so it
    // stands alone
    //
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionMessage> handleConstraintViolationException(ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            if (fieldName.contains(".")) {
                fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
            }
            errors.put(fieldName, violation.getMessage());
        }

        ExceptionMessage exceptionMessage = new ExceptionMessage();
        exceptionMessage.setMessage("Fields validation failed. Check 'errors' field for details.");
        exceptionMessage.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        exceptionMessage.setError(errors);
        exceptionMessage.setTimestamp(LocalDateTime.now());

        log.error("Constraint violation exception: {}", e.getMessage());

        return new ResponseEntity<>(exceptionMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ EntityNotFoundException.class })
    public ResponseEntity<ExceptionMessage> handleNotFoundExceptions(EntityNotFoundException e) {
        ExceptionMessage exceptionMessage = new ExceptionMessage();
        exceptionMessage.setMessage(e.getMessage());
        exceptionMessage.setStatus(String.valueOf(HttpStatus.NOT_FOUND.value()));
        exceptionMessage.setError("Entity not found");
        exceptionMessage.setTimestamp(LocalDateTime.now());

        log.error("Entity not found exception: {}", e.getMessage());

        return new ResponseEntity<>(exceptionMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ EntityExistsException.class })
    public ResponseEntity<ExceptionMessage> handleEntityExistsException(EntityExistsException e) {
        ExceptionMessage exceptionMessage = new ExceptionMessage();
        exceptionMessage.setMessage(e.getMessage());
        exceptionMessage.setStatus(String.valueOf(HttpStatus.CONFLICT.value()));
        exceptionMessage.setError("Same entity already exists");
        exceptionMessage.setTimestamp(LocalDateTime.now());

        log.error("Entity already exists exception: {}", e.getMessage());

        return new ResponseEntity<>(exceptionMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ UsernameNotFoundException.class })
    public ResponseEntity<ExceptionMessage> handleUsernameNotFoundException(UsernameNotFoundException e) {
        ExceptionMessage exceptionMessage = new ExceptionMessage();
        exceptionMessage.setMessage(e.getMessage());
        exceptionMessage.setStatus(String.valueOf(HttpStatus.NOT_FOUND.value()));
        exceptionMessage.setError("Entity not found");
        exceptionMessage.setTimestamp(LocalDateTime.now());

        log.error("Username not found or wrong password or Entity not found exception: {}", e.getMessage());

        return new ResponseEntity<>(exceptionMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ RuntimeException.class })
    public ResponseEntity<ExceptionMessage> handleRuntimeExceptions(RuntimeException e) {
        ExceptionMessage exceptionMessage = new ExceptionMessage();
        exceptionMessage.setMessage(e.getMessage());
        exceptionMessage.setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        exceptionMessage.setError("Internal Server Error. RuntimeException occurred.");
        exceptionMessage.setTimestamp(LocalDateTime.now());

        log.error("Runtime exception: {}", e.getMessage());

        return new ResponseEntity<>(exceptionMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //
    // This is a catch-all for all other exceptions
    //
    @ExceptionHandler({ Exception.class })
    public ResponseEntity<ExceptionMessage> handleException(Exception e) {
        ExceptionMessage exceptionMessage = new ExceptionMessage();
        String message = "Internal Server Error. Exception: " + e.getMessage();

        exceptionMessage.setMessage(message);
        exceptionMessage.setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        exceptionMessage.setError("Internal Server Error");
        exceptionMessage.setTimestamp(LocalDateTime.now());

        log.error(message);

        return new ResponseEntity<>(exceptionMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
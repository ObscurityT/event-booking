package com.amanda.event_platform.event.exception;

import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<String>handleEventNotFoundException(EventNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(InvalidPeriodException.class)
    public ResponseEntity<String>handleInvalidPeriodException(InvalidPeriodException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(InvalidCapacityException.class)
    public ResponseEntity<String>handleInvalidCapacityException(InvalidCapacityException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String>handleValidationErrors(MethodArgumentNotValidException ex){
        String errors = ex.getBindingResult().getFieldErrors().stream().map(error -> error.getField() + ": "
                + error.getDefaultMessage()).collect(java.util.stream.Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

}

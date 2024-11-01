package com.acme.roombooker.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class BookingExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(BookingExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageDTO> handleEntityNotFoundException(EntityNotFoundException exception) {
        ErrorMessageDTO error = new ErrorMessageDTO();
        error.setMessage(exception.getMessage());
        error.setTimestamp(LocalDateTime.now());

        logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookingException.class)
    public ResponseEntity<ErrorMessageDTO> handleBookingException(BookingException exception) {
        ErrorMessageDTO error = new ErrorMessageDTO();
        error.setMessage(exception.getMessage());
        error.setTimestamp(LocalDateTime.now());

        logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        List<String> errors = new ArrayList<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });

        logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorMessageDTO> handleInvalidFormatException(InvalidFormatException exception) {
        ErrorMessageDTO error = new ErrorMessageDTO();
        error.setMessage(ErrorMessages.ARB_006_INVALID_DATA_INPUT.name());
        error.setTimestamp(LocalDateTime.now());

        logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * GENERIC
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> handleException(Exception exception) {
        exception.printStackTrace();

        ErrorMessageDTO error = new ErrorMessageDTO();
        error.setMessage(ErrorMessages.ARB_501_CONTACT_YOUR_ADMINISTRATOR.name());
        error.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}

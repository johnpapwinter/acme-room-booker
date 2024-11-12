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
        ErrorMessageDTO errorDTO = ErrorMessageDTO.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookingException.class)
    public ResponseEntity<ErrorMessageDTO> handleBookingException(BookingException exception) {
        ErrorMessageDTO errorDTO = ErrorMessageDTO.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
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
        ErrorMessageDTO errorDTO = ErrorMessageDTO.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(ErrorMessages.ARB_006_INVALID_DATA_INPUT)
                .timestamp(LocalDateTime.now())
                .build();

        logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    /**
     * GENERIC
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> handleException(Exception exception) {
        exception.printStackTrace();

        ErrorMessageDTO errorDTO = ErrorMessageDTO.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ErrorMessages.ARB_501_CONTACT_YOUR_ADMINISTRATOR)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}

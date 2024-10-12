package com.acme.roombooker.exception;

public class BookingException extends RuntimeException {

    public BookingException() {
        super();
    }

    public BookingException(String message) {
        super(message);
    }
}

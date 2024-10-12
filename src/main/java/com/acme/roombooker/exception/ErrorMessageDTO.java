package com.acme.roombooker.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorMessageDTO {

    private String message;
    private LocalDateTime timestamp;

}

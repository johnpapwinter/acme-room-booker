package com.acme.roombooker.security.dto;

import com.acme.roombooker.exception.ValidationErrorMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = ValidationErrorMessages.ARB1009_USERNAME_IS_REQUIRED)
    private String username;
    @NotBlank(message = ValidationErrorMessages.ARB1010_PASSWORD_IS_REQUIRED)
    private String password;

}

package com.acme.roombooker.security.dto;

import com.acme.roombooker.exception.ErrorMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = ErrorMessages.ARB1009_USERNAME_IS_REQUIRED)
    private String username;
    @NotBlank(message = ErrorMessages.ARB1010_PASSWORD_IS_REQUIRED)
    private String password;

}

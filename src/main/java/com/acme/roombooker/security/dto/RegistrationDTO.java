package com.acme.roombooker.security.dto;

import com.acme.roombooker.exception.ErrorMessages;
import com.acme.roombooker.utils.annotations.PasswordMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@PasswordMatch(
        password = "password",
        confirmPassword = "confirmPassword"
)
public class RegistrationDTO {

    @NotBlank(message = ErrorMessages.ARB1007_NAME_IS_REQUIRED)
    private String name;
    @NotBlank(message = ErrorMessages.ARB1008_EMAIL_IS_REQUIRED)
    @Email(message = ErrorMessages.ARB1003_INVALID_EMAIL_ADDRESS)
    private String email;
    @NotBlank(message = ErrorMessages.ARB1009_USERNAME_IS_REQUIRED)
    private String username;
    @NotBlank(message = ErrorMessages.ARB1010_PASSWORD_IS_REQUIRED)
    private String password;
    @NotBlank(message = ErrorMessages.ARB1012_CONFIRMATION_PASSWORD_REQUIRED)
    private String confirmPassword;

}

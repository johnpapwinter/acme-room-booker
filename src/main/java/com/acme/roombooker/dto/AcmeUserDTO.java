package com.acme.roombooker.dto;

import com.acme.roombooker.domain.enums.AcmeRole;
import com.acme.roombooker.exception.ValidationErrorMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AcmeUserDTO {

    @NotBlank(message = ValidationErrorMessages.ARB1007_NAME_IS_REQUIRED)
    private String name;
    @NotBlank(message = ValidationErrorMessages.ARB1008_EMAIL_IS_REQUIRED)
    @Email(message = ValidationErrorMessages.ARB1003_INVALID_EMAIL_ADDRESS)
    private String email;
    @NotBlank(message = ValidationErrorMessages.ARB1009_USERNAME_IS_REQUIRED)
    private String username;
    private AcmeRole role;

}

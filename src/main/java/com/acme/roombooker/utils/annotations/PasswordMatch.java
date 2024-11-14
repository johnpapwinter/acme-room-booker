package com.acme.roombooker.utils.annotations;

import com.acme.roombooker.exception.ErrorMessages;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatch {
    String message() default ErrorMessages.ARB1011_PASSWORDS_DO_NOT_MATCH;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String password();
    String confirmPassword();

}

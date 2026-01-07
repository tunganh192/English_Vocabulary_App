package com.honda.englishapp.english_learning_backend.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotEmptyPatchRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmptyPatchRequest {
    String message() default "PATCH_REQUEST_EMPTY";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
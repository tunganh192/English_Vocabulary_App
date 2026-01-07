package com.honda.englishapp.english_learning_backend.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)  // anotation sẽ được apply ở đâu
@Retention(RetentionPolicy.RUNTIME)  // anotation sẽ được xử lí lúc nào
@Constraint(
        validatedBy = {DobValidator.class}  // class xử lí validator
)
public @interface DobConstrain {
    String message() default "Invalid date of birh";

    int min();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

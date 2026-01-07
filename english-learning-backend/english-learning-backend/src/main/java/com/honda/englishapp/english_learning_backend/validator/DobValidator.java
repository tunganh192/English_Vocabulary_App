package com.honda.englishapp.english_learning_backend.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DobValidator implements ConstraintValidator<DobConstrain, LocalDate> {

    private int min;

    @Override // Hàm xử lí data có đúng hay không
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (Objects.isNull(value))
            return true;

        long years = ChronoUnit.YEARS.between(value, LocalDate.now()); // Trả về số năm giữa thời gian hiện tại và value

        return years >= min;
    }

    @Override // Khởi tạo mỗi khi cóntrain khởi tạo
    public void initialize(DobConstrain constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
    }
}

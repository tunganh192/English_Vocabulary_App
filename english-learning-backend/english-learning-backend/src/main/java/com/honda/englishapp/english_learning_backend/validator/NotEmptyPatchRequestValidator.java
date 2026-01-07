package com.honda.englishapp.english_learning_backend.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class NotEmptyPatchRequestValidator implements ConstraintValidator<NotEmptyPatchRequest, Object> {

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return false;
        }

        // Lấy tất cả trường của DTO
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                // Kiểm tra nếu có ít nhất một trường không null hoặc không rỗng (cho String)
                if (value != null) {
                    if (value instanceof String stringValue) {
                        if (!stringValue.trim().isBlank()) { // Kiểm tra String không rỗng và không chỉ chứa khoảng trắng
                            return true;
                        }
                    } else {
                        return true; // Các kiểu khác (Integer, LocalDate, v.v.) không null là hợp lệ
                    }
                }
            } catch (IllegalAccessException e) {
                return false;
            }
        }
        return false; // Tất cả trường đều null hoặc String rỗng/khoảng trắng
    }
}
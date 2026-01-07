package com.honda.englishapp.english_learning_backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ADMIN,
    TEACHER,
    USER;


    @JsonCreator
    public static Role fromString(String value) {
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("INVALID_ROLE");
        }
    }
}

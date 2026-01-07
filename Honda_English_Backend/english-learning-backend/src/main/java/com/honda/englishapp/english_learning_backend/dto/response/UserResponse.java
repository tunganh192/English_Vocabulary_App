package com.honda.englishapp.english_learning_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String displayName;
    Integer dailyGoal;
    LocalDate dateOfBirth;
    String role;
}

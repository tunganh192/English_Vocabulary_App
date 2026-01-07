package com.honda.englishapp.english_learning_backend.dto.request.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honda.englishapp.english_learning_backend.validator.DobConstrain;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @Size(min = 6, max = 64, message = "PASSWORD_LENGTH_INVALID")
    String password;

    @Size(min = 1,max = 50, message = "DISPLAY_NAME_LENGTH_INVALID")
    String displayName;

    @Min(value = 1, message = "DAILY_GOAL_MIN")
    Integer dailyGoal;

    @DobConstrain(min = 6, message = "INVALID_DOB")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth; // can sua
}

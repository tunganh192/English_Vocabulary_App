package com.honda.englishapp.english_learning_backend.dto.request.Reminder;

import com.honda.englishapp.english_learning_backend.enums.RepeatType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReminderCreationRequest {
    @NotNull(message = "USER_ID_REQUIRED")
    String userId;

    @NotNull(message = "TIME_REQUIRED")
    LocalTime time;

    @NotNull(message = "REPEAT_TYPE_REQUIRED")
    RepeatType repeatType;

    LocalTime repeatInterval;
}

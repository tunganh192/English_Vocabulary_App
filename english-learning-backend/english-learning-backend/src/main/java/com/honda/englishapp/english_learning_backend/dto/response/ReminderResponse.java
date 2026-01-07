package com.honda.englishapp.english_learning_backend.dto.response;

import com.honda.englishapp.english_learning_backend.enums.RepeatType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReminderResponse {
    String id;
    String time;
    RepeatType repeatType;
    String repeatInterval;
    Boolean isEnabled;
}

package com.honda.englishapp.english_learning_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLessonResponse {
    Long id;
    String userId;
    Long categoryId;
    Date joinedAt;
}

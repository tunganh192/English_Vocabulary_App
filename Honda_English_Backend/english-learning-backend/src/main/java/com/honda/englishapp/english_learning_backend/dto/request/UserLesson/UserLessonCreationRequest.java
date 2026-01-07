package com.honda.englishapp.english_learning_backend.dto.request.UserLesson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLessonCreationRequest {
    @NotBlank(message = "USER_ID_REQUIRED")
    String userId;

    @NotNull(message = "CATEGORY_ID_REQUIRED")
    Long categoryId;
}

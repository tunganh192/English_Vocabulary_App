package com.honda.englishapp.english_learning_backend.dto.request.Question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerSubmissionRequest {
    @NotBlank(message = "USER_ID_REQUIRED")
    String userId;

    @NotNull(message = "WORD_ID_REQUIRED")
    Long wordId;

    @NotNull(message = "IS_CORRECT_REQUIRED")
    Boolean isCorrect;
}

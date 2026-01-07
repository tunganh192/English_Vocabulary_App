package com.honda.englishapp.english_learning_backend.dto.request.LearnedWord;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LearnedWordCreationRequest {
    @NotBlank(message = "USER_ID_REQUIRED")
    String userId;

    @NotNull(message = "WORD_ID_REQUIRED")
    @Positive(message = "WORD_ID_POSITIVE")
    Long wordId;
}

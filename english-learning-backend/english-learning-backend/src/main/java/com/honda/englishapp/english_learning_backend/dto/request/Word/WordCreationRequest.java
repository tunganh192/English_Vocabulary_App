package com.honda.englishapp.english_learning_backend.dto.request.Word;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WordCreationRequest {
    @NotBlank(message = "ENGLISH_WORD_REQUIRED")
    @Size(min = 1, max = 100, message = "ENGLISH_WORD_LENGTH_INVALID")
    String englishWord;

    //@NotEmpty(message = "ENGLISH_WORD_REQUIRED")
    @Nullable
    @Size(min = 1, max = 50, message = "PRONUNCIATION_LENGTH_INVALID")
    String pronunciation;

    @NotBlank(message = "VIETNAMESE_MEANING_REQUIRED")
    @Size(min = 1, max = 100, message = "VIETNAMESE_MEANING_LENGTH_INVALID")
    String vietnameseMeaning;

    @NotNull(message = "CATEGORY_ID_REQUIRED")
    Long categoryId;

    @NotBlank(message = "CREATED_BY_REQUIRED")
    String createdBy;
}

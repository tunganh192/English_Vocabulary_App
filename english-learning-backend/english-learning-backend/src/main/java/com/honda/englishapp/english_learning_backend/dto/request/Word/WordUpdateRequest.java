package com.honda.englishapp.english_learning_backend.dto.request.Word;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WordUpdateRequest {
    @Size(min = 1, max = 100, message = "ENGLISH_WORD_LENGTH_INVALID")
    String englishWord;

    @Size(min = 1, max = 50, message = "PRONUNCIATION_LENGTH_INVALID")
    String pronunciation;

    @Size(min = 1, max = 100, message = "VIETNAMESE_MEANING_LENGTH_INVALID")
    String vietnameseMeaning;

    @NotNull(message = "IS_ACTIVE_REQUIRED")
    Boolean isActive;

    @NotNull(message = "CATEGORY_ID_REQUIRED")
    Long categoryId;
}

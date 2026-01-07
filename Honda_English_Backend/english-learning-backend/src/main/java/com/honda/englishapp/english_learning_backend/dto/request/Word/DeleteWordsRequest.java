package com.honda.englishapp.english_learning_backend.dto.request.Word;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteWordsRequest {
    @NotEmpty(message = "WORD_ID_REQUIRED")
    List<Long> ids;
}

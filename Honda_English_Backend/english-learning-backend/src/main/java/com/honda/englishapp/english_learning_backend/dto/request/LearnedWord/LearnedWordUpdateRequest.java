package com.honda.englishapp.english_learning_backend.dto.request.LearnedWord;

import com.honda.englishapp.english_learning_backend.validator.NotEmptyPatchRequest;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NotEmptyPatchRequest

public class LearnedWordUpdateRequest {
    Long id;

    @Min(value = 0, message = "CORRECT_COUNT_MIN")
    int correctCount;

    @Min(value = 0, message = "WRONG_COUNT_MIN")
    int wrongCount;

    @Min(value = 0, message = "CORRECT_STREAK_MIN")
    int correctStreak;

    boolean isMastered;
}

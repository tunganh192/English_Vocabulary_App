package com.honda.englishapp.english_learning_backend.dto.response.Questtions;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerSubmissionResponse {
    Long wordId;
    Integer correctStreak;
    Boolean isMastered;
}

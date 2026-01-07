package com.honda.englishapp.english_learning_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LearnedWordResponse {
    Long id;
    String userId;
    Long wordId;  // trong nay co : id, englishWord, vietnameseMeaning, pronunciation
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime dateLearned;
    int correctCount;
    int wrongCount;
    int correctStreak;
    boolean isMastered;
}

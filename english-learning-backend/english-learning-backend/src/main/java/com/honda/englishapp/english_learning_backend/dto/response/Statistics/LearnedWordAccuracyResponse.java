package com.honda.englishapp.english_learning_backend.dto.response.Statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LearnedWordAccuracyResponse {
    String userId;
    String displayName;
    Long categoryId;
    String categoryName;
    long learnedCount;
    long correctCount;
    long totalCount;          
}
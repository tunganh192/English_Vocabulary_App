package com.honda.englishapp.english_learning_backend.dto.response.Statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WordCountResponse {
    Long categoryId;
    String categoryName;
    long wordCount;
}

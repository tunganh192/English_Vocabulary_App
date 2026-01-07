package com.honda.englishapp.english_learning_backend.dto.response.Questtions;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrueFalseQuestionResponse {
    Long wordId;
    String word;
    String displayedMeaning;
}

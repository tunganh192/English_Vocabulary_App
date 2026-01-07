package com.honda.englishapp.english_learning_backend.dto.response.Questtions;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MultipleChoiceQuestionResponse {
    Long wordId;
    String word;
    List<String> options; // 4 đáp án (1 đúng, 3 sai)
}

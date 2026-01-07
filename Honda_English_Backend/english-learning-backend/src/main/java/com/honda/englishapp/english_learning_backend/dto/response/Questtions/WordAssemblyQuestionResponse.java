package com.honda.englishapp.english_learning_backend.dto.response.Questtions;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WordAssemblyQuestionResponse {
    Long wordId;
    String meaning; // vietnameseMeaning
    List<String> parts; // Word parts and distractors
}

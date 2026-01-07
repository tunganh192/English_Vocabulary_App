package com.honda.englishapp.english_learning_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WordResponse {
    Long id;
    String englishWord;
    String vietnameseMeaning;
    String pronunciation;
    String createdBy;
    Long categoryId; // trong nay co id , name, parendId
}

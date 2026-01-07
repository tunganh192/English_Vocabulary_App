package com.honda.englishapp.english_learning_backend.dto.request.Question;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckAnswerRequest {
    String userId;
    Long wordId;
    String answer; // Đáp án client gửi (nghĩa, chỉ số, hoặc từ ghép)
    String questionType; // // "TRUE_FALSE", "MULTIPLE_CHOICE", "WORD_ASSEMBLY"
}

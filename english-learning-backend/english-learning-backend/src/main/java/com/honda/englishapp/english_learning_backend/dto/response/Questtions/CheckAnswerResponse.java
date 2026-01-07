package com.honda.englishapp.english_learning_backend.dto.response.Questtions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckAnswerResponse {
    boolean isCorrect;  // trả về true hoặc false của câu trả lời. nếu trả lời đúng sẽ chỉ hiện isCorrect
    String correctAnswer; // đáp án đúng
    String userAnswer; // đáp án sai mà người dùng chọn
}

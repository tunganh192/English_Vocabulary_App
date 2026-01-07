package com.honda.englishapp.english_learning_backend.dto.response.Statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TotalWordsLearnedResponse {
    String userId;
    String type; // DAY, WEEK, MONTH, YEAR
    Long totalWords; // Tổng số từ học được
    String period; // Thông tin bổ sung: ngày, tuần, tháng, năm (ví dụ: "2025-05-05", "2025 Week 19", "May 2025", "2025")
    //thông tin về khoảng thời gian mà dữ liệu này áp dụng
}

package com.honda.englishapp.english_learning_backend.dto.response.Statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WordsLearnedStatsResponse {
    String userId;
    String type; // DAY_IN_WEEK, WEEK_IN_MONTH, MONTH_IN_YEAR
    String period; // Ví dụ: "2025 Week 19", "May 2025", "2025"
    List<StatDetail> details;

    @Getter
    @Setter
    public static class StatDetail {
        private String timeUnit; // Tên ngày (Monday), số tuần (1), hoặc số tháng (1)
        private Long wordCount; // Số từ học được
    }
}

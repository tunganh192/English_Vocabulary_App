package com.honda.englishapp.english_learning_backend.dto.request.Statistics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WordsLearnedInWeekRequest {
    @NotBlank(message = "USER_ID_REQUIRED")
    String userId;

    @NotNull(message = "DATE_REQUIRED")
    LocalDate date;
}

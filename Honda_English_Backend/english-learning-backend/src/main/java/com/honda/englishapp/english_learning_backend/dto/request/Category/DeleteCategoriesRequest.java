package com.honda.englishapp.english_learning_backend.dto.request.Category;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteCategoriesRequest {
    @NotEmpty(message = "CATEGORY_ID_REQUIRED")
    List<Long> ids;
}

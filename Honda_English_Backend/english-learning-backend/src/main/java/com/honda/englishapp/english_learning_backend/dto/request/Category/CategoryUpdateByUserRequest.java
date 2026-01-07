package com.honda.englishapp.english_learning_backend.dto.request.Category;

import com.honda.englishapp.english_learning_backend.validator.NotEmptyPatchRequest;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NotEmptyPatchRequest
public class CategoryUpdateByUserRequest {
    @Size(min = 1, max = 100, message = "CATEGORY_NAME_LENGTH_INVALID")
    String name;

    Boolean isActive;
}

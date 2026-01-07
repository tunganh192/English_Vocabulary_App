package com.honda.englishapp.english_learning_backend.dto.request.Category;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryUpdateByAdminRequest {

    @Size(min = 1, max = 100, message = "CATEGORY_NAME_LENGTH_INVALID")
    String name;

    @Size(min = 1, max = 255, message = "ICON_URL_LENGTH_INVALID")
    String iconUrl;

    @Positive(message = "PARENT_ID_MUST_BE_POSITIVE")
    Long parentId;

    Boolean isActive;
}

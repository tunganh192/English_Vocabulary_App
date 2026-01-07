package com.honda.englishapp.english_learning_backend.dto.request.Category;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCreationRequest {

    @NotBlank(message = "CATEGORY_NAME_REQUIRED")
    @Size(min = 1 ,max = 100, message = "CATEGORY_NAME_LENGTH_INVALID")
    String name;

    @Size(min = 1, max = 255, message = "ICON_URL_LENGTH_INVALID")
    String iconUrl;

    @Positive(message = "PARENT_ID_MUST_BE_POSITIVE")
    @Nullable
    Long parentId;

    //@NotBlank(message = "CATEGORY_CODE_REQUIRED")
    @Nullable
    @Size(min = 5, max = 50, message = "CATEGORY_CODE_LENGTH_INVALID")
    String code;

    @NotBlank(message = "CREATED_BY_REQUIRED")
    @Size(min = 1, max = 100, message = "CREATED_BY_LENGTH_INVALID")
    String createdBy;
}

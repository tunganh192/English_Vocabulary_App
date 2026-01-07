package com.honda.englishapp.english_learning_backend.dto.response;

import com.honda.englishapp.english_learning_backend.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {

    Long id;

    String name;

    String iconUrl;

    Long parentId;

    String code;

    Date createdAt;

    String createdBy;

}

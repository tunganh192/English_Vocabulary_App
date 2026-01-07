package com.honda.englishapp.english_learning_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
    int pageNo;
    int pageSize;
    int totalPages;
    long totalElements;
    T items;
}

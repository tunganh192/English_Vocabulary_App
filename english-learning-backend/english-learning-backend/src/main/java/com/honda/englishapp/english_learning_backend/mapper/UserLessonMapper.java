package com.honda.englishapp.english_learning_backend.mapper;

import com.honda.englishapp.english_learning_backend.dto.request.User.UserCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.UserLesson.UserLessonCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.response.UserLessonResponse;
import com.honda.englishapp.english_learning_backend.dto.response.UserResponse;
import com.honda.englishapp.english_learning_backend.entity.UserLesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserLessonMapper {
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "categoryId", target = "category.id")
    UserLesson toUserLesson(UserLessonCreationRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "category.id", target = "categoryId")
    UserLessonResponse toUserLessonResponse(UserLesson userLesson);
}

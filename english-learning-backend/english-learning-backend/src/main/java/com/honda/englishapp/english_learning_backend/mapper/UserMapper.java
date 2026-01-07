package com.honda.englishapp.english_learning_backend.mapper;

import com.honda.englishapp.english_learning_backend.dto.request.User.UserCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.User.UserUpdateRequest;
import com.honda.englishapp.english_learning_backend.dto.response.UserResponse;
import com.honda.englishapp.english_learning_backend.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    //@Mapping(target = "role.", source = "role")
    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)// để MapStruct tự bỏ qua các trường null.
    void patchUser(@MappingTarget User user, UserUpdateRequest request);

}

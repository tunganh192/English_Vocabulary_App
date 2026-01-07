package com.honda.englishapp.english_learning_backend.mapper;

import com.honda.englishapp.english_learning_backend.dto.request.Category.CategoryCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.Category.CategoryUpdateByUserRequest;
import com.honda.englishapp.english_learning_backend.dto.response.CategoryResponse;
import com.honda.englishapp.english_learning_backend.entity.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "createdBy", ignore = true)
    Category toCategory(CategoryCreationRequest request);

    @Mapping(target = "createdBy", source = "createdBy.id")
    CategoryResponse toCategoryResponse(Category category);

    @Mapping(target = "id", ignore = true)
    void updateCategory(@MappingTarget Category category, CategoryUpdateByUserRequest request);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)// để MapStruct tự bỏ qua các trường null.
    void patchCategory(@MappingTarget Category category, CategoryUpdateByUserRequest request);

}

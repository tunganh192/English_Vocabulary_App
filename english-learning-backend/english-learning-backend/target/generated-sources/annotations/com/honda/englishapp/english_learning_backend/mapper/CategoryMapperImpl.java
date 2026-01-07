package com.honda.englishapp.english_learning_backend.mapper;

import com.honda.englishapp.english_learning_backend.dto.request.Category.CategoryCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.Category.CategoryUpdateByUserRequest;
import com.honda.englishapp.english_learning_backend.dto.response.CategoryResponse;
import com.honda.englishapp.english_learning_backend.entity.Category;
import com.honda.englishapp.english_learning_backend.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-27T10:27:30+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public Category toCategory(CategoryCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        Category.CategoryBuilder category = Category.builder();

        category.name( request.getName() );
        category.iconUrl( request.getIconUrl() );
        category.parentId( request.getParentId() );
        category.code( request.getCode() );

        return category.build();
    }

    @Override
    public CategoryResponse toCategoryResponse(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryResponse.CategoryResponseBuilder categoryResponse = CategoryResponse.builder();

        categoryResponse.createdBy( categoryCreatedById( category ) );
        categoryResponse.id( category.getId() );
        categoryResponse.name( category.getName() );
        categoryResponse.iconUrl( category.getIconUrl() );
        categoryResponse.parentId( category.getParentId() );
        categoryResponse.code( category.getCode() );
        categoryResponse.createdAt( category.getCreatedAt() );

        return categoryResponse.build();
    }

    @Override
    public void updateCategory(Category category, CategoryUpdateByUserRequest request) {
        if ( request == null ) {
            return;
        }

        category.setName( request.getName() );
    }

    @Override
    public void patchCategory(Category category, CategoryUpdateByUserRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.getName() != null ) {
            category.setName( request.getName() );
        }
    }

    private String categoryCreatedById(Category category) {
        if ( category == null ) {
            return null;
        }
        User createdBy = category.getCreatedBy();
        if ( createdBy == null ) {
            return null;
        }
        String id = createdBy.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}

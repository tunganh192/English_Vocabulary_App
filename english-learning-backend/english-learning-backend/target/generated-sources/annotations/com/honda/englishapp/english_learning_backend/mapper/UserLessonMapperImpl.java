package com.honda.englishapp.english_learning_backend.mapper;

import com.honda.englishapp.english_learning_backend.dto.request.UserLesson.UserLessonCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.response.UserLessonResponse;
import com.honda.englishapp.english_learning_backend.entity.Category;
import com.honda.englishapp.english_learning_backend.entity.User;
import com.honda.englishapp.english_learning_backend.entity.UserLesson;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-27T10:27:30+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class UserLessonMapperImpl implements UserLessonMapper {

    @Override
    public UserLesson toUserLesson(UserLessonCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        UserLesson.UserLessonBuilder userLesson = UserLesson.builder();

        userLesson.user( userLessonCreationRequestToUser( request ) );
        userLesson.category( userLessonCreationRequestToCategory( request ) );

        return userLesson.build();
    }

    @Override
    public UserLessonResponse toUserLessonResponse(UserLesson userLesson) {
        if ( userLesson == null ) {
            return null;
        }

        UserLessonResponse.UserLessonResponseBuilder userLessonResponse = UserLessonResponse.builder();

        userLessonResponse.userId( userLessonUserId( userLesson ) );
        userLessonResponse.categoryId( userLessonCategoryId( userLesson ) );
        userLessonResponse.id( userLesson.getId() );
        userLessonResponse.joinedAt( userLesson.getJoinedAt() );

        return userLessonResponse.build();
    }

    protected User userLessonCreationRequestToUser(UserLessonCreationRequest userLessonCreationRequest) {
        if ( userLessonCreationRequest == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( userLessonCreationRequest.getUserId() );

        return user.build();
    }

    protected Category userLessonCreationRequestToCategory(UserLessonCreationRequest userLessonCreationRequest) {
        if ( userLessonCreationRequest == null ) {
            return null;
        }

        Category.CategoryBuilder category = Category.builder();

        category.id( userLessonCreationRequest.getCategoryId() );

        return category.build();
    }

    private String userLessonUserId(UserLesson userLesson) {
        if ( userLesson == null ) {
            return null;
        }
        User user = userLesson.getUser();
        if ( user == null ) {
            return null;
        }
        String id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long userLessonCategoryId(UserLesson userLesson) {
        if ( userLesson == null ) {
            return null;
        }
        Category category = userLesson.getCategory();
        if ( category == null ) {
            return null;
        }
        Long id = category.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}

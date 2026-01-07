package com.honda.englishapp.english_learning_backend.mapper;

import com.honda.englishapp.english_learning_backend.dto.request.User.UserCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.User.UserUpdateRequest;
import com.honda.englishapp.english_learning_backend.dto.response.UserResponse;
import com.honda.englishapp.english_learning_backend.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-27T10:27:30+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.username( request.getUsername() );
        user.password( request.getPassword() );
        user.displayName( request.getDisplayName() );
        if ( request.getRole() != null ) {
            user.role( request.getRole().name() );
        }
        user.dailyGoal( request.getDailyGoal() );
        user.dateOfBirth( request.getDateOfBirth() );

        return user.build();
    }

    @Override
    public UserResponse toUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.id( user.getId() );
        userResponse.displayName( user.getDisplayName() );
        userResponse.dailyGoal( user.getDailyGoal() );
        userResponse.dateOfBirth( user.getDateOfBirth() );
        userResponse.role( user.getRole() );

        return userResponse.build();
    }

    @Override
    public void updateUser(User user, UserUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        user.setPassword( request.getPassword() );
        user.setDisplayName( request.getDisplayName() );
        user.setDailyGoal( request.getDailyGoal() );
        user.setDateOfBirth( request.getDateOfBirth() );
    }

    @Override
    public void patchUser(User user, UserUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.getPassword() != null ) {
            user.setPassword( request.getPassword() );
        }
        if ( request.getDisplayName() != null ) {
            user.setDisplayName( request.getDisplayName() );
        }
        if ( request.getDailyGoal() != null ) {
            user.setDailyGoal( request.getDailyGoal() );
        }
        if ( request.getDateOfBirth() != null ) {
            user.setDateOfBirth( request.getDateOfBirth() );
        }
    }
}

package com.honda.englishapp.english_learning_backend.mapper;

import com.honda.englishapp.english_learning_backend.dto.request.Reminder.ReminderCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.Reminder.ReminderUpdateRequest;
import com.honda.englishapp.english_learning_backend.dto.response.ReminderResponse;
import com.honda.englishapp.english_learning_backend.entity.Reminder;
import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-27T10:27:30+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class ReminderMapperImpl implements ReminderMapper {

    @Override
    public Reminder toReminder(ReminderCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        Reminder.ReminderBuilder reminder = Reminder.builder();

        reminder.time( request.getTime() );
        reminder.repeatType( request.getRepeatType() );
        reminder.repeatInterval( request.getRepeatInterval() );

        return reminder.build();
    }

    @Override
    public ReminderResponse toReminderResponse(Reminder reminder) {
        if ( reminder == null ) {
            return null;
        }

        ReminderResponse.ReminderResponseBuilder reminderResponse = ReminderResponse.builder();

        if ( reminder.getId() != null ) {
            reminderResponse.id( String.valueOf( reminder.getId() ) );
        }
        if ( reminder.getTime() != null ) {
            reminderResponse.time( DateTimeFormatter.ISO_LOCAL_TIME.format( reminder.getTime() ) );
        }
        reminderResponse.repeatType( reminder.getRepeatType() );
        if ( reminder.getRepeatInterval() != null ) {
            reminderResponse.repeatInterval( DateTimeFormatter.ISO_LOCAL_TIME.format( reminder.getRepeatInterval() ) );
        }
        reminderResponse.isEnabled( reminder.getIsEnabled() );

        return reminderResponse.build();
    }

    @Override
    public void patchUser(Reminder reminder, ReminderUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.getTime() != null ) {
            reminder.setTime( request.getTime() );
        }
        if ( request.getRepeatType() != null ) {
            reminder.setRepeatType( request.getRepeatType() );
        }
        if ( request.getRepeatInterval() != null ) {
            reminder.setRepeatInterval( request.getRepeatInterval() );
        }
        if ( request.getIsEnabled() != null ) {
            reminder.setIsEnabled( request.getIsEnabled() );
        }
    }
}

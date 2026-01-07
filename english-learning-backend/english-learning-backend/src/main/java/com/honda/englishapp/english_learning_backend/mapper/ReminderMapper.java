package com.honda.englishapp.english_learning_backend.mapper;

import com.honda.englishapp.english_learning_backend.dto.request.Reminder.ReminderCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.Reminder.ReminderUpdateRequest;
import com.honda.englishapp.english_learning_backend.dto.response.ReminderResponse;
import com.honda.englishapp.english_learning_backend.entity.Reminder;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReminderMapper {

    @Mapping(target = "user", ignore = true)
    Reminder toReminder(ReminderCreationRequest request);

    ReminderResponse toReminderResponse(Reminder reminder);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)// để MapStruct tự bỏ qua các trường null.
    void patchUser(@MappingTarget Reminder reminder, ReminderUpdateRequest request);
}

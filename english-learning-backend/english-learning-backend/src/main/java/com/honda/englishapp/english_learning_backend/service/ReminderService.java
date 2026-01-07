package com.honda.englishapp.english_learning_backend.service;

import com.honda.englishapp.english_learning_backend.dto.request.Reminder.ReminderCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.Reminder.ReminderUpdateRequest;
import com.honda.englishapp.english_learning_backend.dto.response.PageResponse;
import com.honda.englishapp.english_learning_backend.dto.response.ReminderResponse;
import com.honda.englishapp.english_learning_backend.entity.Reminder;
import com.honda.englishapp.english_learning_backend.enums.RepeatType;
import com.honda.englishapp.english_learning_backend.exception.AppException;
import com.honda.englishapp.english_learning_backend.exception.ErrorCode;
import com.honda.englishapp.english_learning_backend.mapper.ReminderMapper;
import com.honda.englishapp.english_learning_backend.repository.ReminderRepository;
import com.honda.englishapp.english_learning_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ReminderService {

    ReminderRepository reminderRepository;
    ReminderMapper reminderMapper;
    UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN') or (hasAnyRole('USER', 'TEACHER') and #request.userId == authentication.name)")
    public ReminderResponse create(ReminderCreationRequest request) {
        if (reminderRepository.existsByUserId(request.getUserId()))
            throw new AppException(ErrorCode.REMINDER_EXISTED);
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Reminder reminder = reminderMapper.toReminder(request);
        reminder.setUser(user);

        return reminderMapper.toReminderResponse(reminderRepository.save(reminder));
    }

    @PreAuthorize("hasRole('ADMIN') or (hasAnyRole('USER', 'TEACHER') and #userId == authentication.name)")
    public ReminderResponse getReminderByUserId(String userId) {
        Reminder reminder = reminderRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.REMINDER_NOT_EXISTED));
        return reminderMapper.toReminderResponse(reminder);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<List<ReminderResponse>> getReminders(int pageNo, int pageSize) {
        Page<Reminder> reminders = reminderRepository.findAll(PageRequest.of(pageNo - 1, pageSize));
        List<ReminderResponse> items = reminders.stream()
                .map(reminderMapper::toReminderResponse)
                .toList();

        return PageResponse.<List<ReminderResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(reminders.getTotalPages())
                .totalElements(reminders.getTotalElements())
                .items(items)
                .build();
    }

    public ReminderResponse updateReminder(Long id, ReminderUpdateRequest request) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REMINDER_NOT_EXISTED));

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean isOwner = reminder.getUser().getId().equals(currentUser);

        if (!isAdmin() && !isOwner) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        reminderMapper.patchUser(reminder, request);

        return reminderMapper.toReminderResponse(reminderRepository.save(reminder));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteReminder(Long id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REMINDER_NOT_EXISTED));

        reminder.getUser().setReminder(null);

        reminderRepository.delete(reminder);
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}

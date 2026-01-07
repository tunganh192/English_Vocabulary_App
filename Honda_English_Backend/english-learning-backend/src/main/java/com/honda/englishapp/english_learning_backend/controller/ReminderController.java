package com.honda.englishapp.english_learning_backend.controller;

import com.honda.englishapp.english_learning_backend.dto.request.Reminder.ReminderCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.Reminder.ReminderUpdateRequest;
import com.honda.englishapp.english_learning_backend.dto.response.ApiResponse;
import com.honda.englishapp.english_learning_backend.dto.response.PageResponse;
import com.honda.englishapp.english_learning_backend.dto.response.ReminderResponse;
import com.honda.englishapp.english_learning_backend.service.ReminderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("reminder")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ReminderController {

    ReminderService reminderService;

    @PostMapping
    public ApiResponse<ReminderResponse> createReminder(@RequestBody @Valid ReminderCreationRequest request) {
        return ApiResponse.<ReminderResponse>builder()
                .result(reminderService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<List<ReminderResponse>>> getReminders(@RequestParam(defaultValue = "1") int pageNo,
                                                                   @RequestParam(defaultValue = "10") int pageSize) {

        PageResponse<List<ReminderResponse>> result = reminderService.getReminders(pageNo, pageSize);

        return ApiResponse.<PageResponse<List<ReminderResponse>>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<ReminderResponse> getReminderByUserId(@PathVariable String userId) {
        return ApiResponse.<ReminderResponse>builder()
                .result(reminderService.getReminderByUserId(userId))
                .build();
    }

    @PatchMapping("/{id}")
    public ApiResponse<ReminderResponse> updateReminder(@PathVariable @Valid Long id,
                                                @RequestBody ReminderUpdateRequest request) {
        return ApiResponse.<ReminderResponse>builder()
                .result(reminderService.updateReminder(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteReminder(@PathVariable Long id) {
        reminderService.deleteReminder(id);
        return ApiResponse.builder()
                .message("Reminder deleted successfully")
                .build();
    }
}

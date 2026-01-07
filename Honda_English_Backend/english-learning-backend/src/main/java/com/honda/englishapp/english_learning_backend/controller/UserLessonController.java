package com.honda.englishapp.english_learning_backend.controller;

import com.honda.englishapp.english_learning_backend.dto.request.UserLesson.UserLessonCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.response.ApiResponse;
import com.honda.englishapp.english_learning_backend.dto.response.CategoryResponse;
import com.honda.englishapp.english_learning_backend.dto.response.PageResponse;
import com.honda.englishapp.english_learning_backend.dto.response.UserLessonResponse;
import com.honda.englishapp.english_learning_backend.service.UserLessonService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user-lesson")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserLessonController {

    UserLessonService userLessonService;

    @PostMapping
    public ApiResponse<UserLessonResponse> create(@RequestBody @Valid UserLessonCreationRequest request) {
        return ApiResponse.<UserLessonResponse>builder()
                .result(userLessonService.create(request))
                .build();
    }

    @GetMapping("/all/{userId}")
    public ApiResponse<PageResponse<List<UserLessonResponse>>> getAllByUserId(@PathVariable String userId,
                                                                                         @RequestParam(defaultValue = "1") int pageNo,
                                                                                         @RequestParam(defaultValue = "10") int pageSize) {

        PageResponse<List<UserLessonResponse>> result = userLessonService.getUserLessonsByUserId(userId,pageNo, pageSize);
        return ApiResponse.<PageResponse<List<UserLessonResponse>>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/{userId}/{categoryId}")
    public ApiResponse<UserLessonResponse> getByUserIdAndCategoryId(@PathVariable String userId, @PathVariable Long categoryId) {

        return ApiResponse.<UserLessonResponse>builder()
                .result(userLessonService.getByUserIdAndCategoryId(userId, categoryId))
                .build();
    }

    @GetMapping("/joined/{userId}")
    public ApiResponse<PageResponse<List<CategoryResponse>>> getJoinedCategoriesByUserId(@PathVariable String userId,
                                                                   @RequestParam(defaultValue = "1") int pageNo,
                                                                   @RequestParam(defaultValue = "10") int pageSize) {

        PageResponse<List<CategoryResponse>> result = userLessonService.getCategoriesJoinedByUser(userId,pageNo, pageSize);
        return ApiResponse.<PageResponse<List<CategoryResponse>>>builder()
                .result(result)
                .build();
    }

    @DeleteMapping("/delete")
    public ApiResponse<String> delete(@RequestParam @Valid String userId, @RequestParam @Valid Long categoryId) {
        userLessonService.deleteByUserIdAndCategoryId(userId, categoryId);
        return ApiResponse.<String>builder()
                .result("Deleted successfully")
                .build();
    }
}
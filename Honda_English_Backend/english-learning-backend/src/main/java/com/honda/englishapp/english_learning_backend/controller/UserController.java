package com.honda.englishapp.english_learning_backend.controller;


import com.honda.englishapp.english_learning_backend.dto.request.User.UserCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.User.UserUpdateRequest;
import com.honda.englishapp.english_learning_backend.dto.response.ApiResponse;
import com.honda.englishapp.english_learning_backend.dto.response.PageResponse;
import com.honda.englishapp.english_learning_backend.dto.response.UserResponse;
import com.honda.englishapp.english_learning_backend.mapper.UserMapper;
import com.honda.englishapp.english_learning_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("user")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserController {
    UserMapper userMapper;
    UserService userService;

    @PostMapping()
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {

        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<List<UserResponse>>> getUsers(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {

        PageResponse<List<UserResponse>> result = userService.getUsers(pageNo, pageSize);
        return ApiResponse.<PageResponse<List<UserResponse>>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<List<UserResponse>>> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String displayName,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer dailyGoal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth, // DateTimeFormat.ISO.DATE = yyyy-MM-dd // Hướng dẫn Spring cách parse chuỗi thành LocalDate
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {

        PageResponse<List<UserResponse>> result = userService.searchUsers(
                username, displayName, role, dailyGoal, dateOfBirth, sortBy, pageNo, pageSize
        );

        return ApiResponse.<PageResponse<List<UserResponse>>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/id/{userId}")
    ApiResponse<UserResponse> getUserById(@PathVariable String userId) {

        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/username/{username}")
    ApiResponse<UserResponse> getUserByUserName(@PathVariable String username) {

        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserByUserName(username))
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {

        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PatchMapping("{userId}")
    ApiResponse<UserResponse> patchUser(@PathVariable String userId, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("{userId}")
    ApiResponse<?> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);

        return ApiResponse.builder()
                .message("User has delete")
                .build();
    }
}

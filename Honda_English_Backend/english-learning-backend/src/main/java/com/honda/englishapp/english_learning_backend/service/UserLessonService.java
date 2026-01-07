package com.honda.englishapp.english_learning_backend.service;

import com.honda.englishapp.english_learning_backend.dto.request.Category.CategoryCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.UserLesson.UserLessonCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.response.CategoryResponse;
import com.honda.englishapp.english_learning_backend.dto.response.PageResponse;
import com.honda.englishapp.english_learning_backend.dto.response.UserLessonResponse;
import com.honda.englishapp.english_learning_backend.entity.Category;
import com.honda.englishapp.english_learning_backend.entity.User;
import com.honda.englishapp.english_learning_backend.entity.UserLesson;
import com.honda.englishapp.english_learning_backend.enums.Role;
import com.honda.englishapp.english_learning_backend.exception.AppException;
import com.honda.englishapp.english_learning_backend.exception.ErrorCode;
import com.honda.englishapp.english_learning_backend.mapper.CategoryMapper;
import com.honda.englishapp.english_learning_backend.mapper.UserLessonMapper;
import com.honda.englishapp.english_learning_backend.repository.CategoryRepository;
import com.honda.englishapp.english_learning_backend.repository.UserLessonRepository;
import com.honda.englishapp.english_learning_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserLessonService {

    UserLessonRepository userLessonRepository;
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    UserLessonMapper userLessonMapper;
    CategoryMapper categoryMapper;

    @PreAuthorize("hasRole('ADMIN') or #request.userId == authentication.name")
    public UserLessonResponse create(UserLessonCreationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        if (userLessonRepository.existsByUserIdAndCategoryId(request.getUserId(), request.getCategoryId())) {
            throw new AppException(ErrorCode.USER_ALREADY_JOINED_CATEGORY);
        }

        UserLesson userLesson = userLessonMapper.toUserLesson(request);

        return userLessonMapper.toUserLessonResponse(userLessonRepository.save(userLesson));
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.name")
    public PageResponse<List<UserLessonResponse>> getUserLessonsByUserId(String userId, int pageNo, int pageSize) {
        Page<UserLesson> userLessons = userLessonRepository.findByUserId(userId,PageRequest.of(pageNo - 1, pageSize, Sort.by("id")));
        List<UserLessonResponse> items = userLessons.stream()
                .map(userLessonMapper::toUserLessonResponse)
                .toList();

        return PageResponse.<List<UserLessonResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(userLessons.getTotalPages())
                .totalElements(userLessons.getTotalElements())
                .items(items)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.name")
    public UserLessonResponse getByUserIdAndCategoryId(String userId, Long categoryId) {
        UserLesson userLesson = userLessonRepository
                .findByUserIdAndCategoryId(userId, categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_LESSON_NOT_EXISTED));

        return userLessonMapper.toUserLessonResponse(userLesson);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.name")
    public PageResponse<List<CategoryResponse>> getCategoriesJoinedByUser(String userId, int pageNo, int pageSize) {
        Page<Category> categories = userLessonRepository.findCategoriesByUserId(userId,PageRequest.of(pageNo - 1, pageSize, Sort.by("id")));
        List<CategoryResponse> items = categories.stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();

        return PageResponse.<List<CategoryResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(categories.getTotalPages())
                .totalElements(categories.getTotalElements())
                .items(items)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.name")
    @Transactional
    public void deleteByUserIdAndCategoryId(String userId, long categoryId) {
        userLessonRepository.deleteByUserIdAndCategoryId(userId, categoryId);
    }

}
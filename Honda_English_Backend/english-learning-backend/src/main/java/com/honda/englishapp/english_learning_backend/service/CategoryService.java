package com.honda.englishapp.english_learning_backend.service;

import com.honda.englishapp.english_learning_backend.dto.request.Category.CategoryCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.Category.CategoryUpdateByUserRequest;
import com.honda.englishapp.english_learning_backend.dto.response.CategoryResponse;
import com.honda.englishapp.english_learning_backend.dto.response.PageResponse;
import com.honda.englishapp.english_learning_backend.dto.response.UserResponse;
import com.honda.englishapp.english_learning_backend.entity.Category;
import com.honda.englishapp.english_learning_backend.entity.User;
import com.honda.englishapp.english_learning_backend.entity.UserLesson;
import com.honda.englishapp.english_learning_backend.entity.Word;
import com.honda.englishapp.english_learning_backend.enums.Role;
import com.honda.englishapp.english_learning_backend.exception.AppException;
import com.honda.englishapp.english_learning_backend.exception.ErrorCode;
import com.honda.englishapp.english_learning_backend.mapper.CategoryMapper;
import com.honda.englishapp.english_learning_backend.repository.CategoryRepository;
import com.honda.englishapp.english_learning_backend.repository.UserLessonRepository;
import com.honda.englishapp.english_learning_backend.repository.UserRepository;
import com.honda.englishapp.english_learning_backend.repository.WordRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryService {

    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    UserRepository userRepository;
    UserLessonRepository userLessonRepository;
    WordRepository wordRepository;

    @Transactional
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public CategoryResponse createCategory(CategoryCreationRequest request) {

        User user = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (request.getCode() != null && categoryRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }

        Category category = categoryMapper.toCategory(request);

        category.setCreatedBy(user);

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<List<CategoryResponse>> getAllCategories(int pageNo, int pageSize) {
        Page<Category> categories = categoryRepository.findAll(PageRequest.of(pageNo - 1, pageSize));
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

    public PageResponse<List<CategoryResponse>> getSystemGeneratedParentCategories(int pageNo, int pageSize) {
        Page<Category> categories = categoryRepository.findByParentIsNullAndCreatedByUserId(Role.ADMIN.toString(),PageRequest.of(pageNo - 1, pageSize, Sort.by("id")));
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

    public PageResponse<List<CategoryResponse>> getSystemGeneratedSubCategories(Long parentId, int pageNo, int pageSize) {
        if (!categoryRepository.existsById(parentId)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }

        Page<Category> categories = categoryRepository.findByParentIdAndIsActiveTrue(parentId, PageRequest.of(pageNo - 1, pageSize, Sort.by("id")));
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

    public PageResponse<List<CategoryResponse>> getAllSubCategoriesByAdmin(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by("id").ascending());
        Page<Category> categories = categoryRepository.findSubCategoriesCreatedByAdmin("admin", pageable);

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

    @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and #userId == authentication.name)")
    public PageResponse<List<CategoryResponse>> getCategoriesByCreator(String userId, int pageNo, int pageSize) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        Page<Category> categories = categoryRepository.findByCreatedByIdAndIsActiveTrue(userId, PageRequest.of(pageNo - 1, pageSize));
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

    public CategoryResponse getCategoryByCode(String code) {
        Category category = categoryRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        return categoryMapper.toCategoryResponse(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse getCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        return categoryMapper.toCategoryResponse(category);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public CategoryResponse updateCategory(Long categoryId, CategoryUpdateByUserRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean isOwner = category.getCreatedBy().getId().equals(currentUser);

        if (!isAdmin() && !isOwner) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        categoryMapper.patchCategory(category, request);
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        categoryRepository.delete(category);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public void deactivateCategory(Long id) {
        deactivateCategories(List.of(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public void deactivateCategories(List<Long> categoryIds) {
        List<Category> categories = categoryRepository.findAllById(categoryIds);

        if (categories.size() != categoryIds.size()) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }

        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean allCreatedByCurrentUser = categories.stream()
                .allMatch(category -> category.getCreatedBy().getId().equals(currentUserId));

        if (!allCreatedByCurrentUser && !isAdmin()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        categories.forEach(category -> category.setIsActive(false));

        // Cập nhật isActive = false cho tất cả các từ thuộc các danh mục này
        List<Word> wordsToDeactivate = wordRepository.findByCategoryIdIn(categoryIds);
        wordsToDeactivate.forEach(word -> word.setIsActive(false));

        // Lưu các danh mục và từ đã cập nhật
        categoryRepository.saveAll(categories);
        wordRepository.saveAll(wordsToDeactivate);
    }

    public PageResponse<List<CategoryResponse>> searchCategories(
            String name,
            String code,
            String sortBy,
            int pageNo,
            int pageSize) {

        List<Sort.Order> sorts = new ArrayList<>();

        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(sortBy);

            if (matcher.find()) {
                Sort.Direction direction = matcher.group(3).equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
                sorts.add(new Sort.Order(direction, matcher.group(1)));
            }
        }

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sorts));

        Page<Category> page = categoryRepository.searchLessons(name, code, pageable);

        List<CategoryResponse> items = page.stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();

        return PageResponse.<List<CategoryResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .items(items)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public PageResponse<List<UserResponse>> getUsersByCategoryId(Long categoryId, int pageNo, int pageSize) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean isOwner = category.getCreatedBy().getId().equals(currentUser);

        if (!isAdmin() && !isOwner) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Page<UserLesson> page = userLessonRepository.findByCategoryId(categoryId, PageRequest.of(pageNo - 1, pageSize, Sort.by("joinedAt")));

        List<UserResponse> userResponses = page.map(userLesson -> {
            var user = userLesson.getUser();
            return UserResponse.builder()
                    .id(user.getId())
                    .displayName(user.getDisplayName())
                    .dateOfBirth(user.getDateOfBirth())
                    .role(user.getRole())
                    .build();
        }).toList();

        return PageResponse.<List<UserResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .items(userResponses)
                .build();
    }


    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

}

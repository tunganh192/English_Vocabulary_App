package com.honda.englishapp.english_learning_backend.controller;

import com.honda.englishapp.english_learning_backend.dto.request.Category.CategoryCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.Category.CategoryUpdateByUserRequest;
import com.honda.englishapp.english_learning_backend.dto.request.Category.DeleteCategoriesRequest;
import com.honda.englishapp.english_learning_backend.dto.response.ApiResponse;
import com.honda.englishapp.english_learning_backend.dto.response.CategoryResponse;
import com.honda.englishapp.english_learning_backend.dto.response.PageResponse;
import com.honda.englishapp.english_learning_backend.dto.response.UserResponse;
import com.honda.englishapp.english_learning_backend.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryController {

    CategoryService categoryService;

    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryCreationRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.createCategory(request))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<List<CategoryResponse>>> getAllCategories(@RequestParam(defaultValue = "1") int pageNo,
                                                                          @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<List<CategoryResponse>> result = categoryService.getAllCategories(pageNo, pageSize);
        return ApiResponse.<PageResponse<List<CategoryResponse>>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/system-generated/parents")
    public ApiResponse<PageResponse<List<CategoryResponse>>> getSystemGeneratedParentCategories( @RequestParam(defaultValue = "1") int pageNo,
                                                                                  @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<List<CategoryResponse>> result = categoryService.getSystemGeneratedParentCategories(pageNo, pageSize);
        return ApiResponse.<PageResponse<List<CategoryResponse>>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/system-generated/subcategories/{parentId}")
    ApiResponse<PageResponse<List<CategoryResponse>>> getSystemGeneratedSubCategories(
            @PathVariable Long parentId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<List<CategoryResponse>> result = categoryService.getSystemGeneratedSubCategories(parentId, pageNo, pageSize);
        return ApiResponse.<PageResponse<List<CategoryResponse>>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/system-generated/subcategories")
    public ApiResponse<PageResponse<List<CategoryResponse>>> getAllSubCategoriesByAdmin(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {

        PageResponse<List<CategoryResponse>> result = categoryService.getAllSubCategoriesByAdmin(pageNo, pageSize);
        return ApiResponse.<PageResponse<List<CategoryResponse>>>builder()
                .result(result)
                .build();
    }


    @GetMapping("/created-by/{userId}")
    ApiResponse<PageResponse<List<CategoryResponse>>> getCategoriesByCreator(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<List<CategoryResponse>> result = categoryService.getCategoriesByCreator(userId, pageNo, pageSize);
        return ApiResponse.<PageResponse<List<CategoryResponse>>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/by-code/{code}")
    ApiResponse<CategoryResponse> getCategoryByCode(@PathVariable String code) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.getCategoryByCode(code))
                .build();
    }


    @GetMapping("{categoryId}")
    public ApiResponse<CategoryResponse> getCategory(@PathVariable Long categoryId) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.getCategory(categoryId))
                .build();
    }

    @GetMapping("/search")
    public PageResponse<List<CategoryResponse>> searchCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String sortBy
    ) {
        return categoryService.searchCategories(name, code, sortBy, pageNo, pageSize);
    }


    @PatchMapping("{categoryId}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable @Min(value = 1) Long categoryId,
                                                        @RequestBody @Valid CategoryUpdateByUserRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.updateCategory(categoryId, request))
                .build();
    }

    @DeleteMapping("{categoryId}")
    public ApiResponse<?> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.builder()
                .message("Category has been deleted")
                .build();
    }

    @PostMapping("/deactivate/{id}")
    public ApiResponse<?> deactivateCategory(@PathVariable Long id) {
        categoryService.deactivateCategory(id);
        return ApiResponse.builder()
                .message("Deleted successfully")
                .build();
    }

    @PostMapping("/deactivate-multiple")
    public ApiResponse<?> deactivateCategories(@Valid @RequestBody DeleteCategoriesRequest categoryId) {
        categoryService.deactivateCategories(categoryId.getIds());
        return ApiResponse.builder()
                .message("Deactivate successfully")
                .build();
    }

    @GetMapping("/{categoryId}/users")
    public ApiResponse<PageResponse<List<UserResponse>>> getUsersByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {

        PageResponse<List<UserResponse>> result = categoryService.getUsersByCategoryId(categoryId, pageNo, pageSize);

        return ApiResponse.<PageResponse<List<UserResponse>>>builder()
                .result(result)
                .build();
    }


}

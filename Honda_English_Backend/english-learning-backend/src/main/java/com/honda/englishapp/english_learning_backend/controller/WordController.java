package com.honda.englishapp.english_learning_backend.controller;

import com.honda.englishapp.english_learning_backend.dto.request.Word.DeleteWordsRequest;
import com.honda.englishapp.english_learning_backend.dto.request.Word.WordCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.Word.WordUpdateRequest;
import com.honda.englishapp.english_learning_backend.dto.response.ApiResponse;
import com.honda.englishapp.english_learning_backend.dto.response.PageResponse;
import com.honda.englishapp.english_learning_backend.dto.response.Statistics.WordCountResponse;
import com.honda.englishapp.english_learning_backend.dto.response.WordResponse;
import com.honda.englishapp.english_learning_backend.service.WordService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("word")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WordController {
    WordService wordService;

    @PostMapping
    public ApiResponse<WordResponse> createWord(@RequestBody @Valid WordCreationRequest request) {
        return ApiResponse.<WordResponse>builder()
                .result(wordService.createWord(request))
                .build();
    }

    @GetMapping("{id}")
    public ApiResponse<WordResponse> getWord(@PathVariable Long id) {
        return ApiResponse.<WordResponse>builder()
                .result(wordService.getActiveWord(id))
                .build();
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<PageResponse<List<WordResponse>>> getWordsByCategory(@PathVariable Long categoryId,
                                                              @RequestParam(defaultValue = "1") int pageNo,
                                                              @RequestParam(defaultValue = "10") int pageSize) {

        PageResponse<List<WordResponse>> result = wordService.getActiveWordsByCategory(categoryId, pageNo, pageSize);

        return ApiResponse.<PageResponse<List<WordResponse>>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/user/category/{categoryId}")
    public ApiResponse<PageResponse<List<WordResponse>>> getActiveWordsByCategory(@PathVariable Long categoryId,
                                                                            @RequestParam(defaultValue = "1") int pageNo,
                                                                            @RequestParam(defaultValue = "10") int pageSize) {

        PageResponse<List<WordResponse>> result = wordService.getActiveWordsBySubCategory(categoryId, pageNo, pageSize);

        return ApiResponse.<PageResponse<List<WordResponse>>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/words")
    ApiResponse<PageResponse<List<WordResponse>>> getActiveWords(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<List<WordResponse>> result = wordService.getActiveWords(pageNo, pageSize);
        return ApiResponse.<PageResponse<List<WordResponse>>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/learned/{userId}")
    ApiResponse<PageResponse<List<WordResponse>>> getActiveLearnedWordsMaster(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<List<WordResponse>> result = wordService.getActiveLearnedWordsByUserId(userId, pageNo, pageSize);
        return ApiResponse.<PageResponse<List<WordResponse>>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/count/category/{categoryId}")
    ApiResponse<WordCountResponse> countActiveWordsByCategoryId(
            @PathVariable Long categoryId) {
        return ApiResponse.<WordCountResponse>builder()
                .result(wordService.countActiveWordsByCategoryId(categoryId))
                .build();
    }

    @GetMapping("/count/system-parent-category/{categoryId}")
    ApiResponse<WordCountResponse> countActiveWordsBySystemGeneratedParentCategory(
            @PathVariable Long categoryId) {
        return ApiResponse.<WordCountResponse>builder()
                .result(wordService.countActiveWordsByParentCategory(categoryId))
                .build();
    }

    @GetMapping("/count/system-parent-categories")
    ApiResponse<List<WordCountResponse>> countActiveWordsForAllSystemGeneratedParentCategories() {
        return ApiResponse.<List<WordCountResponse>>builder()
                .result(wordService.countActiveWordsForAllSystemGeneratedParentCategories())
                .build();
    }

    @GetMapping("/search")
    public PageResponse<List<WordResponse>> searchWords(
            @RequestParam(required = false) String englishWord,
            @RequestParam(required = false) String vietnameseMeaning,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String sortBy
    ) {
        return wordService.searchWords(englishWord, vietnameseMeaning, isActive, categoryId, sortBy, pageNo, pageSize);
    }


    @PatchMapping("{id}")
    public ApiResponse<WordResponse> updateWord(@PathVariable @Valid Long id, @RequestBody WordUpdateRequest request) {
        return ApiResponse.<WordResponse>builder()
                .result(wordService.updateWord(id, request))
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<?> deleteWords(@PathVariable Long id) {
        wordService.deleteWord(id);
        return ApiResponse.builder()
                .message("Word has been deleted")
                .build();
    }

    @DeleteMapping("/deactivate/{id}")
    public ApiResponse<?> deactivateWord(@PathVariable Long id) {
        wordService.deleteWord(id);
        return ApiResponse.builder()
                .message("Word has been deleted")
                .build();
    }

    @PostMapping("/deactivate/{id}")
    public ApiResponse<?> deactivateMultipleWords(@PathVariable Long id) {
        wordService.deactivateWord(id);
        return ApiResponse.builder()
                .message("Deleted successfully")
                .build();
    }

    @PostMapping("/deactivate-multiple")
    public ApiResponse<?> deactivateMultipleWords(@Valid @RequestBody DeleteWordsRequest request) {
        wordService.deactivateWords(request.getIds());
        return ApiResponse.builder()
                .message("Deleted successfully")
                .build();
    }
}

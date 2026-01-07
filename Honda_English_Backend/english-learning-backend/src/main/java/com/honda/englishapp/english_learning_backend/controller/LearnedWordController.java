package com.honda.englishapp.english_learning_backend.controller;

import com.honda.englishapp.english_learning_backend.dto.request.LearnedWord.LearnedWordCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.LearnedWord.LearnedWordUpdateRequest;
import com.honda.englishapp.english_learning_backend.dto.response.ApiResponse;
import com.honda.englishapp.english_learning_backend.dto.response.LearnedWordResponse;
import com.honda.englishapp.english_learning_backend.dto.response.PageResponse;
import com.honda.englishapp.english_learning_backend.dto.response.Statistics.LearnedWordPercentageResponse;
import com.honda.englishapp.english_learning_backend.service.LearnedWordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("learned-words")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LearnedWordController {

    LearnedWordService learnedWordService;

    @PostMapping
    public ApiResponse<LearnedWordResponse> create(@RequestBody @Valid LearnedWordCreationRequest request) {
        return ApiResponse.<LearnedWordResponse>builder()
                .result(learnedWordService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<List<LearnedWordResponse>>> getLearnedWords(@RequestParam(defaultValue = "1") int pageNo,
                                                         @RequestParam(defaultValue = "10") int pageSize) {

        PageResponse<List<LearnedWordResponse>> result = learnedWordService.getAll(pageNo, pageSize);
        return ApiResponse.<PageResponse<List<LearnedWordResponse>>>builder()
                .result(result)
                .build();
    }

    @GetMapping("{id}")
    public ApiResponse<LearnedWordResponse> getLearnedWordById(@PathVariable Long id) {
        return ApiResponse.<LearnedWordResponse>builder()
                .result(learnedWordService.getById(id))
                .build();
    }

    @GetMapping("/search")
    public PageResponse<List<LearnedWordResponse>> searchLearnedWords(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Long wordId,
            @RequestParam(required = false) Boolean isMastered,
            @RequestParam(required = false) Integer correctStreak,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateLearned,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String sortBy
    ) {
        return learnedWordService.searchLearnedWords(
                userId, wordId, isMastered, correctStreak, dateLearned, sortBy, pageNo, pageSize
        );
    }

    @GetMapping("/percentage/by-category/{categoryId}/{userId}")
    ApiResponse<LearnedWordPercentageResponse> calculateLearnedWordPercentageByCategory(
            @PathVariable Long categoryId,
            @PathVariable String userId) {
        return ApiResponse.<LearnedWordPercentageResponse>builder()
                .result(learnedWordService.calculateLearnedWordPercentageByCategory(userId, categoryId))
                .build();
    }


    @PatchMapping("{id}")
    public ApiResponse<LearnedWordResponse> updateLearnedWord(@PathVariable @Valid Long id,
                                                   @RequestBody LearnedWordUpdateRequest request) {
        return ApiResponse.<LearnedWordResponse>builder()
                .result(learnedWordService.update(id, request))
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<?> deleteLearnedWord(@PathVariable Long id) {
        learnedWordService.delete(id);
        return ApiResponse.builder()
                .message("Deleted successfully")
                .build();
    }
}

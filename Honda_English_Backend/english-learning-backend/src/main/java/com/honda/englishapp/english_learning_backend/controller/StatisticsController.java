package com.honda.englishapp.english_learning_backend.controller;

import com.honda.englishapp.english_learning_backend.dto.response.ApiResponse;
import com.honda.englishapp.english_learning_backend.dto.response.PageResponse;
import com.honda.englishapp.english_learning_backend.dto.response.Statistics.LearnedWordAccuracyResponse;
import com.honda.englishapp.english_learning_backend.dto.response.Statistics.TotalWordsLearnedResponse;
import com.honda.englishapp.english_learning_backend.dto.response.Statistics.WordsLearnedStatsResponse;
import com.honda.englishapp.english_learning_backend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("statistics")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class StatisticsController {
    StatisticsService statisticsService;

    @GetMapping("/learning-words/{userId}")
    public ApiResponse<TotalWordsLearnedResponse> getTotalWordsLearning(@PathVariable String userId) {

        return ApiResponse.<TotalWordsLearnedResponse>builder()
                .result(statisticsService.getTotalWordsLearning(userId))
                .build();
    }

    @GetMapping("/learned-words/{userId}")
    public ApiResponse<TotalWordsLearnedResponse> getTotalWordsLearned(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String type) {
        return ApiResponse.<TotalWordsLearnedResponse>builder()
                .result(statisticsService.getTotalWordsLearned(userId, date, type))
                .build();
    }

    @GetMapping("/learned-stats/{userId}")
    public ApiResponse<WordsLearnedStatsResponse> getWordLearningStats(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String type) {
        return ApiResponse.<WordsLearnedStatsResponse>builder()
                .result(statisticsService.getWordLearningStats(userId, date, type))
                .build();
    }

    @GetMapping("/learned-word-accuracy/{userId}/{categoryId}")
    public ApiResponse<LearnedWordAccuracyResponse> getLearnedWordAccuracy(
            @PathVariable String userId,
            @PathVariable Long categoryId) {
        return ApiResponse.<LearnedWordAccuracyResponse>builder()
                .result(statisticsService.getLearnedWordAccuracy(userId, categoryId))
                .build();
    }

    @GetMapping("/category/{categoryId}/accuracy")
    public ApiResponse<PageResponse<List<LearnedWordAccuracyResponse>>> getCategoryAccuracyStats(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.<PageResponse<List<LearnedWordAccuracyResponse>>>builder()
                .result(statisticsService.getCategoryAccuracyStats(categoryId, pageNo, pageSize))
                .build();
    }
}
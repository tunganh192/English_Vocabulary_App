package com.honda.englishapp.english_learning_backend.controller;

import com.honda.englishapp.english_learning_backend.dto.request.Question.CheckAnswerRequest;
import com.honda.englishapp.english_learning_backend.dto.response.ApiResponse;
import com.honda.englishapp.english_learning_backend.dto.response.Questtions.CheckAnswerResponse;
import com.honda.englishapp.english_learning_backend.dto.response.Questtions.MultipleChoiceQuestionResponse;
import com.honda.englishapp.english_learning_backend.dto.response.Questtions.TrueFalseQuestionResponse;
import com.honda.englishapp.english_learning_backend.dto.response.Questtions.WordAssemblyQuestionResponse;
import com.honda.englishapp.english_learning_backend.service.QuestionService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("question")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
public class QuestionController {
    QuestionService questionService;

    @GetMapping("/true-false/{categoryId}/{userId}")
    ApiResponse<TrueFalseQuestionResponse> generateTrueFalseQuestion(
            @NotNull(message = "CATEGORY_ID_REQUIRED") @PathVariable Long categoryId,
            @NotNull(message = "USER_ID_REQUIRED") @PathVariable String userId,
            @RequestParam(name = "fromMasteredWords", defaultValue = "false") boolean fromMasteredWords) {
        return ApiResponse.<TrueFalseQuestionResponse>builder()
                .result(questionService.generateTrueFalseQuestion(categoryId, userId, fromMasteredWords))
                .build();
    }

    @GetMapping("/four-options/{categoryId}/{userId}")
    ApiResponse<MultipleChoiceQuestionResponse> generateFourOptionsQuestions(
            @NotNull(message = "CATEGORY_ID_REQUIRED") @PathVariable Long categoryId,
            @NotNull(message = "USER_ID_REQUIRED") @PathVariable String userId,
            @RequestParam(name = "fromMasteredWords", defaultValue = "false") boolean fromMasteredWords) {
        return ApiResponse.<MultipleChoiceQuestionResponse>builder()
                .result(questionService.generateMultipleChoiceQuestionWith4Options(categoryId, userId, fromMasteredWords))
                .build();
    }


    @GetMapping("/six-options/{categoryId}/{userId}")
    ApiResponse<MultipleChoiceQuestionResponse> generateSixOptionsQuestion(
            @NotNull(message = "CATEGORY_ID_REQUIRED") @PathVariable Long categoryId,
            @NotNull(message = "USER_ID_REQUIRED") @PathVariable String userId,
            @RequestParam(name = "fromMasteredWords", defaultValue = "false") boolean fromMasteredWords) {
        return ApiResponse.<MultipleChoiceQuestionResponse>builder()
                .result(questionService.generateMultipleChoiceQuestionWith6Options(categoryId, userId, fromMasteredWords))
                .build();
    }

    @GetMapping("/word-assembly/{categoryId}/{userId}")
    ApiResponse<WordAssemblyQuestionResponse> generateWordAssemblyQuestion(
            @NotNull(message = "CATEGORY_ID_REQUIRED") @PathVariable Long categoryId,
            @NotNull(message = "USER_ID_REQUIRED") @PathVariable String userId,
            @RequestParam(name = "fromMasteredWords", defaultValue = "false") boolean fromMasteredWords) {
        return ApiResponse.<WordAssemblyQuestionResponse>builder()
                .result(questionService.generateWordAssemblyWithOptions(categoryId, userId, fromMasteredWords))
                .build();
    }

    @PostMapping("/check-answer")
    ApiResponse<CheckAnswerResponse> checkAnswer(
            @RequestBody CheckAnswerRequest request) {
        return ApiResponse.<CheckAnswerResponse>builder()
                .result(questionService.checkAnswer(request))
                .build();
    }


}

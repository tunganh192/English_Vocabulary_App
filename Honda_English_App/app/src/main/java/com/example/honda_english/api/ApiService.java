package com.example.honda_english.api;

import com.example.honda_english.model.ApiResponse;
import com.example.honda_english.model.Authentication.AuthenticationRequest;
import com.example.honda_english.model.Authentication.AuthenticationResponse;
import com.example.honda_english.model.Authentication.RefreshRequest;
import com.example.honda_english.model.Category.CategoryCreationRequest;
import com.example.honda_english.model.Category.CategoryResponse;
import com.example.honda_english.model.Category.DeleteCategoriesRequest;
import com.example.honda_english.model.Question.CheckAnswerRequest;
import com.example.honda_english.model.Question.CheckAnswerResponse;
import com.example.honda_english.model.LearnedWord.LearnedWordCreationRequest;
import com.example.honda_english.model.Statistic.LearnedWordAccuracyResponse;
import com.example.honda_english.model.Statistic.LearnedWordPercentageResponse;
import com.example.honda_english.model.LearnedWord.LearnedWordResponse;
import com.example.honda_english.model.LearnedWord.LearnedWordUpdateRequest;
import com.example.honda_english.model.Authentication.LogoutRequest;
import com.example.honda_english.model.Question.MultipleChoiceQuestionResponse;
import com.example.honda_english.model.PageResponse;
import com.example.honda_english.model.Reminder.ReminderCreationRequest;
import com.example.honda_english.model.Reminder.ReminderResponse;
import com.example.honda_english.model.Reminder.ReminderUpdateRequest;
import com.example.honda_english.model.Statistic.TotalWordsLearnedResponse;
import com.example.honda_english.model.Question.TrueFalseQuestionResponse;
import com.example.honda_english.model.User.UserCreationRequest;
import com.example.honda_english.model.User.UserUpdateRequest;
import com.example.honda_english.model.UserLesson.UserLessonCreationRequest;
import com.example.honda_english.model.UserLesson.UserLessonResponse;
import com.example.honda_english.model.User.UserResponse;
import com.example.honda_english.model.Question.WordAssemblyQuestionResponse;
import com.example.honda_english.model.Statistic.WordCountResponse;
import com.example.honda_english.model.Word.DeleteWordsRequest;
import com.example.honda_english.model.Word.WordCreationRequest;
import com.example.honda_english.model.Word.WordResponse;
import com.example.honda_english.model.Statistic.WordsLearnedStatsResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // Auth
    @POST("/auth/login")
    Call<ApiResponse<AuthenticationResponse>> login(@Body AuthenticationRequest request);
    @POST("/auth/logout")
    Call<ApiResponse<Void>> logout(@Body LogoutRequest request);

    @POST("/auth/refresh")
    Call<ApiResponse<AuthenticationResponse>> refresh(@Body RefreshRequest request);

    // User
    @GET("user/my-info")
    Call<ApiResponse<UserResponse>> getMyInfo();
    @POST("user")
    Call<ApiResponse<UserResponse>> createUser(@Body UserCreationRequest request);
    @GET("/user/id/{userId}")
    Call<ApiResponse<UserResponse>> getUserById(@Path("userId") String userId);
    @GET("/user/username/{username}")
    Call<ApiResponse<UserResponse>> getUserByUserName(@Path("username") String userId);

    @PATCH("/user/{userId}")
    Call<ApiResponse<UserResponse>> updateUser(@Path("userId") String userId, @Body UserUpdateRequest user);

    // Category
    @GET("/category/system-generated/parents")
    Call<ApiResponse<PageResponse<List<CategoryResponse>>>> getSystemGeneratedParentCategories(
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize
    );

    @GET("/category/created-by/{userId}")
    Call<ApiResponse<PageResponse<List<CategoryResponse>>>> getCategoriesByCreator(
            @Path("userId") String userId,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize
    );

    @GET("/category/system-generated/subcategories/{parentCategoryId}")
    Call<ApiResponse<PageResponse<List<CategoryResponse>>>> getSubcategories(
            @Path("parentCategoryId") Long parentCategoryId,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize
    );

    @GET("category/system-generated/subcategories")
    Call<ApiResponse<PageResponse<List<CategoryResponse>>>> getAllSubCategoriesByAdmin(
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize
    );
    @POST("/category")
    Call<ApiResponse<CategoryResponse>> createCategory(@Body CategoryCreationRequest category);

    @GET("category/search")
    Call<PageResponse<List<CategoryResponse>>> searchCategories(
            @Query("name") String name,
            @Query("code") String code,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize,
            @Query("sortBy") String sortBy);



    @GET("category/{categoryId}/users")
    Call<ApiResponse<PageResponse<List<UserResponse>>>> getStudentsByCategory(
            @Path("categoryId") Long categoryId,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize
    );

    @POST("category/deactivate-multiple")
    Call<ApiResponse<Void>> deactivateCategories(@Body DeleteCategoriesRequest request);

    // User-Lesson
    @POST("/user-lesson")
    Call<ApiResponse<UserLessonResponse>> createUserLesson(@Body UserLessonCreationRequest request);
    @GET("user-lesson/joined/{userId}")
    Call<ApiResponse<PageResponse<List<CategoryResponse>>>> getJoinedCategoriesByUserId(
            @Path("userId") String userId,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize
    );
    @GET("user-lesson/{userId}/{categoryId}")
    Call<ApiResponse<CategoryResponse>> getUserLessonByUserIdAndCategoryId(
            @Path("userId") String userId,
            @Path("categoryId") String categoryId
    );
    @DELETE("user-lesson/delete")
    Call<ApiResponse<String>> deleteUserLesson(
            @Query("userId") String userId,
            @Query("categoryId") Long categoryId);
    // Word
    @POST("/word")
    Call<ApiResponse<WordResponse>> createWord(@Body WordCreationRequest request);

    @GET("/word/user/category/{categoryId}")
    Call<ApiResponse<PageResponse<List<WordResponse>>>> getWordsByCategory(
            @Path("categoryId") Long categoryId,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize
    );
    @GET("/word/count/system-parent-category/{categoryId}")
    Call<ApiResponse<WordCountResponse>> getWordCountByParentCategory(@Path("categoryId") Long categoryId);

    @GET("/word/count/category/{categoryId}")
    Call<ApiResponse<WordCountResponse>> getWordCountCategory(@Path("categoryId") Long categoryId);

    @GET("/word/learned/{userId}")
    Call<ApiResponse<PageResponse<List<WordResponse>>>> getLearnedWords(
            @Path("userId") String userId,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize
    );

    @POST("/word/deactivate-multiple")
    Call<ApiResponse<Void>> deactivateWords(@Body DeleteWordsRequest request);
    // Learned Words
    @POST("/learned-words")
    Call<ApiResponse<LearnedWordResponse>> createLearnedWord(@Body LearnedWordCreationRequest request);
    @GET("/learned-words")
    Call<ApiResponse<LearnedWordResponse>> getLearnedWordById(@Path("id") Long id);

    @GET("statistics/learning-words/{userId}")
    Call<ApiResponse<TotalWordsLearnedResponse>> getTotalWordsLearning(
            @Path("userId") String userId
    );
    @GET("statistics/learned-words/{userId}")
    Call<ApiResponse<TotalWordsLearnedResponse>> getTotalWordsLearned(
            @Path("userId") String userId,
            @Query("date") String date,
            @Query("type") String type
    );
    @GET("statistics/learned-stats/{userId}")
    Call<ApiResponse<WordsLearnedStatsResponse>> getLearningStats(
            @Path("userId") String userId,
            @Query("date") String date,
            @Query("type") String type
    );

    @GET("statistics/category/{categoryId}/accuracy")
    Call<ApiResponse<PageResponse<List<LearnedWordAccuracyResponse>>>> getCategoryAccuracyStats(
            @Path("categoryId") Long categoryId,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize
    );

    @GET("/learned-words/percentage/by-category/{categoryId}/{userId}")
    Call<ApiResponse<LearnedWordPercentageResponse>> getLearnedWordPercentage(
            @Path("categoryId") Long categoryId,
            @Path("userId") String userId
    );

    // Questions
    @GET("/question/true-false/{categoryId}/{userId}")
    Call<ApiResponse<TrueFalseQuestionResponse>> getTrueFalseQuestion(
            @Path("categoryId") Long categoryId,
            @Path("userId") String userId,
            @Query("fromMasteredWords") boolean fromMasteredWords
            );

    @GET("/question/four-options/{categoryId}/{userId}")
    Call<ApiResponse<MultipleChoiceQuestionResponse>> generateFourOptionsQuestions(
            @Path("categoryId") Long categoryId,
            @Path("userId") String userId,
            @Query("fromMasteredWords") boolean fromMasteredWords
    );
    @GET("/question/six-options/{categoryId}/{userId}")
    Call<ApiResponse<MultipleChoiceQuestionResponse>> generateSixOptionsQuestion(
            @Path("categoryId") Long categoryId,
            @Path("userId") String userId,
            @Query("fromMasteredWords") boolean fromMasteredWords
    );

    @GET("/question/word-assembly/{categoryId}/{userId}")
    Call<ApiResponse<WordAssemblyQuestionResponse>> getWordAssemblyQuestion(
            @Path("categoryId") Long categoryId,
            @Path("userId") String userId,
            @Query("fromMasteredWords") boolean fromMasteredWords
    );

    @POST("/question/check-answer")
    Call<ApiResponse<CheckAnswerResponse>> checkAnswer(@Body CheckAnswerRequest request);

    // Reminder
    @POST("/reminder")
    Call<ApiResponse<ReminderResponse>> createReminder(@Body ReminderCreationRequest request);

    @GET("/reminder/{userId}")
    Call<ApiResponse<ReminderResponse>> getReminderByUserId(@Path("userId") String userId);

    @PATCH("/reminder/{id}")
    Call<ApiResponse<ReminderResponse>> updateReminder(@Path("id") Long id, @Body ReminderUpdateRequest reminder);
}
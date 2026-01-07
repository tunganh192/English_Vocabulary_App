package com.example.honda_english.model.UserLesson;

import com.google.gson.annotations.SerializedName;

public class UserLessonCreationRequest {
    @SerializedName("userId")
    private String userId;

    public Long getCategoryId() {
        return categoryId;
    }

    public UserLessonCreationRequest() {
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserLessonCreationRequest(Long categoryId, String userId) {
        this.categoryId = categoryId;
        this.userId = userId;
    }

    @SerializedName("categoryId")
    private Long categoryId;
}

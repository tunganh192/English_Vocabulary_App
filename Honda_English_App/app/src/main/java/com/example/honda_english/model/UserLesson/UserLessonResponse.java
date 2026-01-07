package com.example.honda_english.model.UserLesson;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class UserLessonResponse {

    @SerializedName("id")
    private Long id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("categoryId")
    private Long categoryId;

    @SerializedName("joinedAt")
    private Date joinedAt;

    public UserLessonResponse(Long categoryId, Long id, Date joinedAt, String userId) {
        this.categoryId = categoryId;
        this.id = id;
        this.joinedAt = joinedAt;
        this.userId = userId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
package com.example.honda_english.model.Statistic;

import com.google.gson.annotations.SerializedName;

public class LearnedWordAccuracyResponse {
    @SerializedName("userId")
    private String userId;

    @SerializedName("displayName")
    private String userName;

    @SerializedName("categoryId")
    private Long categoryId;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("learnedCount")
    private long learnedCount;

    @SerializedName("correctCount")
    private long correctCount;

    @SerializedName("totalCount")
    private long totalCount;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getLearnedCount() {
        return learnedCount;
    }

    public void setLearnedCount(long learnedCount) {
        this.learnedCount = learnedCount;
    }

    public long getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(long correctCount) {
        this.correctCount = correctCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
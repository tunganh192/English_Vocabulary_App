package com.example.honda_english.model.Statistic;

import com.google.gson.annotations.SerializedName;

public class LearnedWordPercentageResponse {
    @SerializedName("percentage")
    private double percentage;

    @SerializedName("categoryId")
    private Long categoryId;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("learnedCount")
    private long learnedCount;

    @SerializedName("totalCount")
    private long totalCount;

    // Getter và Setter
    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public long getLearnedCount() { return learnedCount; }
    public void setLearnedCount(long learnedCount) { this.learnedCount = learnedCount; }
    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
}
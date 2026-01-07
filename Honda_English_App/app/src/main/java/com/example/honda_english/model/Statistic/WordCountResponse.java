package com.example.honda_english.model.Statistic;

import com.google.gson.annotations.SerializedName;

public class WordCountResponse {
    @SerializedName("categoryId")
    private Long categoryId;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("wordCount")
    private long wordCount;

    // Getter và Setter
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public long getWordCount() { return wordCount; }
    public void setWordCount(long wordCount) { this.wordCount = wordCount; }
}
package com.example.honda_english.model.Statistic;

import com.google.gson.annotations.SerializedName;

public class TotalWordsLearnedResponse {
    @SerializedName("userId")
    private String userId;
    @SerializedName("type")
    private String type;
    @SerializedName("totalWords")
    private Long totalWords;
    @SerializedName("period")
    private String period;

    // Getter và Setter

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Long getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(Long totalWords) {
        this.totalWords = totalWords;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
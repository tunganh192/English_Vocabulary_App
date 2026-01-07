package com.example.honda_english.model.Statistic;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WordsLearnedStatsResponse {
    @SerializedName("userId")
    private String userId;

    @SerializedName("type")
    private String type;

    @SerializedName("period")
    private String period;

    @SerializedName("details")
    private List<StatDetail> details;

    // Getter và Setter
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public List<StatDetail> getDetails() { return details; }
    public void setDetails(List<StatDetail> details) { this.details = details; }

    public static class StatDetail {
        @SerializedName("timeUnit")
        private String timeUnit;

        @SerializedName("wordCount")
        private Long wordCount;

        // Getter và Setter
        public String getTimeUnit() { return timeUnit; }
        public void setTimeUnit(String timeUnit) { this.timeUnit = timeUnit; }
        public Long getWordCount() { return wordCount; }
        public void setWordCount(Long wordCount) { this.wordCount = wordCount; }
    }
}
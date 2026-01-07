package com.example.honda_english.model.LearnedWord;

import com.google.gson.annotations.SerializedName;

public class LearnedWordResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("wordId")
    private Long wordId;

    @SerializedName("dateLearned")
    private String dateLearned;

    @SerializedName("correctCount")
    private int correctCount;

    @SerializedName("wrongCount")
    private int wrongCount;

    @SerializedName("correctStreak")
    private int correctStreak;

    @SerializedName("mastered")
    private boolean mastered;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public String getDateLearned() {
        return dateLearned;
    }

    public void setDateLearned(String dateLearned) {
        this.dateLearned = dateLearned;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(int wrongCount) {
        this.wrongCount = wrongCount;
    }

    public int getCorrectStreak() {
        return correctStreak;
    }

    public void setCorrectStreak(int correctStreak) {
        this.correctStreak = correctStreak;
    }

    public boolean isMastered() {
        return mastered;
    }

    public void setMastered(boolean mastered) {
        this.mastered = mastered;
    }
}
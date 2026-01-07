package com.example.honda_english.model.LearnedWord;

import com.google.gson.annotations.SerializedName;

public class LearnedWordCreationRequest {
    @SerializedName("userId")
    private String userId;

    @SerializedName("wordId")
    private Long wordId;

    public LearnedWordCreationRequest() {
    }

    public LearnedWordCreationRequest(String userId, Long wordId) {
        this.userId = userId;
        this.wordId = wordId;
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
}

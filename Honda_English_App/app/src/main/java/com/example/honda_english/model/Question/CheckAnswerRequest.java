package com.example.honda_english.model.Question;

import com.google.gson.annotations.SerializedName;

public class CheckAnswerRequest {
    @SerializedName("userId")
    private String userId;

    @SerializedName("wordId")
    private Long wordId;

    @SerializedName("answer")
    private String answer;

    @SerializedName("questionType")
    private String questionType;

    public CheckAnswerRequest() {
    }

    // Constructor
    public CheckAnswerRequest(String userId, Long wordId, String answer, String questionType) {
        this.userId = userId;
        this.wordId = wordId;
        this.answer = answer;
        this.questionType = questionType;
    }

    // Getter và Setter
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Long getWordId() { return wordId; }
    public void setWordId(Long wordId) { this.wordId = wordId; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
}
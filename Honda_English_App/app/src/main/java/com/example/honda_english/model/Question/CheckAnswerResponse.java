package com.example.honda_english.model.Question;

import com.google.gson.annotations.SerializedName;

public class CheckAnswerResponse {
    @SerializedName("correct")
    private boolean isCorrect;

    @SerializedName("correctAnswer")
    private String correctAnswer;

    @SerializedName("userAnswer")
    private String userAnswer;

    // Getter và Setter
    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean isCorrect) { this.isCorrect = isCorrect; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
}
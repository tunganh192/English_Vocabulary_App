package com.example.honda_english.model.Question;

import com.google.gson.annotations.SerializedName;

public class TrueFalseQuestionResponse {
    @SerializedName("wordId")
    private Long wordId;

    @SerializedName("word")
    private String word;

    @SerializedName("displayedMeaning")
    private String displayedMeaning;

    // Getter và Setter
    public Long getWordId() { return wordId; }
    public void setWordId(Long wordId) { this.wordId = wordId; }
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    public String getDisplayedMeaning() { return displayedMeaning; }
    public void setDisplayedMeaning(String displayedMeaning) { this.displayedMeaning = displayedMeaning; }
}
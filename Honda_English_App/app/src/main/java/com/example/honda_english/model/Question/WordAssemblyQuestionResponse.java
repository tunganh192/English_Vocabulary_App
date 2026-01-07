package com.example.honda_english.model.Question;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WordAssemblyQuestionResponse {
    @SerializedName("wordId")
    private Long wordId;

    @SerializedName("meaning")
    private String meaning;

    @SerializedName("parts")
    private List<String> parts;

    // Getter và Setter
    public Long getWordId() { return wordId; }
    public void setWordId(Long wordId) { this.wordId = wordId; }
    public String getMeaning() { return meaning; }
    public void setMeaning(String meaning) { this.meaning = meaning; }
    public List<String> getParts() { return parts; }
    public void setParts(List<String> parts) { this.parts = parts; }
}
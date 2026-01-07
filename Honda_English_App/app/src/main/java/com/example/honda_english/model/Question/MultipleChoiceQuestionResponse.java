package com.example.honda_english.model.Question;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MultipleChoiceQuestionResponse {
    @SerializedName("wordId")
    private Long wordId;

    @SerializedName("word")
    private String word;

    @SerializedName("options")
    private List<String> options;

    // Getter và Setter
    public Long getWordId() { return wordId; }
    public void setWordId(Long wordId) { this.wordId = wordId; }
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
}
package com.example.honda_english.model.Word;

import com.google.gson.annotations.SerializedName;

public class WordResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("englishWord")
    private String englishWord;

    @SerializedName("vietnameseMeaning")
    private String vietnameseMeaning;

    @SerializedName("pronunciation")
    private String pronunciation;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("categoryId")
    private Long categoryId;

    // Getter và Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEnglishWord() { return englishWord; }
    public void setEnglishWord(String englishWord) { this.englishWord = englishWord; }
    public String getVietnameseMeaning() { return vietnameseMeaning; }
    public void setVietnameseMeaning(String vietnameseMeaning) { this.vietnameseMeaning = vietnameseMeaning; }
    public String getPronunciation() { return pronunciation; }
    public void setPronunciation(String pronunciation) { this.pronunciation = pronunciation; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Word toWord() {
        return new Word(englishWord, vietnameseMeaning, id, pronunciation);
    }
}
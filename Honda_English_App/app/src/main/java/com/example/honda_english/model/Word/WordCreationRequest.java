package com.example.honda_english.model.Word;

public class WordCreationRequest {
    private String englishWord;
    private String pronunciation;
    private String vietnameseMeaning;
    private Long categoryId;
    private String createdBy;

    public WordCreationRequest() {
        // Constructor mặc định
    }

    public WordCreationRequest(Long categoryId, String createdBy, String englishWord, String pronunciation, String vietnameseMeaning) {
        this.categoryId = categoryId;
        this.createdBy = createdBy;
        this.englishWord = englishWord;
        this.pronunciation = pronunciation;
        this.vietnameseMeaning = vietnameseMeaning;
    }

    public String getEnglishWord() {
        return englishWord;
    }

    public void setEnglishWord(String englishWord) {
        this.englishWord = englishWord;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getVietnameseMeaning() {
        return vietnameseMeaning;
    }

    public void setVietnameseMeaning(String vietnameseMeaning) {
        this.vietnameseMeaning = vietnameseMeaning;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}

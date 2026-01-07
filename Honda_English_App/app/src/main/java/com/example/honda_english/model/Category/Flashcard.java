package com.example.honda_english.model.Category;

public class Flashcard {
    private long id;
    private String title;
    private long wordCount;
    private int iconResId;
    private String code;
    private String createdBy;

    public Flashcard(long id, String title, long wordCount, int iconResId, String code) {
        this.id = id;
        this.title = title;
        this.wordCount = wordCount;
        this.iconResId = iconResId;
        this.code = code;
    }
    public Flashcard(long id, String title, long wordCount, int iconResId, String code, String createdBy) {
        this.id = id;
        this.title = title;
        this.wordCount = wordCount;
        this.iconResId = iconResId;
        this.code = code;
        this.createdBy = createdBy;
    }

    public Flashcard(String code, String createdBy, int iconResId, long id, String title, long wordCount) {
        this.code = code;
        this.createdBy = createdBy;
        this.iconResId = iconResId;
        this.id = id;
        this.title = title;
        this.wordCount = wordCount;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getWordCount() {
        return wordCount;
    }

    public void setWordCount(long wordCount) {
        this.wordCount = wordCount;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getCode() {
        return code;
    }
}
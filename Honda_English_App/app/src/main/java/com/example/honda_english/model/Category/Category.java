package com.example.honda_english.model.Category;

public class Category {
    private long id;
    private String title;
    private String name;
    private double progress;

    public Category(long id, String title, String name) {
        this.id = id;
        this.title = title;
        this.name = name;
        this.progress = 0.0;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
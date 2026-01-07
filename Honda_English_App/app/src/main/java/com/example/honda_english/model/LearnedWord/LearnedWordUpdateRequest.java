package com.example.honda_english.model.LearnedWord;

public class LearnedWordUpdateRequest {
    private Long id;
    private int correctCount;
    private int wrongCount;
    private int correctStreak;
    private boolean isMastered;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }
    public int getWrongCount() { return wrongCount; }
    public void setWrongCount(int wrongCount) { this.wrongCount = wrongCount; }
    public int getCorrectStreak() { return correctStreak; }
    public void setCorrectStreak(int correctStreak) { this.correctStreak = correctStreak; }
    public boolean isMastered() { return isMastered; }
    public void setMastered(boolean mastered) { isMastered = mastered; }
}

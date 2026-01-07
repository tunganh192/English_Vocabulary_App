package com.example.honda_english.model.Statistic;

public class StudentStats {
    private String studentId;
    private String studentName;
    private int wordsLearned;
    private double correctRate;
    private long correctCount;
    private long totalCount;

    public StudentStats(String studentId, String studentName, int wordsLearned, long correctCount, long totalCount) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.wordsLearned = wordsLearned;
        this.correctCount = correctCount;
        this.totalCount = totalCount;
        this.correctRate = totalCount > 0 ? (double) correctCount / totalCount * 100 : 0.0;
    }

    public double getCorrectRate() {
        return correctRate;
    }

    public void setCorrectRate(double correctRate) {
        this.correctRate = correctRate;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getWordsLearned() {
        return wordsLearned;
    }

    public void setWordsLearned(int wordsLearned) {
        this.wordsLearned = wordsLearned;
    }

    public long getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(long correctCount) {
        this.correctCount = correctCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
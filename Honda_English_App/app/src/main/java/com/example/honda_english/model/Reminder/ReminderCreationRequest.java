package com.example.honda_english.model.Reminder;

public class ReminderCreationRequest {
    private String userId;
    private String time;
    private String repeatType;
    private String repeatInterval;

    public ReminderCreationRequest(String userId, String time, String repeatType, String repeatInterval) {
        this.userId = userId;
        this.time = time;
        this.repeatType = repeatType;
        this.repeatInterval = repeatInterval;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getRepeatType() { return repeatType; }
    public void setRepeatType(String repeatType) { this.repeatType = repeatType; }
    public String getRepeatInterval() { return repeatInterval; }
    public void setRepeatInterval(String repeatInterval) { this.repeatInterval = repeatInterval; }
}
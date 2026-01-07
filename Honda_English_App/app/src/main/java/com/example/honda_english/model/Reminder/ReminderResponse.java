package com.example.honda_english.model.Reminder;

import com.google.gson.annotations.SerializedName;

public class ReminderResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("time")
    private String time;

    @SerializedName("repeatType")
    private String repeatType;

    @SerializedName("repeatInterval")
    private String repeatInterval;

    @SerializedName("isEnabled")
    private Boolean isEnabled;

    // Getter và Setter


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public String getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(String repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
package com.example.honda_english.model.Reminder;

import com.google.gson.annotations.SerializedName;

public class ReminderUpdateRequest {
    @SerializedName("time")
    private String time;

    @SerializedName("repeatType")
    private String repeatType;

    @SerializedName("repeatInterval")
    private String repeatInterval;

    @SerializedName("isEnabled")
    private boolean isEnabled;
    public ReminderUpdateRequest() {
    }

    public ReminderUpdateRequest(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public ReminderUpdateRequest(boolean isEnabled, String repeatInterval, String repeatType, String time) {
        this.isEnabled = isEnabled;
        this.repeatInterval = repeatInterval;
        this.repeatType = repeatType;
        this.time = time;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
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

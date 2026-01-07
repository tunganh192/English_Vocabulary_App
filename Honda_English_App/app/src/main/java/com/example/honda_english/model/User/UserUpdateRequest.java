package com.example.honda_english.model.User;

import com.google.gson.annotations.SerializedName;

public class UserUpdateRequest {
    @SerializedName("password")
    private String password;

    @SerializedName("displayName")
    private String displayName;

    @SerializedName("dailyGoal")
    private Integer dailyGoal;

    @SerializedName("dateOfBirth")
    private String dateOfBirth;

    public UserUpdateRequest() {
    }

    public UserUpdateRequest(Integer dailyGoal) {
        this.dailyGoal = dailyGoal;
    }

    public UserUpdateRequest(Integer dailyGoal, String dateOfBirth, String displayName, String password) {
        this.dailyGoal = dailyGoal;
        this.dateOfBirth = dateOfBirth;
        this.displayName = displayName;
        this.password = password;
    }

    public Integer getDailyGoal() {
        return dailyGoal;
    }

    public void setDailyGoal(Integer dailyGoal) {
        this.dailyGoal = dailyGoal;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

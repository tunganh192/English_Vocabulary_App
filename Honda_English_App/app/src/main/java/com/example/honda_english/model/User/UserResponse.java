package com.example.honda_english.model.User;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("id")
    private String id;
    @SerializedName("displayName")
    private String displayName;

    @SerializedName("role")
    private String role;

    @SerializedName("dailyGoal")
    private int dailyGoal;
    public UserResponse() {
    }
    public UserResponse(int dailyGoal, String dateOfBirth, String displayName, String id, String role) {
        this.dailyGoal = dailyGoal;
        this.dateOfBirth = dateOfBirth;
        this.displayName = displayName;
        this.id = id;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @SerializedName("dateOfBirth")
    private String dateOfBirth;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public int getDailyGoal() { return dailyGoal; }
    public void setDailyGoal(int dailyGoal) { this.dailyGoal = dailyGoal; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
}
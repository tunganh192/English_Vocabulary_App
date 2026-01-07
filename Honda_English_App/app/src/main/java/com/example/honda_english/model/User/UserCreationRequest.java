package com.example.honda_english.model.User;

import com.google.gson.annotations.SerializedName;
import org.threeten.bp.LocalDate;

public class UserCreationRequest {
    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("displayName")
    private String displayName;

    @SerializedName("role")
    private String role;

    @SerializedName("dailyGoal")
    private int dailyGoal;

    @SerializedName("dateOfBirth")
    private LocalDate dateOfBirth;

    public UserCreationRequest(int dailyGoal, LocalDate dateOfBirth, String displayName, String password, String role, String username) {
        this.dailyGoal = dailyGoal;
        this.dateOfBirth = dateOfBirth;
        this.displayName = displayName;
        this.password = password;
        this.role = role;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getDailyGoal() {
        return dailyGoal;
    }

    public void setDailyGoal(Integer dailyGoal) {
        this.dailyGoal = dailyGoal;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
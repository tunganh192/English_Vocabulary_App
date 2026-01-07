package com.example.honda_english.model.Authentication;

import com.google.gson.annotations.SerializedName;

public class AuthenticationRequest {
    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    public AuthenticationRequest(String password, String username) {
        this.password = password;
        this.username = username;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
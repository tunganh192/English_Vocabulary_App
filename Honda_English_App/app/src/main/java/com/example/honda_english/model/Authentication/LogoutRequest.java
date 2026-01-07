package com.example.honda_english.model.Authentication;

import com.google.gson.annotations.SerializedName;

public class LogoutRequest {
    @SerializedName("token")
    String token;

    public LogoutRequest() {
    }

    public LogoutRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

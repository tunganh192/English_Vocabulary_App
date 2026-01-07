package com.example.honda_english.model.Authentication;

import com.google.gson.annotations.SerializedName;

public class RefreshRequest {
    @SerializedName("token")
    String token;

    public RefreshRequest() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

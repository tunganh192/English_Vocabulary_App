package com.example.honda_english.model.Authentication;

import com.google.gson.annotations.SerializedName;

public class IntrospectResponse {
    @SerializedName("token")
    private String token;

    public IntrospectResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
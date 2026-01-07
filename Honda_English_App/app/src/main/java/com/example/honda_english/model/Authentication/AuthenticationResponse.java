package com.example.honda_english.model.Authentication;

import com.google.gson.annotations.SerializedName;

public class AuthenticationResponse {
    @SerializedName("authenticated")
    private boolean authenticated;

    @SerializedName("token")
    private String token;

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
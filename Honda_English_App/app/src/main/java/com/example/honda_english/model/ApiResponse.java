package com.example.honda_english.model;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    @SerializedName("code")
    private int code = 1000;

    @SerializedName("message")
    private String message;

    @SerializedName("result")
    private T result;

    // Getter và Setter
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getResult() { return result; }
    public void setResult(T result) { this.result = result; }
}
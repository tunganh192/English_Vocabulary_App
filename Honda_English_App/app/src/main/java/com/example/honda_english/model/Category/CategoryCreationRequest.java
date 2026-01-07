package com.example.honda_english.model.Category;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class CategoryCreationRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("iconUrl")
    @Nullable
    private String iconUrl;

    @SerializedName("parentId")
    @Nullable
    private Long parentId;

    @SerializedName("code")
    @Nullable
    private String code;

    @SerializedName("createdBy")
    private String createdBy;

    public CategoryCreationRequest(@Nullable String code, String createdBy, @Nullable String iconUrl, String name, @Nullable Long parentId) {
        this.code = code;
        this.createdBy = createdBy;
        this.iconUrl = iconUrl;
        this.name = name;
        this.parentId = parentId;
    }

    public CategoryCreationRequest() {
    }

    @Nullable
    public String getCode() {
        return code;
    }

    public void setCode(@Nullable String code) {
        this.code = code;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Nullable
    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(@Nullable String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(@Nullable Long parentId) {
        this.parentId = parentId;
    }
}

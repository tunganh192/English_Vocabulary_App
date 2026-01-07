package com.example.honda_english.model.Category;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class CategoryResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("iconUrl")
    private String iconUrl;

    @SerializedName("parentId")
    private Long parentId;

    @SerializedName("code")
    private String code;

    @SerializedName("createdAt")
    private Date createdAt;

    @SerializedName("createdBy")
    private String createdBy;

    // Getter và Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
package com.example.honda_english.model;

import com.google.gson.annotations.SerializedName;

public class PageResponse<T> {
    @SerializedName("pageNo")
    private int pageNo;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("totalElements")
    private long totalElements;

    @SerializedName("items")
    private T items;

    // Getter và Setter
    public int getPageNo() { return pageNo; }
    public void setPageNo(int pageNo) { this.pageNo = pageNo; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public T getItems() { return items; }
    public void setItems(T items) { this.items = items; }
}
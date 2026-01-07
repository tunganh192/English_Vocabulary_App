package com.example.honda_english.model.Category;

import java.util.List;

public class DeleteCategoriesRequest {
    private List<Long> ids;

    public DeleteCategoriesRequest(List<Long> ids) {
        this.ids = ids;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
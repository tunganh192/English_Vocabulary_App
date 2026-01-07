package com.example.honda_english.model.Word;

import java.util.List;

public class DeleteWordsRequest {
    private List<Long> ids;

    public DeleteWordsRequest(List<Long> ids) {
        this.ids = ids;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}

package com.example.honda_english.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.honda_english.R;
import com.example.honda_english.model.Word.Word;

import java.util.List;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.ViewHolder> {
    private List<Word> vocabularyList;

    public VocabularyAdapter(List<Word> vocabularyList) {
        this.vocabularyList = vocabularyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vocabulary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Word word = vocabularyList.get(position);
        holder.tvEnglishWord.setText(word.getEnglish());
        holder.tvVietnameseMeaning.setText(word.getVietnamese());
    }

    @Override
    public int getItemCount() {
        return vocabularyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEnglishWord, tvVietnameseMeaning;

        public ViewHolder(View itemView) {
            super(itemView);
            tvEnglishWord = itemView.findViewById(R.id.tvEnglishWord);
            tvVietnameseMeaning = itemView.findViewById(R.id.tvVietnameseMeaning);
        }
    }
}

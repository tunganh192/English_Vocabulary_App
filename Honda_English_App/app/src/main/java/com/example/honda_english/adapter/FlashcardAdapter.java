package com.example.honda_english.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.honda_english.model.Category.Flashcard;
import com.example.honda_english.R;

import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.ViewHolder> {

    private List<Flashcard> flashcardList;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Flashcard flashcard);  
    }

    public FlashcardAdapter(List<Flashcard> flashcardList, OnItemClickListener listener) {
        this.flashcardList = flashcardList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flashcard_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Flashcard flashcard = flashcardList.get(position);

        holder.titleText.setText(flashcard.getTitle());
        holder.wordCountText.setText(flashcard.getWordCount() + " từ vựng");
        holder.iconImage.setImageResource(flashcard.getIconResId());

        if (!TextUtils.isEmpty(flashcard.getCode())) {
            holder.codeText.setText("Code " + flashcard.getCode());
            holder.codeText.setVisibility(View.VISIBLE);
        } else {
            holder.codeText.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(flashcard);
            }
        });
    }

    @Override
    public int getItemCount() {
        return flashcardList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, wordCountText, codeText;
        ImageView iconImage;

        public ViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            wordCountText = itemView.findViewById(R.id.wordCountText);
            codeText = itemView.findViewById(R.id.codeText);
            iconImage = itemView.findViewById(R.id.iconImage);
        }
    }
}

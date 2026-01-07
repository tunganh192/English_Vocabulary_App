package com.example.honda_english.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.honda_english.R;
import com.example.honda_english.model.Category.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<Category> categoryList;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(long categoryId, String categoryName, int progress);
    }

    public CategoryAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.textCategoryTitle.setText(category.getTitle());
        holder.textCategory.setText(category.getName());
        holder.progressBar.setProgress((int) Math.round(category.getProgress()));

        holder.itemView.setOnClickListener(v ->
                listener.onCategoryClick(category.getId(), category.getName(), (int) Math.round(category.getProgress()))
        );
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textCategoryTitle, textCategory;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            textCategoryTitle = itemView.findViewById(R.id.textCategoryTitle);
            textCategory = itemView.findViewById(R.id.textCategory);
            progressBar = itemView.findViewById(R.id.progressBarCategory);
        }
    }
}

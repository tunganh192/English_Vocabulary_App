package com.example.honda_english.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.honda_english.R;
import com.example.honda_english.model.Statistic.StudentStats;

import java.util.List;

public class StudentStatsAdapter extends RecyclerView.Adapter<StudentStatsAdapter.StudentStatsViewHolder> {

    private List<StudentStats> studentStatsList;

    public StudentStatsAdapter(List<StudentStats> studentStatsList) {
        this.studentStatsList = studentStatsList;
    }

    @NonNull
    @Override
    public StudentStatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_stats, parent, false);
        return new StudentStatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentStatsViewHolder holder, int position) {
        StudentStats stats = studentStatsList.get(position);
        holder.tvStudentName.setText("Tên: " + (stats.getStudentName() != null ? stats.getStudentName() : "Không xác định"));
        holder.tvWordsLearned.setText("Số từ đã học: " + stats.getWordsLearned());
        holder.tvCorrectRate.setText(String.format("Tỷ lệ đúng: %.1f%%", stats.getCorrectRate()));
    }

    @Override
    public int getItemCount() {
        return studentStatsList != null ? studentStatsList.size() : 0;
    }

    static class StudentStatsViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvWordsLearned, tvCorrectRate;

        public StudentStatsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvWordsLearned = itemView.findViewById(R.id.tvWordsLearned);
            tvCorrectRate = itemView.findViewById(R.id.tvCorrectRate);
        }
    }
}
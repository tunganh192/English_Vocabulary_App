package com.example.honda_english.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.honda_english.R;
import com.example.honda_english.util.PrefUtils;
import com.example.honda_english.adapter.VocabularyAdapter;
import com.example.honda_english.api.ApiService;
import com.example.honda_english.api.RetrofitClient;
import com.example.honda_english.model.*;
import com.example.honda_english.model.Statistic.LearnedWordPercentageResponse;
import com.example.honda_english.model.Word.Word;
import com.example.honda_english.model.Word.WordResponse;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VocabularyActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VocabularyAdapter vocabularyAdapter;
    private List<Word> vocabularyList = new ArrayList<>();
    private ApiService apiService;
    private ProgressBar progressBar;
    private TextView tvProgress, tvTotalWords, tvCategoryTitle, tvCategoryParent;
    private PrefUtils prefUtils;
    private long categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);

        prefUtils = new PrefUtils(this);
        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        initView();

        Intent intent = getIntent();
        categoryId = intent.getLongExtra("CATEGORY_ID", -1);
        tvCategoryTitle.setText(intent.getStringExtra("CATEGORY_NAME"));
        tvCategoryParent.setText(intent.getStringExtra("CATEGORY_PARENT"));

        findViewById(R.id.imgLeftIcon).setOnClickListener(v -> {
            startActivity(new Intent(this, CategoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            finish();
        });

        findViewById(R.id.btnLearn).setOnClickListener(v -> {
            if (prefUtils.isLoggedIn()) {
                startActivity(new Intent(this, LearningActivity.class).putExtra("CATEGORY_ID", categoryId));
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập để sử dụng chức năng này", Toast.LENGTH_LONG).show();
            }
        });

        fetchVocabulary(categoryId, 1, 10);
    }

    private void initView(){
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        tvCategoryParent = findViewById(R.id.tvCategoryParent);
        tvProgress = findViewById(R.id.tvProgress);
        tvTotalWords = findViewById(R.id.tvTotalWords);
        progressBar = findViewById(R.id.progressBarCategory);
        recyclerView = findViewById(R.id.recyclerViewVocabulary);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vocabularyAdapter = new VocabularyAdapter(vocabularyList);
        recyclerView.setAdapter(vocabularyAdapter);
    }

    private void fetchVocabulary(long categoryId, int pageNo, int pageSize) {
        apiService.getWordsByCategory(categoryId, pageNo, pageSize).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<List<WordResponse>>>> call, Response<ApiResponse<PageResponse<List<WordResponse>>>> response) {
                PageResponse<List<WordResponse>> page = response.body() != null ? response.body().getResult() : null;
                if (response.isSuccessful() && page != null) {
                    for (WordResponse w : page.getItems()) {
                        vocabularyList.add(new Word(w.getEnglishWord(), w.getVietnameseMeaning(), w.getId(), w.getPronunciation()));
                    }
                    if (pageNo < page.getTotalPages()) {
                        fetchVocabulary(categoryId, pageNo + 1, pageSize);
                    } else {
                        updateVocabulary();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<List<WordResponse>>>> call, Throwable t) {}
        });
    }

    private void updateVocabulary() {
        vocabularyList.sort(Comparator.comparingLong(Word::getId));
        vocabularyAdapter.notifyDataSetChanged();
        tvTotalWords.setText(vocabularyList.size() + " từ");
        loadProgress();
    }

    private void loadProgress() {
        String userId = prefUtils.getUserId();
        if (userId == null) return;

        apiService.getLearnedWordPercentage(categoryId, userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<LearnedWordPercentageResponse>> call, Response<ApiResponse<LearnedWordPercentageResponse>> res) {
                LearnedWordPercentageResponse result = res.body() != null ? res.body().getResult() : null;
                if (res.isSuccessful() && result != null) {
                    int percentage = (int) result.getPercentage();
                    tvProgress.setText(percentage + "% đã thuộc");
                    progressBar.setProgress(percentage);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LearnedWordPercentageResponse>> call, Throwable t) {}
        });
    }
}

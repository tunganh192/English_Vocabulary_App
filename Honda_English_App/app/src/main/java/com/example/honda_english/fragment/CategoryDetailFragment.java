package com.example.honda_english.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.honda_english.R;
import com.example.honda_english.activity.LearningActivity;
import com.example.honda_english.activity.LessonsActivity;
import com.example.honda_english.util.PrefUtils;
import com.example.honda_english.adapter.VocabularyAdapter;
import com.example.honda_english.api.ApiService;
import com.example.honda_english.api.RetrofitClient;
import com.example.honda_english.model.ApiResponse;
import com.example.honda_english.model.Category.CategoryResponse;
import com.example.honda_english.model.Statistic.LearnedWordPercentageResponse;
import com.example.honda_english.model.PageResponse;
import com.example.honda_english.model.UserLesson.UserLessonCreationRequest;
import com.example.honda_english.model.UserLesson.UserLessonResponse;
import com.example.honda_english.model.Word.Word;
import com.example.honda_english.model.Word.WordResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryDetailFragment extends Fragment {
    private static final String ARG_CATEGORY_ID = "category_id";
    private static final String ARG_CATEGORY_NAME = "category_name";
    private static final String ARG_CATEGORY_CODE = "category_code";

    private Long categoryId;
    private String categoryName, categoryCode;
    private TextView tvCategoryTitle, tvCategoryCode, tvProgressPercentage, tvProgressCount;
    private Button btnJoinLeave, btnLearn;
    private ProgressBar progressBar;
    private RelativeLayout progressSection;
    private RecyclerView recyclerViewWords;
    private VocabularyAdapter wordAdapter;
    private List<Word> wordList = new ArrayList<>();
    private ApiService apiService;
    private boolean isJoined;
    private long totalWords;
    private PrefUtils prefUtils;

    public static CategoryDetailFragment newInstance(Long categoryId, String categoryName, String categoryCode) {
        CategoryDetailFragment fragment = new CategoryDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CATEGORY_ID, categoryId);
        args.putString(ARG_CATEGORY_NAME, categoryName);
        args.putString(ARG_CATEGORY_CODE, categoryCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getLong(ARG_CATEGORY_ID);
            categoryName = getArguments().getString(ARG_CATEGORY_NAME);
            categoryCode = getArguments().getString(ARG_CATEGORY_CODE);
        }
        apiService = RetrofitClient.getClient(getContext()).create(ApiService.class);
        prefUtils = new PrefUtils(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_detail, container, false);

        tvCategoryTitle = view.findViewById(R.id.tvCategoryTitle);
        tvCategoryCode = view.findViewById(R.id.tvCode);
        btnJoinLeave = view.findViewById(R.id.btnJoinLeave);
        btnLearn = view.findViewById(R.id.btnLearn);
        progressSection = view.findViewById(R.id.progressSection);
        tvProgressPercentage = view.findViewById(R.id.tvProgressPercentage);
        tvProgressCount = view.findViewById(R.id.tvProgressCount);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerViewWords = view.findViewById(R.id.recyclerViewWords);

        recyclerViewWords.setLayoutManager(new LinearLayoutManager(getContext()));
        wordAdapter = new VocabularyAdapter(wordList);
        recyclerViewWords.setAdapter(wordAdapter);

        tvCategoryTitle.setText("Tên: "+ categoryName);
        tvCategoryCode.setText("Code: "+categoryCode);

        loadWords();

        checkJoinStatus();

        btnJoinLeave.setOnClickListener(v -> {
            if (isJoined) {
                leaveCategory();
            } else {
                joinCategory();
            }
        });

        btnLearn.setOnClickListener(v -> {
            if (wordList.isEmpty()) {
                Toast.makeText(getContext(), "Không có từ để học trong danh mục này", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getContext(), LearningActivity.class);
            intent.putExtra("CATEGORY_ID", categoryId);
            intent.putExtra("USER_ID", prefUtils.getUserId());
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isJoined) loadProgress();
    }

    private void updateUIForJoinStatus() {
        btnJoinLeave.setVisibility(View.VISIBLE);
        btnJoinLeave.setText(isJoined ? "Rời" : "Tham gia");
        btnLearn.setVisibility(isJoined ? View.VISIBLE : View.GONE);
        progressSection.setVisibility(isJoined ? View.VISIBLE : View.GONE);
    }

    private void checkJoinStatus() {
        String userId = prefUtils.getUserId();

        apiService.getUserLessonByUserIdAndCategoryId(userId, String.valueOf(categoryId))
                .enqueue(new Callback<ApiResponse<CategoryResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<CategoryResponse>> call,
                                           Response<ApiResponse<CategoryResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                            isJoined = true;
                            updateUIForJoinStatus();
                            loadProgress();
                            Log.d("API", "Trạng thái tham gia: Đã tham gia");
                        } else {
                            isJoined = false;
                            updateUIForJoinStatus();
                            Log.d("API", "Trạng thái tham gia: Chưa tham gia");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<CategoryResponse>> call, Throwable t) {
                        isJoined = false;
                        updateUIForJoinStatus();
                        Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadWords() {
        apiService.getWordsByCategory(categoryId, 1, 100).enqueue(new Callback<ApiResponse<PageResponse<List<WordResponse>>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<List<WordResponse>>>> call, Response<ApiResponse<PageResponse<List<WordResponse>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<WordResponse> reponse = response.body().getResult().getItems();
                    totalWords = response.body().getResult().getTotalElements();
                    wordList.clear();
                    if (reponse != null && !reponse.isEmpty()) {
                        for (WordResponse word : reponse) {
                            wordList.add(new Word(word.getEnglishWord(), word.getVietnameseMeaning(), word.getId(), word.getPronunciation()));
                        }
                        tvProgressCount.setText(totalWords + " từ");
                    } else {
                        tvProgressCount.setText("0 từ");
                        Toast.makeText(getContext(), "Không có từ vựng trong danh mục", Toast.LENGTH_SHORT).show();
                    }
                    wordAdapter.notifyDataSetChanged();
                } else {
                    tvProgressCount.setText("0 từ");
                    Toast.makeText(getContext(), "Lỗi khi tải từ vựng", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<PageResponse<List<WordResponse>>>> call, Throwable t) {
                tvProgressCount.setText("0 từ");
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProgress() {
        apiService.getLearnedWordPercentage(categoryId, prefUtils.getUserId()).enqueue(new Callback<ApiResponse<LearnedWordPercentageResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LearnedWordPercentageResponse>> call, Response<ApiResponse<LearnedWordPercentageResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    double percentage = response.body().getResult().getPercentage();
                    progressBar.setProgress((int) percentage);
                    tvProgressPercentage.setText(percentage + "% đã thuộc");
                } else {
                    setProgressZero();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<LearnedWordPercentageResponse>> call, Throwable t) {
                setProgressZero();
            }
        });
    }

    private void setProgressZero() {
        progressBar.setProgress(0);
        tvProgressPercentage.setText("0.0% đã thuộc");
    }

    private void joinCategory() {
        UserLessonCreationRequest request = new UserLessonCreationRequest();
        request.setUserId(prefUtils.getUserId());
        request.setCategoryId(categoryId);

        apiService.createUserLesson(request).enqueue(new Callback<ApiResponse<UserLessonResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserLessonResponse>> call, Response<ApiResponse<UserLessonResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Tham gia danh mục thành công", Toast.LENGTH_SHORT).show();
                    isJoined = true;
                    updateUIForJoinStatus();
                    loadProgress();
                    if (getActivity() instanceof LessonsActivity) ((LessonsActivity) getActivity()).loadJoinedCategories();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi tham gia danh mục", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<UserLessonResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void leaveCategory() {
        apiService.deleteUserLesson(prefUtils.getUserId(), categoryId).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Rời danh mục thành công", Toast.LENGTH_SHORT).show();
                    isJoined = false;
                    updateUIForJoinStatus();
                    if (getActivity() instanceof LessonsActivity) ((LessonsActivity) getActivity()).loadJoinedCategories();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi rời danh mục", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

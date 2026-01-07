package com.example.honda_english.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.honda_english.R;
import com.example.honda_english.util.PrefUtils;
import com.example.honda_english.util.enums.Time;
import com.example.honda_english.adapter.FlashcardAdapter;
import com.example.honda_english.api.ApiService;
import com.example.honda_english.api.RetrofitClient;
import com.example.honda_english.model.ApiResponse;
import com.example.honda_english.model.Category.CategoryResponse;
import com.example.honda_english.model.Category.Flashcard;
import com.example.honda_english.model.PageResponse;
import com.example.honda_english.model.Statistic.TotalWordsLearnedResponse;
import com.example.honda_english.model.User.UserResponse;
import com.example.honda_english.model.Statistic.WordCountResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressLoading;
    private TextView tvRecentTitle, tvRecentWordCount, tvRecent;
    private LinearLayout layoutRecentCategory;
    private BottomNavigationView bottomNavigationView;
    private ImageView imgRecentIcon;

    private FlashcardAdapter adapter;
    private List<Flashcard> flashcardList = new ArrayList<>();

    private ApiService apiService;
    private SharedPreferences prefs;
    private PrefUtils prefUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        initViews();
        initPreferences();
        initApiService();
        setupRecyclerView();
        setupBottomNavigation();
        setupRecentCategoryClick();

        loadFlashcardsFromApi();
    }

    private void initViews() {
        progressLoading = findViewById(R.id.progressLoading);
        tvRecent = findViewById(R.id.tvRecent);
        tvRecentTitle = findViewById(R.id.tvRecentTitle);
        tvRecentWordCount = findViewById(R.id.tvRecentWordCount);
        recyclerView = findViewById(R.id.recyclerView);
        layoutRecentCategory = findViewById(R.id.layoutRecentCategory);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        imgRecentIcon = findViewById(R.id.imgRecentIcon);
    }

    private void initPreferences() {
        prefUtils = new PrefUtils(this);
        prefs = getSharedPreferences("HondaEnglishPrefs", MODE_PRIVATE);
    }

    private void initApiService() {
        apiService = RetrofitClient.getClient(this).create(ApiService.class);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FlashcardAdapter(flashcardList, this::onFlashcardClicked);
        recyclerView.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
            bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                return true;
            }
            if (id == R.id.navigation_settings) {
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                return true;
            }
            if (id == R.id.navigation_statistics) {
                startActivity(new Intent(HomeActivity.this, StatisticsActivity.class));
                return true;
            }
            if (id == R.id.navigation_lessons) {
                startActivity(new Intent(HomeActivity.this, LessonsActivity.class));
                return true;
            }
            return false;
        });
    }

    private void setupRecentCategoryClick() {
        layoutRecentCategory.setOnClickListener(v -> openRecentCategory());
    }

    private void onFlashcardClicked(Flashcard flashcard) {
        prefs.edit().putLong("lastCategoryId", flashcard.getId()).apply();

        Intent intentCategory = new Intent(HomeActivity.this, CategoryActivity.class);
        intentCategory.putExtra("title", flashcard.getTitle());
        intentCategory.putExtra("CATEGORY_ID", flashcard.getId());
        startActivity(intentCategory);
    }

    private void openRecentCategory() {
        long lastCategoryId = prefs.getLong("lastCategoryId", -1);
        Flashcard recentFlashcard = getRecentCategory(lastCategoryId);

        if (recentFlashcard != null) {
            Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
            intent.putExtra("CATEGORY_ID", recentFlashcard.getId());
            intent.putExtra("title", recentFlashcard.getTitle());
            startActivity(intent);
        }
    }

    private Flashcard getRecentCategory(long lastCategoryId) {
        if (lastCategoryId != -1 && !flashcardList.isEmpty()) {
            return flashcardList.stream()
                    .filter(flashcard -> flashcard.getId() == lastCategoryId)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private void loadFlashcardsFromApi() {
        progressLoading.setVisibility(View.VISIBLE);
        flashcardList.clear();
        fetchCategories(1, 10);
    }

    private void fetchCategories(int pageNo, int pageSize) {
        apiService.getSystemGeneratedParentCategories(pageNo, pageSize).enqueue(new Callback<ApiResponse<PageResponse<List<CategoryResponse>>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<List<CategoryResponse>>>> call,
                                   Response<ApiResponse<PageResponse<List<CategoryResponse>>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    PageResponse<List<CategoryResponse>> pageResponse = response.body().getResult();
                    List<CategoryResponse> categories = pageResponse.getItems();

                    if (categories != null && !categories.isEmpty()) {
                        for (CategoryResponse category : categories) {
                            Flashcard flashcard = createFlashcardFromCategory(category);
                            flashcardList.add(flashcard);
                            fetchWordCountForFlashcard(flashcard);
                        }

                        if (pageNo < pageResponse.getTotalPages()) {
                            fetchCategories(pageNo + 1, pageSize);
                        }
                    } else {
                        updateAdapterIfComplete();
                    }
                } else {
                    showToast("Lỗi hệ thống. Vui lòng thử lại.");
                    updateAdapterIfComplete();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<List<CategoryResponse>>>> call, Throwable t) {
                Log.e("API", "Network error: " + t.getMessage());
                updateAdapterIfComplete();
            }
        });
    }

    private Flashcard createFlashcardFromCategory(CategoryResponse category) {
        String iconName = category.getIconUrl();
        if (iconName == null || !iconName.matches("^[a-zA-Z0-9_]+$")) {
            iconName = "abc";
        }

        int iconResId = getResources().getIdentifier(iconName, "drawable", getPackageName());
        if (iconResId == 0) {
            iconResId = R.drawable.abc;
        }

        return new Flashcard(category.getId(), category.getName(), 0, iconResId, category.getCode());
    }

    private void fetchWordCountForFlashcard(Flashcard flashcard) {
        apiService.getWordCountByParentCategory(flashcard.getId()).enqueue(new Callback<ApiResponse<WordCountResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<WordCountResponse>> call, Response<ApiResponse<WordCountResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    long wordCount = response.body().getResult().getWordCount();
                    flashcard.setWordCount(wordCount);
                    Log.d("API", "Flashcard updated: " + flashcard.getTitle() + ", wordCount: " + wordCount);
                } else {
                    Log.e("API", "Failed to get word count for flashcard id: " + flashcard.getId());
                }
                updateAdapterIfComplete();
            }

            @Override
            public void onFailure(Call<ApiResponse<WordCountResponse>> call, Throwable t) {
                Log.e("API", "Error fetching word count: " + t.getMessage());
                updateAdapterIfComplete();
            }
        });
    }

    private void updateAdapterIfComplete() {
        if (!flashcardList.isEmpty()) {
            progressLoading.setVisibility(View.GONE);
            flashcardList.sort(Comparator.comparingLong(Flashcard::getId));
            adapter.notifyDataSetChanged();
            updateRecentCategory();
            Log.d("API", "Total categories loaded: " + flashcardList.size());
        }
    }

    private void updateRecentCategory() {
        long lastCategoryId = prefs.getLong("lastCategoryId", -1);
        Flashcard recentFlashcard = getRecentCategory(lastCategoryId);

        if (recentFlashcard != null) {
            layoutRecentCategory.setVisibility(View.VISIBLE);
            tvRecent.setVisibility(View.VISIBLE);

            tvRecentTitle.setText(recentFlashcard.getTitle());
            imgRecentIcon.setImageResource(recentFlashcard.getIconResId());
            tvRecentWordCount.setText(recentFlashcard.getWordCount() + " từ vựng");
        } else {
            clearRecentCategoryUI();
        }
    }

    private void clearRecentCategoryUI() {
        layoutRecentCategory.setVisibility(View.GONE);
        tvRecent.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefUtils.isLoggedIn()) {
            checkDailyProgress();
        }
        updateRecentCategory();
    }

    private void checkDailyProgress() {
        String userId = prefUtils.getUserId();
        if (userId == null) return;

        apiService.getUserById(userId).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    int dailyGoal = response.body().getResult().getDailyGoal();
                    checkWordsLearnedToday(userId, dailyGoal);
                } else {
                    Log.e("API", "Invalid user response for daily goal");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Log.e("API", "Network error fetching user info: " + t.getMessage());
            }
        });
    }

    private void checkWordsLearnedToday(String userId, int dailyGoal) {
        String currentDate = getCurrentDateString();

        String lastCheckedDate = prefs.getString("last_checked_date", "");
        if (!currentDate.equals(lastCheckedDate)) {
            prefs.edit()
                    .putBoolean("is_goal_completed_shown", false)
                    .putString("last_checked_date", currentDate)
                    .apply();
        }

        apiService.getTotalWordsLearned(userId, currentDate, Time.DAY.toString())
                .enqueue(new Callback<ApiResponse<TotalWordsLearnedResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<TotalWordsLearnedResponse>> call, Response<ApiResponse<TotalWordsLearnedResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                            long wordsLearned = response.body().getResult().getTotalWords();
                            boolean isGoalCompletedShown = prefs.getBoolean("is_goal_completed_shown", false);

                            if (wordsLearned >= dailyGoal && !isGoalCompletedShown) {
                                showToast("Hoàn thành mục tiêu " + dailyGoal + " từ rồi! Tuyệt vời! 🎉");
                                prefs.edit().putBoolean("is_goal_completed_shown", true).apply();
                            } else {
                                //showToast("Đã học " + wordsLearned + "/" + dailyGoal + " từ. Cố lên nào! 🚀");
                                Toast toast = Toast.makeText(HomeActivity.this, "Đã học " + wordsLearned + "/" + dailyGoal + " từ. Cố lên nào! 🚀", Toast.LENGTH_LONG);
                                toast.show();
                                new Handler().postDelayed(toast::cancel, 1000);
                            }
                        } else {
                            Log.e("API", "Invalid response checking daily words learned");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<TotalWordsLearnedResponse>> call, Throwable t) {
                        Log.e("API", "Network error fetching words learned: " + t.getMessage());
                    }
                });
    }

    private String getCurrentDateString() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return dateFormat.format(calendar.getTime());
    }

    private void showToast(String message) {
        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}

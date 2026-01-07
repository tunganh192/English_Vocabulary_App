//package com.example.honda_english.activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ProgressBar;
//import android.widget.RadioGroup;
//import android.widget.TextView;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.honda_english.R;
//import com.example.honda_english.activity.ultil.PrefUtils;
//import com.example.honda_english.activity.ultil.enums.Time;
//import com.example.honda_english.api.ApiService;
//import com.example.honda_english.api.RetrofitClient;
//import com.example.honda_english.model.ApiResponse;
//import com.example.honda_english.model.Statistic.TotalWordsLearnedResponse;
//import com.example.honda_english.model.User.UserResponse;
//import com.example.honda_english.model.Statistic.WordsLearnedStatsResponse;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Locale;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class StatisticsActivity extends AppCompatActivity {
//    private ProgressBar progressWeek, progressMonth;
//    private TextView tvWeekPercent, tvWeekWords, tvMonthPercent, tvMonthWords, tvKnownWords, tvTotalWords;
//    private BottomNavigationView bottomNavigationView;
//    private ApiService apiService;
//    private PrefUtils prefUtils;
//    private String userId;
//    private int dailyGoal;
//    private List<WordsLearnedStatsResponse.StatDetail> statsDetails;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_statistics);
//
//        // Initialize PrefUtils
//        prefUtils = new PrefUtils(this);
//
//        // Get userId from PrefUtils
//        userId = prefUtils.getUserId();
//
//        // Initialize ApiService
//        apiService = RetrofitClient.getClient(this).create(ApiService.class);
//
//        // Initialize views
//        progressWeek = findViewById(R.id.progressWeek);
//        progressMonth = findViewById(R.id.progressMonth);
//        tvWeekPercent = findViewById(R.id.tvWeekPercent);
//        tvWeekWords = findViewById(R.id.tvWeekWords);
//        tvMonthPercent = findViewById(R.id.tvMonthPercent);
//        tvMonthWords = findViewById(R.id.tvMonthWords);
//        tvKnownWords = findViewById(R.id.tvKnownWords);
//        tvTotalWords = findViewById(R.id.tvTotalWords);
//        bottomNavigationView = findViewById(R.id.bottomNavigation);
//
//        RadioGroup rgDays = findViewById(R.id.rgDays);
//
//        // Get dailyGoal from API
//        apiService.getUserById(userId).enqueue(new Callback<ApiResponse<UserResponse>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
//                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
//                    dailyGoal = response.body().getResult().getDailyGoal();
//                    selectTodayRadioButton(rgDays);
//                    updateStatistics();
//                } else {
//                    dailyGoal = 0;
//                    selectTodayRadioButton(rgDays);
//                    updateStatistics();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
//                dailyGoal = 0;
//                selectTodayRadioButton(rgDays);
//                updateStatistics();
//            }
//        });
//
//        // Handle day selection
//        rgDays.setOnCheckedChangeListener((group, checkedId) -> {
//            String selectedDay = getDayFromRadioId(checkedId);
//            if (selectedDay != null) {
//                updateKnownWordsForDay(selectedDay);
//            } else {
//                tvKnownWords.setText("Số từ đã thuộc: 0");
//            }
//        });
//
//        // Bottom Navigation
//        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//            if (itemId == R.id.navigation_home) {
//                startActivity(new Intent(StatisticsActivity.this, HomeActivity.class));
//                finish();
//                return true;
//            } else if (itemId == R.id.navigation_settings) {
//                startActivity(new Intent(StatisticsActivity.this, SettingsActivity.class));
//                finish();
//                return true;
//            } else if (itemId == R.id.navigation_lessons) {
//                startActivity(new Intent(StatisticsActivity.this, LessonsActivity.class));
//                finish();
//                return true;
//            }
//            return false;
//        });
//        bottomNavigationView.setSelectedItemId(R.id.navigation_statistics);
//    }
//
//    private void selectTodayRadioButton(RadioGroup rgDays) {
//        Calendar calendar = Calendar.getInstance();
//        String today = new SimpleDateFormat("EEEE", Locale.US).format(calendar.getTime()).toUpperCase();
//        int radioButtonId = getRadioIdFromDay(today);
//        if (radioButtonId != -1) {
//            rgDays.check(radioButtonId);
//        }
//    }
//
//    private void updateStatistics() {
//        if (userId == null)
//            return;
//
//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//        String dateStr = dateFormat.format(calendar.getTime());
//
//        int totalWordsTargetWeek = dailyGoal * 7;
//        int totalWordsTargetMonth = dailyGoal * 30;
//
//        // Weekly statistics
//        apiService.getTotalWordsLearned(userId, dateStr, Time.WEEK.toString())
//                .enqueue(new Callback<ApiResponse<TotalWordsLearnedResponse>>() {
//                    @Override
//                    public void onResponse(Call<ApiResponse<TotalWordsLearnedResponse>> call, Response<ApiResponse<TotalWordsLearnedResponse>> response) {
//                        if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
//                            TotalWordsLearnedResponse stat = response.body().getResult();
//                            Log.d("StatisticsActivity", "WEEK totalWords: " + stat.getTotalWords());
//                            int wordsLearnedWeek = stat.getTotalWords().intValue();
//                            int percentWeek = totalWordsTargetWeek > 0 ? (int) ((wordsLearnedWeek / (float) totalWordsTargetWeek) * 100) : 0;
//                            progressWeek.setProgress(percentWeek);
//                            tvWeekPercent.setText(percentWeek + "% mục tiêu");
//                            tvWeekWords.setText(wordsLearnedWeek + "/" + totalWordsTargetWeek + " từ vựng trong tuần này");
//                        } else {
//                            Log.e("StatisticsActivity", "WEEK response failed: " + (response.body() != null ? response.body().getCode() : response.code()));
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ApiResponse<TotalWordsLearnedResponse>> call, Throwable t) {
//                        Log.e("StatisticsActivity", "WEEK network error: " + t.getMessage());
//                    }
//                });
//
//        // Monthly statistics
//        apiService.getTotalWordsLearned(userId, dateStr, Time.MONTH.toString())
//                .enqueue(new Callback<ApiResponse<TotalWordsLearnedResponse>>() {
//                    @Override
//                    public void onResponse(Call<ApiResponse<TotalWordsLearnedResponse>> call, Response<ApiResponse<TotalWordsLearnedResponse>> response) {
//                        if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
//                            TotalWordsLearnedResponse stat = response.body().getResult();
//                            Log.d("StatisticsActivity", "MONTH totalWords: " + stat.getTotalWords());
//                            int wordsLearnedMonth = stat.getTotalWords().intValue();
//                            int percentMonth = totalWordsTargetMonth > 0 ? (int) ((wordsLearnedMonth / (float) totalWordsTargetMonth) * 100) : 0;
//                            progressMonth.setProgress(percentMonth);
//                            tvMonthPercent.setText(percentMonth + "% mục tiêu");
//                            tvMonthWords.setText(wordsLearnedMonth + "/" + totalWordsTargetMonth + " từ vựng trong tháng này");
//                        } else {
//                            Log.e("StatisticsActivity", "MONTH response failed: " + (response.body() != null ? response.body().getCode() : response.code()));
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ApiResponse<TotalWordsLearnedResponse>> call, Throwable t) {
//                        Log.e("StatisticsActivity", "MONTH network error: " + t.getMessage());
//                    }
//                });
//
//        // Total words learned
//        apiService.getTotalWordsLearning(userId).enqueue(new Callback<ApiResponse<TotalWordsLearnedResponse>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<TotalWordsLearnedResponse>> call, Response<ApiResponse<TotalWordsLearnedResponse>> response) {
//                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
//                    TotalWordsLearnedResponse stat = response.body().getResult();
//                    Log.d("StatisticsActivity", "TOTAL words learned: " + stat.getTotalWords());
//                    int totalWords = stat.getTotalWords().intValue();
//                    tvTotalWords.setText("Tổng số từ đã học: " + totalWords);
//                } else {
//                    Log.e("StatisticsActivity", "TOTAL response failed: " + (response.body() != null ? response.body().getCode() : response.code()));
//                    tvTotalWords.setText("Tổng số từ đã học: 0");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<TotalWordsLearnedResponse>> call, Throwable t) {
//                Log.e("StatisticsActivity", "TOTAL network error: " + t.getMessage());
//                tvTotalWords.setText("Tổng số từ đã học: 0");
//            }
//        });
//
//        // Daily stats
//        updateDailyStats();
//    }
//
//    private void updateDailyStats() {
//        if (userId == null)
//            return;
//
//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//        String dateStr = dateFormat.format(calendar.getTime());
//        String today = new SimpleDateFormat("EEEE", Locale.US).format(calendar.getTime()).toUpperCase();
//
//        apiService.getLearningStats(userId, dateStr, "DAY_IN_WEEK").enqueue(new Callback<ApiResponse<WordsLearnedStatsResponse>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<WordsLearnedStatsResponse>> call, Response<ApiResponse<WordsLearnedStatsResponse>> response) {
//                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null && response.body().getResult().getType().equals("DAY_IN_WEEK")) {
//                    WordsLearnedStatsResponse stats = response.body().getResult();
//                    statsDetails = stats.getDetails();
//                    Log.d("Statistics", "DAY_IN_WEEK details: " + statsDetails);
//                    updateKnownWordsForDay(today);
//                } else {
//                    Log.e("Statistics", "DAY_IN_WEEK response failed: " + (response.body() != null ? response.body().getCode() : response.code()));
//                    statsDetails = null;
//                    tvKnownWords.setText("Số từ đã thuộc: 0");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<WordsLearnedStatsResponse>> call, Throwable t) {
//                Log.e("Statistics", "DAY_IN_WEEK network error: " + t.getMessage());
//                statsDetails = null;
//                tvKnownWords.setText("Số từ đã thuộc: 0");
//            }
//        });
//    }
//
//    private void updateKnownWordsForDay(String selectedDay) {
//        if (statsDetails != null) {
//            for (WordsLearnedStatsResponse.StatDetail detail : statsDetails) {
//                if (detail.getTimeUnit() != null && detail.getTimeUnit().equalsIgnoreCase(selectedDay)) {
//                    long wordCount = detail.getWordCount() != null ? detail.getWordCount() : 0;
//                    tvKnownWords.setText("Số từ đã thuộc: " + wordCount);
//                    return;
//                }
//            }
//        }
//        tvKnownWords.setText("Số từ đã thuộc: 0");
//    }
//
//    private String getDayFromRadioId(int checkedId) {
//        if (checkedId == R.id.rbMonday) return "MONDAY";
//        if (checkedId == R.id.rbTuesday) return "TUESDAY";
//        if (checkedId == R.id.rbWednesday) return "WEDNESDAY";
//        if (checkedId == R.id.rbThursday) return "THURSDAY";
//        if (checkedId == R.id.rbFriday) return "FRIDAY";
//        if (checkedId == R.id.rbSaturday) return "SATURDAY";
//        if (checkedId == R.id.rbSunday) return "SUNDAY";
//        return null;
//    }
//
//    private int getRadioIdFromDay(String day) {
//        switch (day.toUpperCase()) {
//            case "MONDAY": return R.id.rbMonday;
//            case "TUESDAY": return R.id.rbTuesday;
//            case "WEDNESDAY": return R.id.rbWednesday;
//            case "THURSDAY": return R.id.rbThursday;
//            case "FRIDAY": return R.id.rbFriday;
//            case "SATURDAY": return R.id.rbSaturday;
//            case "SUNDAY": return R.id.rbSunday;
//            default: return -1;
//        }
//    }
//}

package com.example.honda_english.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.honda_english.R;
import com.example.honda_english.util.PrefUtils;
import com.example.honda_english.util.enums.Time;
import com.example.honda_english.util.enums.TimeUnitType;
import com.example.honda_english.api.ApiService;
import com.example.honda_english.api.RetrofitClient;
import com.example.honda_english.model.ApiResponse;
import com.example.honda_english.model.Statistic.TotalWordsLearnedResponse;
import com.example.honda_english.model.Statistic.WordsLearnedStatsResponse;
import com.example.honda_english.model.User.UserResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsActivity extends AppCompatActivity {
    private ProgressBar progressWeek, progressMonth;
    private TextView tvWeekPercent, tvWeekWords, tvMonthPercent, tvMonthWords, tvKnownWords, tvTotalWords;
    private RadioGroup rgDays;
    private ApiService apiService;
    private String userId;
    private int dailyGoal;
    private List<WordsLearnedStatsResponse.StatDetail> statsDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistics);

        userId = new PrefUtils(this).getUserId();
        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        initViews();
        fetchUserDailyGoal();
    }

    private void initViews() {
        progressWeek = findViewById(R.id.progressWeek);
        progressMonth = findViewById(R.id.progressMonth);
        tvWeekPercent = findViewById(R.id.tvWeekPercent);
        tvWeekWords = findViewById(R.id.tvWeekWords);
        tvMonthPercent = findViewById(R.id.tvMonthPercent);
        tvMonthWords = findViewById(R.id.tvMonthWords);
        tvKnownWords = findViewById(R.id.tvKnownWords);
        tvTotalWords = findViewById(R.id.tvTotalWords);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_statistics);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) startActivity(new Intent(this, HomeActivity.class));
            else if (id == R.id.navigation_lessons) startActivity(new Intent(this, LessonsActivity.class));
            else if (id == R.id.navigation_settings) startActivity(new Intent(this, SettingsActivity.class));
            else return false;
            finish();
            return true;
        });

        rgDays = findViewById(R.id.rgDays);
        rgDays.setOnCheckedChangeListener((group, checkedId) -> {
            String day = getDayFromRadioId(checkedId);
            updateKnownWordsForDay(day);
        });

    }

    private void fetchUserDailyGoal() {
        apiService.getUserById(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null){
                    dailyGoal = response.body().getResult().getDailyGoal();
                }else{
                    dailyGoal = 0;
                }

                selectTodayRadioButton(rgDays);
                updateStatistics();
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                dailyGoal = 0;
                selectTodayRadioButton((RadioGroup) findViewById(R.id.rgDays));
                updateStatistics();
            }
        });
    }

    private void updateStatistics() {
        if (userId == null) return;

        String todayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime());
        int weekTarget = dailyGoal * 7;
        int monthTarget = dailyGoal * 30;

        loadProgress(Time.WEEK, todayStr, weekTarget, progressWeek, tvWeekPercent, tvWeekWords, "tuần này");
        loadProgress(Time.MONTH, todayStr, monthTarget, progressMonth, tvMonthPercent, tvMonthWords, "tháng này");

        apiService.getTotalWordsLearning(userId).enqueue(getSimpleStatCallback(tvTotalWords, "Tổng số từ đã học: "));

        updateDailyStats(todayStr);
    }

    private void loadProgress(Time time, String date, int target, ProgressBar progressBar, TextView tvPercent, TextView tvWords, String text) {
        apiService.getTotalWordsLearned(userId, date, time.toString()).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<TotalWordsLearnedResponse>> call, Response<ApiResponse<TotalWordsLearnedResponse>> response) {
                int learned = 0;
                if (response.body() != null && response.body().getResult() != null){
                    learned = response.body().getResult().getTotalWords().intValue();
                }

                int percent = target > 0 ? (int) ((learned / (float) target) * 100) : 0;
//                float pecent = target > 0 ? (learned / (float) target) * 100 : 0;
//                String percentText = String.format(Locale.US, "%.2f%% mục tiêu", percent);

                progressBar.setProgress(percent);
                tvPercent.setText(percent + "% mục tiêu");
                tvWords.setText(learned + "/" + target + " từ vựng trong " + text);
            }

            @Override
            public void onFailure(Call<ApiResponse<TotalWordsLearnedResponse>> call, Throwable t) {
                progressBar.setProgress(0);
                tvPercent.setText("0% mục tiêu");
                tvWords.setText("0/" + target + " từ vựng trong " + text);
            }
        });
    }

    private Callback<ApiResponse<TotalWordsLearnedResponse>> getSimpleStatCallback(TextView textView, String prefix) {
        return new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<TotalWordsLearnedResponse>> call, Response<ApiResponse<TotalWordsLearnedResponse>> response) {
                int count = 0;
                if (response.body() != null || response.body().getResult() != null){
                    count = response.body().getResult().getTotalWords().intValue();
                    textView.setText(prefix + count);
                    return;
                }

                textView.setText(prefix + count);
            }

            @Override
            public void onFailure(Call<ApiResponse<TotalWordsLearnedResponse>> call, Throwable t) {
                textView.setText(prefix + "0");
            }
        };
    }

    private void updateDailyStats(String date) {
        apiService.getLearningStats(userId, date, TimeUnitType.DAY_IN_WEEK.name()).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<WordsLearnedStatsResponse>> call, Response<ApiResponse<WordsLearnedStatsResponse>> response) {
                if (response.body() != null || response.body().getResult() != null){
                    WordsLearnedStatsResponse stats = response.body().getResult();
                        statsDetails = stats.getDetails();
                        String today = new SimpleDateFormat("EEEE", Locale.US).format(Calendar.getInstance().getTime()).toUpperCase();
                        updateKnownWordsForDay(today);

                }else {
                    statsDetails = null;
                    tvKnownWords.setText("Số từ đã thuộc: 0");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<WordsLearnedStatsResponse>> call, Throwable t) {
                statsDetails = null;
                tvKnownWords.setText("Số từ đã thuộc: 0");
            }
        });
    }

    private void updateKnownWordsForDay(String day) {
        if (day == null || statsDetails == null) {
            tvKnownWords.setText("Số từ đã thuộc: 0");
            return;
        }
        long count = statsDetails.stream()
                .filter(d -> day.equalsIgnoreCase(d.getTimeUnit()))
                .map(WordsLearnedStatsResponse.StatDetail::getWordCount)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(0L);
        tvKnownWords.setText("Số từ đã thuộc: " + count);
    }

    private void selectTodayRadioButton(RadioGroup rgDays) {
        String today = new SimpleDateFormat("EEEE", Locale.US).format(Calendar.getInstance().getTime()).toUpperCase();
        int radioId = getRadioIdFromDay(today);
        if (radioId != -1) rgDays.check(radioId);
    }

    private String getDayFromRadioId(int id) {
        Map<Integer, String> dayMap = Map.of(
                R.id.rbMonday, "MONDAY", R.id.rbTuesday, "TUESDAY", R.id.rbWednesday, "WEDNESDAY",
                R.id.rbThursday, "THURSDAY", R.id.rbFriday, "FRIDAY", R.id.rbSaturday, "SATURDAY",
                R.id.rbSunday, "SUNDAY"
        );
        return dayMap.getOrDefault(id, null);
    }

    private int getRadioIdFromDay(String day) {
        Map<String, Integer> idMap = Map.of(
                "MONDAY", R.id.rbMonday, "TUESDAY", R.id.rbTuesday, "WEDNESDAY", R.id.rbWednesday,
                "THURSDAY", R.id.rbThursday, "FRIDAY", R.id.rbFriday, "SATURDAY", R.id.rbSaturday,
                "SUNDAY", R.id.rbSunday
        );
        return idMap.getOrDefault(day.toUpperCase(), -1);
    }
}

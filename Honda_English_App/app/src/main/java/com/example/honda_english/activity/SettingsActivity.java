package com.example.honda_english.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.honda_english.R;
import com.example.honda_english.fragment.MyLessonFragment;
import com.example.honda_english.fragment.LearnedWordsFragment;
import com.example.honda_english.fragment.TeacherStatisticsFragment;
import com.example.honda_english.util.PrefUtils;
import com.example.honda_english.receiver.ReminderReceiver;
import com.example.honda_english.util.enums.RepeatType;
import com.example.honda_english.util.enums.Role;
import com.example.honda_english.api.ApiService;
import com.example.honda_english.api.RetrofitClient;
import com.example.honda_english.model.ApiResponse;
import com.example.honda_english.model.Authentication.LogoutRequest;
import com.example.honda_english.model.Category.CategoryResponse;
import com.example.honda_english.model.PageResponse;
import com.example.honda_english.model.Question.TrueFalseQuestionResponse;
import com.example.honda_english.model.Reminder.ReminderCreationRequest;
import com.example.honda_english.model.Reminder.ReminderResponse;
import com.example.honda_english.model.Reminder.ReminderUpdateRequest;
import com.example.honda_english.model.User.UserResponse;
import com.example.honda_english.model.User.UserUpdateRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView textViewTitle, tvLoginLogout;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout btnDailyGoal, btnStartTest, btnSetReminder, btnLearnedWords,
            btnAdjustTTS, btnMyLesson,btnTeacherStats, btnLogout, btnLogin;

    private ScrollView scrollViewSettings;
    private ApiService apiService;
    private PrefUtils prefUtils;
    private static final String CHANNEL_ID = "reminder_channel";
    private static final String CHANNEL_NAME = "Reminder Notifications";
    private static final int NOTIFICATION_PERMISSION_CODE = 101;
    private static final int REQUEST_CODE = 100;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        prefUtils = new PrefUtils(this);

        sharedPreferences = getSharedPreferences("TTS_Prefs", MODE_PRIVATE);

        createNotificationChannel();

        requestNotificationPermission();
        requestExactAlarmPermission();

        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        loadAndScheduleReminder();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        textViewTitle = findViewById(R.id.toolbar_title);
        textViewTitle.setText("Cài đặt");

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_settings);

        scrollViewSettings = findViewById(R.id.scrollViewSettings);
        btnDailyGoal = findViewById(R.id.btnDailyGoal);
        btnLearnedWords = findViewById(R.id.btnLearnedWords);
        btnSetReminder = findViewById(R.id.btnSetReminder);
        btnAdjustTTS = findViewById(R.id.btnAdjustTTS);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogin = findViewById(R.id.btnLogin);
        btnMyLesson = findViewById(R.id.btnMyLesson);
        btnTeacherStats = findViewById(R.id.btnTeacherStats);
        btnStartTest = findViewById(R.id.btnStartTest);
        tvLoginLogout = findViewById(R.id.tvLoginLogout);

        if (!prefUtils.isLoggedIn()) {
            tvLoginLogout.setText("Đăng nhập");
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
        }
        else {
            tvLoginLogout.setText("Đăng xuất");
            btnLogin.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
        }

        btnDailyGoal.setOnClickListener(v -> {
            if (!checkLoginBeforeAction()) {
                return;
            }
            showDailyGoalDialog();
        });
        btnSetReminder.setOnClickListener(v -> {
            if (!checkLoginBeforeAction()) {
                return;
            }
            showReminderDialog();
        });
        btnAdjustTTS.setOnClickListener(v -> showAdjustTTSDialog());
        btnLearnedWords.setOnClickListener(v -> {
            if (!checkLoginBeforeAction()) {
                return;
            }
            showLearnedWordsFragment();
        });
        btnMyLesson.setOnClickListener(v -> {
            if (!checkLoginBeforeAction()) {
                return;
            }

            if (!Role.TEACHER.toString().equals(prefUtils.getUserRole())) {
                Toast.makeText(this, "Chỉ giáo viên mới có dùng chức năng này!", Toast.LENGTH_SHORT).show();
                return;
            }
            showMyLessonFragment();
        });
        btnTeacherStats.setOnClickListener(v -> {
            if (!checkLoginBeforeAction()) {
                return;
            }

            if (!"TEACHER".equals(prefUtils.getUserRole())) {
                Toast.makeText(this, "Chỉ giáo viên được xem thống kê học sinh!", Toast.LENGTH_SHORT).show();
                return;
            }
            showTeacherStatsFragment();
        });
        btnLogout.setOnClickListener(v -> logout());
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        btnStartTest.setOnClickListener(v -> {
            if (!checkLoginBeforeAction()) {
                return;
            }
            showTestCategoryDialog();
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_statistics) {
                startActivity(new Intent(SettingsActivity.this, StatisticsActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_lessons) {
                startActivity(new Intent(SettingsActivity.this, LessonsActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    //Test
    private void showTestCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn danh mục kiểm tra");

        apiService.getAllSubCategoriesByAdmin(1, 100).enqueue(new Callback<ApiResponse<PageResponse<List<CategoryResponse>>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<List<CategoryResponse>>>> call, Response<ApiResponse<PageResponse<List<CategoryResponse>>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    List<CategoryResponse> categories = response.body().getResult().getItems();
                    if (categories.isEmpty()) {
                        Toast.makeText(SettingsActivity.this, "Không có danh mục nào", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] categoryNames = new String[categories.size()];
                    Long[] categoryIds = new Long[categories.size()];
                    for (int i = 0; i < categories.size(); i++) {
                        categoryNames[i] = categories.get(i).getName();
                        categoryIds[i] = categories.get(i).getId();
                    }

                    builder.setItems(categoryNames, (dialog, which) -> {
                        Long selectedCategoryId = categoryIds[which];
                        startTest(selectedCategoryId);
                    });
                    builder.setNegativeButton("Hủy", null);
                    builder.create().show();
                } else {
                    Toast.makeText(SettingsActivity.this, "Lỗi khi lấy danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<List<CategoryResponse>>>> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void startTest(Long categoryId) {
        apiService.getTrueFalseQuestion(categoryId, prefUtils.getUserId(), false).enqueue(new Callback<ApiResponse<TrueFalseQuestionResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TrueFalseQuestionResponse>> call, Response<ApiResponse<TrueFalseQuestionResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    TrueFalseQuestionResponse question = response.body().getResult();
                    Intent intent = new Intent(SettingsActivity.this, QuizTrueFalseActivity.class);
                    intent.putExtra("USER_ID", prefUtils.getUserId());
                    intent.putExtra("WORD_ID", question.getWordId());
                    intent.putExtra("WORD", question.getWord());
                    intent.putExtra("DISPLAYED_MEANING", question.getDisplayedMeaning());
                    //intent.putExtra("TOTAL_QUIZ_COUNT", 1);
                    //intent.putExtra("QUIZ_TYPE", 2);
                    intent.putExtra("IS_TEST_MODE", true);
                    intent.putExtra("CATEGORY_ID", categoryId);
                    intent.putExtra("QUESTION_INDEX", 1);
                    intent.putExtra("CORRECT_ANSWERS_COUNT", 0);
                    startActivity(intent);
                } else {
                    Toast.makeText(SettingsActivity.this, "Lỗi khi lấy câu hỏi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TrueFalseQuestionResponse>> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Text to speech
    private void showAdjustTTSDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_adjust_tts, null);
        builder.setView(dialogView);

        SeekBar seekBarSpeechRate = dialogView.findViewById(R.id.seekBarSpeechRate);
        SeekBar seekBarPitch = dialogView.findViewById(R.id.seekBarPitch);
        TextView tvSpeechRateValue = dialogView.findViewById(R.id.tvSpeechRateValue);
        TextView tvPitchValue = dialogView.findViewById(R.id.tvPitchValue);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        int savedSpeechRate = sharedPreferences.getInt("speechRate", 100);
        int savedPitch = sharedPreferences.getInt("pitch", 100);

        seekBarSpeechRate.setProgress(savedSpeechRate);
        seekBarPitch.setProgress(savedPitch);
        tvSpeechRateValue.setText(String.format("%.1fx", savedSpeechRate / 100.0f));
        tvPitchValue.setText(String.format("%.1f", savedPitch / 100.0f));

        // Update
        seekBarSpeechRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float speechRate = progress / 100.0f;
                tvSpeechRateValue.setText(String.format("%.1fx", speechRate));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float pitch = progress / 100.0f;
                tvPitchValue.setText(String.format("%.1f", pitch));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            int newSpeechRate = seekBarSpeechRate.getProgress();
            int newPitch = seekBarPitch.getProgress();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("speechRate", newSpeechRate);
            editor.putInt("pitch", newPitch);
            editor.apply();

            Toast.makeText(this, "Đã lưu cài đặt tiếng nói", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    //Notification
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Thông báo cho nhắc nhở học tập");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
    }
    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Cần quyền thông báo để gửi nhắc nhở", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void loadAndScheduleReminder() {
        if (prefUtils.getUserId() == null)
            return;
        apiService.getReminderByUserId(prefUtils.getUserId()).enqueue(new Callback<ApiResponse<ReminderResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReminderResponse>> call, Response<ApiResponse<ReminderResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    ReminderResponse reminder = response.body().getResult();
                    if (reminder.getEnabled()) {
                        scheduleReminder(reminder);
                        Log.d("SettingsActivity", "Scheduled existing reminder on start: id=" + reminder.getId());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ReminderResponse>> call, Throwable t) {
                Log.e("SettingsActivity", "Failed to load reminder on start: " + t.getMessage());
            }
        });
    }
    private void showReminderDialog() {
        apiService.getReminderByUserId(prefUtils.getUserId()).enqueue(new Callback<ApiResponse<ReminderResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReminderResponse>> call, Response<ApiResponse<ReminderResponse>> response) {
                ReminderResponse reminder = null;
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    reminder = response.body().getResult();
                }
                createAndShowReminderDialog(reminder);
            }

            @Override
            public void onFailure(Call<ApiResponse<ReminderResponse>> call, Throwable t) {
                Log.e("SettingsActivity", "Lỗi mạng khi tải nhắc nhở: " + t.getMessage());
                createAndShowReminderDialog(null);
            }
        });
    }
    private void createAndShowReminderDialog(ReminderResponse reminder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_set_reminder, null);
        builder.setView(dialogView);

        setupReminderDialogUI(dialogView, reminder);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> {
            if (saveReminder(dialogView, reminder)) {
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }
    private void setupReminderDialogUI(View dialogView, ReminderResponse reminder) {
        Switch switchReminder = dialogView.findViewById(R.id.switchReminder);
        LinearLayout reminderOptions = dialogView.findViewById(R.id.reminderOptions);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        RadioGroup rgRepeatType = dialogView.findViewById(R.id.rgRepeatType);
        EditText etRepeatInterval = dialogView.findViewById(R.id.etRepeatInterval);

        final boolean[] isEnabled = {reminder != null && reminder.getEnabled()};
        switchReminder.setChecked(isEnabled[0]);
        reminderOptions.setVisibility(isEnabled[0] ? View.VISIBLE : View.GONE);
        etRepeatInterval.setText("01:00:00");


        if (reminder != null) {
            String time = reminder.getTime();
            try {
                String[] parts = time.split(":");
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                timePicker.setHour(hour);
                timePicker.setMinute(minute);
            } catch (Exception e) {
                Log.e("SettingsActivity", "Invalid time format: " + e.getMessage());
            }

            RepeatType repeatType = RepeatType.valueOf(reminder.getRepeatType().toUpperCase());
            if (repeatType == RepeatType.ONCE) {
                rgRepeatType.check(R.id.rbOnce);
                etRepeatInterval.setVisibility(View.GONE);
            } else if (repeatType == RepeatType.DAILY) {
                rgRepeatType.check(R.id.rbDaily);
                etRepeatInterval.setVisibility(View.GONE);
            } else if (repeatType == RepeatType.INTERVAL) {
                rgRepeatType.check(R.id.rbInterval);
                etRepeatInterval.setVisibility(View.VISIBLE);
                etRepeatInterval.setText(reminder.getRepeatInterval());
            }
        } else {
            timePicker.setHour(8);
            timePicker.setMinute(0);
            rgRepeatType.check(R.id.rbDaily);
            etRepeatInterval.setVisibility(View.GONE);
        }

        switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isEnabled[0] = isChecked;
            reminderOptions.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked && reminder != null) {
                ReminderUpdateRequest updateRequest = new ReminderUpdateRequest();
                updateRequest.setEnabled(false);
                apiService.updateReminder(reminder.getId(), updateRequest).enqueue(new Callback<ApiResponse<ReminderResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<ReminderResponse>> call, Response<ApiResponse<ReminderResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1000) {
                            cancelReminder();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<ReminderResponse>> call, Throwable t) {
                        Log.e("SettingsActivity", "Failed to disable reminder: " + t.getMessage());
                    }
                });
            }
        });

        rgRepeatType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbInterval) {
                etRepeatInterval.setVisibility(View.VISIBLE);
            } else {
                etRepeatInterval.setVisibility(View.GONE);
            }
        });
    }
    private boolean saveReminder(View dialogView, ReminderResponse existingReminder) {
        Switch switchReminder = dialogView.findViewById(R.id.switchReminder);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        RadioGroup rgRepeatType = dialogView.findViewById(R.id.rgRepeatType);
        EditText etRepeatInterval = dialogView.findViewById(R.id.etRepeatInterval);

        if (!switchReminder.isChecked()) {
            Toast.makeText(this, "Vui lòng bật nhắc nhở", Toast.LENGTH_SHORT).show();
            return false;
        }

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        String time = String.format("%02d:%02d:00", hour, minute);

        int checkedId = rgRepeatType.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Vui lòng chọn loại lặp lại", Toast.LENGTH_SHORT).show();
            return false;
        }
        RepeatType repeatType = checkedId == R.id.rbOnce ? RepeatType.ONCE :
                checkedId == R.id.rbDaily ? RepeatType.DAILY : RepeatType.INTERVAL;

        String repeatInterval = null;
        if (repeatType == RepeatType.INTERVAL) {
            repeatInterval = etRepeatInterval.getText().toString().trim();
            if (!isValidInterval(repeatInterval)) {
                etRepeatInterval.setError("Vui lòng nhập khoảng thời gian hợp lệ (HH:mm:ss)");
                return false;
            }
        }

        if (existingReminder != null) {
            ReminderUpdateRequest updateRequest = new ReminderUpdateRequest();
            updateRequest.setEnabled(true);
            updateRequest.setTime(time);
            updateRequest.setRepeatType(repeatType.toString());
            updateRequest.setRepeatInterval(repeatInterval);

            apiService.updateReminder(existingReminder.getId(), updateRequest).enqueue(new Callback<ApiResponse<ReminderResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<ReminderResponse>> call, Response<ApiResponse<ReminderResponse>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1000) {
                        ReminderResponse updatedReminder = response.body().getResult();
                        if (updatedReminder.getEnabled()) {
                            scheduleReminder(updatedReminder);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<ReminderResponse>> call, Throwable t) {
                    Log.e("SettingsActivity", "Failed to update reminder: " + t.getMessage());
                }
            });
        } else {
            ReminderCreationRequest createRequest = new ReminderCreationRequest(prefUtils.getUserId(), time, repeatType.toString(), repeatInterval);
            apiService.createReminder(createRequest).enqueue(new Callback<ApiResponse<ReminderResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<ReminderResponse>> call, Response<ApiResponse<ReminderResponse>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1000) {
                        ReminderResponse createdReminder = response.body().getResult();
                        if (createdReminder.getEnabled()) {
                            scheduleReminder(createdReminder);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<ReminderResponse>> call, Throwable t) {
                    Log.e("SettingsActivity", "Failed to create reminder: " + t.getMessage());
                }
            });
        }
        return true;
    }
    private boolean isValidInterval(String interval) {
        if (TextUtils.isEmpty(interval) || !interval.matches("\\d{2}:\\d{2}:\\d{2}")) {
            return false;
        }
        String[] parts = interval.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59 || seconds < 0 || seconds > 59) {
            return false;
        }
        if (hours == 0 && minutes == 0 && seconds == 0) {
            return false;
        }
        return true;
    }
    private void scheduleReminder(ReminderResponse reminder) {
        if (!reminder.getEnabled()) {
            Log.w("SettingsActivity", "Nhắc nhở không được bật, không lên lịch: id=" + reminder.getId());
            return;
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("repeat_type", reminder.getRepeatType());
        intent.putExtra("repeat_interval", reminder.getRepeatInterval());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        try {
            String[] timeParts = reminder.getTime().split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            long triggerTime = calendar.getTimeInMillis();
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi đặt lịch nhắc nhở", Toast.LENGTH_SHORT).show();
        }
    }
    private void cancelReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
        Log.d("SettingsActivity", "Hủy nhắc nhở");
    }

    //Daily Goal
    private void showDailyGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_set_daily_goal, null);
        builder.setView(dialogView);

        RadioGroup rgDailyGoal = dialogView.findViewById(R.id.rgDailyGoal);
        EditText etCustomDailyGoal = dialogView.findViewById(R.id.etCustomDailyGoal);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        rgDailyGoal.setOnCheckedChangeListener((group, checkedId) ->
                etCustomDailyGoal.setVisibility(checkedId == R.id.rbCustom ? View.VISIBLE : View.GONE));

        apiService.getUserById(prefUtils.getUserId()).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    Toast.makeText(SettingsActivity.this, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
                    return;
                }

                int currentDailyGoal = response.body().getResult().getDailyGoal();
                setupDailyGoalUI(rgDailyGoal, etCustomDailyGoal, currentDailyGoal);

                AlertDialog dialog = builder.create();
                dialog.show();

                btnSave.setOnClickListener(v -> {
                    int dailyGoal = getSelectedDailyGoal(rgDailyGoal, etCustomDailyGoal);
                    if (dailyGoal == currentDailyGoal) {
                        dialog.dismiss();
                        return;
                    }
                    updateDailyGoal(dailyGoal);
                    dialog.dismiss();
                });

                btnCancel.setOnClickListener(v -> dialog.dismiss());
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDailyGoalUI(RadioGroup rgDailyGoal, EditText etCustomDailyGoal, int currentDailyGoal) {
        switch (currentDailyGoal) {
            case 5:
                rgDailyGoal.check(R.id.rbFive);
                break;
            case 8:
                rgDailyGoal.check(R.id.rbEight);
                break;
            case 10:
                rgDailyGoal.check(R.id.rbTen);
                break;
            default:
                rgDailyGoal.check(R.id.rbCustom);
                etCustomDailyGoal.setVisibility(View.VISIBLE);
                etCustomDailyGoal.setText(String.valueOf(currentDailyGoal));
        }
    }

    private int getSelectedDailyGoal(RadioGroup rgDailyGoal, EditText etCustomDailyGoal) {
        int checkedId = rgDailyGoal.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(SettingsActivity.this, "Vui lòng chọn mục tiêu", Toast.LENGTH_SHORT).show();
            return -1;
        }

        if (checkedId == R.id.rbFive) return 5;
        if (checkedId == R.id.rbEight) return 8;
        if (checkedId == R.id.rbTen) return 10;

        String input = etCustomDailyGoal.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            etCustomDailyGoal.setError("Vui lòng nhập số từ");
            return -1;
        }

        try {
            int dailyGoal = Integer.parseInt(input);
            if (dailyGoal <= 0) {
                etCustomDailyGoal.setError("Mục tiêu phải lớn hơn 0");
                return -1;
            }
            return dailyGoal;
        } catch (NumberFormatException e) {
            etCustomDailyGoal.setError("Vui lòng nhập số hợp lệ");
            return -1;
        }
    }

    private void updateDailyGoal(int dailyGoal) {
        UserUpdateRequest user = new UserUpdateRequest();
        user.setDailyGoal(dailyGoal);
        apiService.updateUser(prefUtils.getUserId(), user).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                String message = response.isSuccessful() && response.body() != null
                        ? "Đã đặt mục tiêu: " + dailyGoal + " từ/ngày"
                        : "Lỗi khi lưu mục tiêu";
                Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //Leared Word
    private void showLearnedWordsFragment() {
        scrollViewSettings.setVisibility(View.GONE);
        findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
        textViewTitle.setText("Từ đã thuộc");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, new LearnedWordsFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //My Lesson
    private void showMyLessonFragment() {
        scrollViewSettings.setVisibility(View.GONE);
        findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
        textViewTitle.setText("Bài học của tôi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, new MyLessonFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //TeacherStats
    private void showTeacherStatsFragment() {
        scrollViewSettings.setVisibility(View.GONE);
        findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
        textViewTitle.setText("Thống kê học sinh");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, new TeacherStatisticsFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //Logout
    private void logout() {
        LogoutRequest request = new LogoutRequest();
        request.setToken(prefUtils.getToken());
        apiService.logout(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("SettingsActivity", "Đăng xuất thành công");
                    Toast.makeText(SettingsActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    prefUtils.clearUser();
                    cancelReminder();
                    navigateToMainActivity();
                } else {
                    Log.e("SettingsActivity", "Đăng xuất thất bại: " + (response.errorBody() != null ? response.errorBody().toString() : "Không có phản hồi"));
                    Toast.makeText(SettingsActivity.this, "Lỗi đăng xuất", Toast.LENGTH_SHORT).show();
                    prefUtils.clearUser();
                    cancelReminder();
                    navigateToMainActivity();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("SettingsActivity", "Lỗi mạng khi đăng xuất: " + t.getMessage());
                Toast.makeText(SettingsActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                prefUtils.clearUser();
                navigateToMainActivity();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.fragmentContainer).getVisibility() == View.VISIBLE) {
            getSupportFragmentManager().popBackStack();
            textViewTitle.setText("Cài đặt");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            scrollViewSettings.setVisibility(View.VISIBLE);
            findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private boolean checkLoginBeforeAction() {
        if (!prefUtils.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để sử dụng chức năng này", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
package com.example.honda_english.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.honda_english.R;
import com.example.honda_english.api.ApiService;
import com.example.honda_english.api.RetrofitClient;
import com.example.honda_english.model.ApiResponse;
import com.example.honda_english.model.Question.CheckAnswerRequest;
import com.example.honda_english.model.Question.CheckAnswerResponse;
import com.example.honda_english.model.Question.TrueFalseQuestionResponse;
import com.example.honda_english.util.enums.QuestionType;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizMultipleChoiceActivity extends AppCompatActivity {
    private TextView tvEnglishWord, tvQuestionCount;
    private Button[] btnOptions;
    private ApiService apiService;
    private String userId;
    private long wordId;
    private String word;
    private ArrayList<String> options;
    private int quizType, questionIndex, correctAnswersCount;
    private boolean isTestMode;
    private Long categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_multiple_choice);

        tvEnglishWord = findViewById(R.id.tvEnglishWord);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);

        btnOptions = new Button[]{
                findViewById(R.id.btnAnswer1),
                findViewById(R.id.btnAnswer2),
                findViewById(R.id.btnAnswer3),
                findViewById(R.id.btnAnswer4),
                findViewById(R.id.btnAnswer5),
                findViewById(R.id.btnAnswer6)
        };

        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        wordId = intent.getLongExtra("WORD_ID", -1);
        word = intent.getStringExtra("WORD");
        options = intent.getStringArrayListExtra("OPTIONS");
        //totalQuizCount = intent.getIntExtra("TOTAL_QUIZ_COUNT", 1);
        quizType = intent.getIntExtra("QUIZ_TYPE", 4);
        questionIndex = intent.getIntExtra("QUESTION_INDEX", 1);

        isTestMode = intent.getBooleanExtra("IS_TEST_MODE", false);
        categoryId = intent.getLongExtra("CATEGORY_ID", -1);
        correctAnswersCount = intent.getIntExtra("CORRECT_ANSWERS_COUNT", 0);

        if (options == null || options.size() != quizType) {
            Log.e("API", "Lỗi: Số lượng đáp án không phù hợp với QUIZ_TYPE");
            Toast.makeText(this, "Lỗi: Số lượng đáp án không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvEnglishWord.setText(word);
        tvQuestionCount.setText("Câu " + questionIndex + "/15");

        findViewById(R.id.imgBack).setOnClickListener(v -> finish());

        for (int i = 0; i < btnOptions.length; i++) {
            if (i < quizType) {
                btnOptions[i].setText(options.get(i));
                btnOptions[i].setVisibility(View.VISIBLE);
                final int index = i;
                btnOptions[i].setOnClickListener(v -> checkAnswer(index));
            } else {
                btnOptions[i].setVisibility(View.GONE);
            }
        }
    }

    private void checkAnswer(int selectedIndex) {
        setButtonsEnabled(false);

        CheckAnswerRequest request = new CheckAnswerRequest();
        request.setUserId(userId);
        request.setWordId(wordId);
        request.setAnswer(String.valueOf(selectedIndex));
        request.setQuestionType(QuestionType.MULTIPLE_CHOICE.name());

        apiService.checkAnswer(request).enqueue(new Callback<ApiResponse<CheckAnswerResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CheckAnswerResponse>> call, Response<ApiResponse<CheckAnswerResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    CheckAnswerResponse result = response.body().getResult();
                    boolean isCorrect = result.isCorrect();

                    if (isCorrect) {
                        setButtonColor(selectedIndex, true);
                    } else {
                        try {
                            int correctIndex = Integer.parseInt(result.getCorrectAnswer());
                            setButtonColor(correctIndex, true);
                        } catch (Exception e) {
                            Log.e("API", "Lỗi parse correctAnswer: " + e.getMessage());
                        }
                        setButtonColor(selectedIndex, false);
                    }

                    if (isTestMode) {
                        if (isCorrect) correctAnswersCount++;
                        questionIndex++;

                        btnOptions[0].postDelayed(() -> {
                            if (questionIndex <= 15) fetchNextTrueFalseQuestion();
                        }, 500);
                    } else {
                        btnOptions[0].postDelayed(() -> {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("IS_CORRECT", isCorrect);
                            resultIntent.putExtra("CORRECT_ANSWER", result.getCorrectAnswer());
                            resultIntent.putExtra("USER_ANSWER", result.getUserAnswer() != null ? result.getUserAnswer() : String.valueOf(selectedIndex));
                            resultIntent.putExtra("WORD", word);
                            resultIntent.putExtra("OPTIONS", options);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }, 1000);
                    }
                } else {
                    logApiError(response);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CheckAnswerResponse>> call, Throwable t) {
                Toast.makeText(QuizMultipleChoiceActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void fetchNextTrueFalseQuestion() {
        apiService.getTrueFalseQuestion(categoryId, userId, false).enqueue(new Callback<ApiResponse<TrueFalseQuestionResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TrueFalseQuestionResponse>> call, Response<ApiResponse<TrueFalseQuestionResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    TrueFalseQuestionResponse question = response.body().getResult();
                    Intent intent = new Intent(QuizMultipleChoiceActivity.this, QuizTrueFalseActivity.class);
                    intent.putExtra("WORD_ID", question.getWordId());
                    intent.putExtra("WORD", question.getWord());
                    intent.putExtra("DISPLAYED_MEANING", question.getDisplayedMeaning());
                    intent.putExtra("USER_ID", userId);
                    intent.putExtra("QUIZ_TYPE", 2);
                    intent.putExtra("IS_TEST_MODE", true);
                    intent.putExtra("CATEGORY_ID", categoryId);
                    intent.putExtra("QUESTION_INDEX", questionIndex);
                    intent.putExtra("CORRECT_ANSWERS_COUNT", correctAnswersCount);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(QuizMultipleChoiceActivity.this, "Lỗi khi lấy câu hỏi", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TrueFalseQuestionResponse>> call, Throwable t) {
                Toast.makeText(QuizMultipleChoiceActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setButtonColor(int index, boolean isCorrect) {
        if (index < 0 || index >= btnOptions.length) return;
        btnOptions[index].setBackgroundTintList(ColorStateList.valueOf(isCorrect ? Color.GREEN : Color.RED));
    }

    private void setButtonsEnabled(boolean enabled) {
        for (int i = 0; i < quizType; i++) {
            btnOptions[i].setEnabled(enabled);
        }
    }

    private void logApiError(Response<?> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Lỗi không xác định";
            Log.e("API", "Lỗi: " + errorBody);
        } catch (Exception e) {
            Log.e("API", "Không đọc được errorBody: " + e.getMessage());
        }
    }
}

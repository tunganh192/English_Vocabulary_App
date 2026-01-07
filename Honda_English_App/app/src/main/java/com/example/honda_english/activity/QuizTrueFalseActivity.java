package com.example.honda_english.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.honda_english.R;
import com.example.honda_english.fragment.CompletionFragment;
import com.example.honda_english.api.ApiService;
import com.example.honda_english.api.RetrofitClient;
import com.example.honda_english.model.ApiResponse;
import com.example.honda_english.model.Question.CheckAnswerRequest;
import com.example.honda_english.model.Question.CheckAnswerResponse;
import com.example.honda_english.model.Question.MultipleChoiceQuestionResponse;
import com.example.honda_english.util.enums.QuestionType;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizTrueFalseActivity extends AppCompatActivity {
    private TextView tvEnglishWord, tvVietnameseMeaning, tvQuestionCount;
    private ImageView imgCorrect, imgWrong;
    private ApiService apiService;
    private String userId, word, displayedMeaning, correctMeaning;
    private long wordId;
    private boolean isTestMode;
    private Long categoryId;
    private int questionIndex, correctAnswersCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_true_false);

        initViews();
        setupDataFromIntent();
        if (displayedMeaning == null) {
            finish();
            return;
        }

        tvQuestionCount.setText("Câu " + questionIndex + "/15");
        findViewById(R.id.imgBack).setOnClickListener(v -> finish());
        setupQuiz();
    }

    private void initViews() {
        tvEnglishWord = findViewById(R.id.tvEnglishWord);
        tvVietnameseMeaning = findViewById(R.id.tvVietnameseMeaning);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        imgCorrect = findViewById(R.id.imgCorrect);
        imgWrong = findViewById(R.id.imgWrong);
        apiService = RetrofitClient.getClient(this).create(ApiService.class);
    }

    private void setupDataFromIntent() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        wordId = intent.getLongExtra("WORD_ID", -1);
        word = intent.getStringExtra("WORD");
        displayedMeaning = intent.getStringExtra("DISPLAYED_MEANING");

        questionIndex = intent.getIntExtra("QUESTION_INDEX", 1);

        isTestMode = intent.getBooleanExtra("IS_TEST_MODE", false);
        categoryId = intent.getLongExtra("CATEGORY_ID", -1);
        correctAnswersCount = intent.getIntExtra("CORRECT_ANSWERS_COUNT", 0);
    }

    private void setupQuiz() {
        tvEnglishWord.setText(word);
        tvVietnameseMeaning.setText(displayedMeaning);
        imgCorrect.setOnClickListener(v -> checkAnswer(true));
        imgWrong.setOnClickListener(v -> checkAnswer(false));
    }

    private void checkAnswer(boolean userChoseCorrect) {
        imgCorrect.setEnabled(false);
        imgWrong.setEnabled(false);

        CheckAnswerRequest request = new CheckAnswerRequest();
        request.setUserId(userId);
        request.setWordId(wordId);
        request.setQuestionType(QuestionType.TRUE_FALSE.name());
        request.setAnswer(userChoseCorrect ? displayedMeaning : "");

        apiService.checkAnswer(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<CheckAnswerResponse>> call, Response<ApiResponse<CheckAnswerResponse>> res) {
                if (res.isSuccessful() && res.body() != null && res.body().getResult() != null) {
                    CheckAnswerResponse result = res.body().getResult();
                    boolean isUserCorrect = result.isCorrect();
                    String userAnswer = result.getUserAnswer() != null ? result.getUserAnswer() : request.getAnswer();

                    updateAnswerUI(userChoseCorrect, isUserCorrect);

                    if (isTestMode) {
                        if (isUserCorrect) correctAnswersCount++;
                        questionIndex++;
                        imgCorrect.postDelayed(() -> {
                            if (questionIndex < 15) fetchNextQuestion();
                            else showCompletionFragment();
                        }, 500);
                    } else {
                        imgCorrect.postDelayed(() -> {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("WORD", word);
                            resultIntent.putExtra("CORRECT_ANSWER", result.getCorrectAnswer());
                            resultIntent.putExtra("USER_ANSWER", userAnswer);
                            resultIntent.putExtra("USER_CHOICE", userChoseCorrect ? "TRUE" : "FALSE");
                            resultIntent.putExtra("IS_CORRECT", isUserCorrect);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }, 500);
                    }
                } else {
                    showError(res);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CheckAnswerResponse>> call, Throwable t) {
                Toast.makeText(QuizTrueFalseActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateAnswerUI(boolean userChoseCorrect, boolean isCorrect) {
        imgCorrect.setBackgroundTintList(null);
        imgWrong.setBackgroundTintList(null);

        if (userChoseCorrect == isCorrect) {
            ImageView target = userChoseCorrect ? imgCorrect : imgWrong;
            target.setImageResource(userChoseCorrect ? R.drawable.tick3 : R.drawable.x);
            target.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        } else {
            imgCorrect.setImageResource(userChoseCorrect ? R.drawable.tickfalse : R.drawable.tickfalse);
            imgWrong.setImageResource(userChoseCorrect ? R.drawable.xtrue : R.drawable.xtrue);

            imgCorrect.setBackgroundTintList(ColorStateList.valueOf(userChoseCorrect ? Color.RED : Color.GREEN));
            imgWrong.setBackgroundTintList(ColorStateList.valueOf(userChoseCorrect ? Color.GREEN : Color.RED));
        }
    }

    private void showCompletionFragment() {
        findViewById(R.id.toolbar).setVisibility(View.GONE);
        findViewById(R.id.tvEnglishWord).setVisibility(View.GONE);
        findViewById(R.id.tvVietnameseMeaning).setVisibility(View.GONE);
        findViewById(R.id.imgCorrect).setVisibility(View.GONE);
        findViewById(R.id.imgWrong).setVisibility(View.GONE);

        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
        fragmentContainer.setVisibility(View.VISIBLE);

        CompletionFragment fragment = new CompletionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("CORRECT_ANSWERS", correctAnswersCount);
        bundle.putInt("TOTAL_QUESTIONS", 15);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void fetchNextQuestion() {
        int quizType = new Random().nextBoolean() ? 4 : 6;
        Call<ApiResponse<MultipleChoiceQuestionResponse>> call = quizType == 4 ?
                apiService.generateFourOptionsQuestions(categoryId, userId, false) :
                apiService.generateSixOptionsQuestion(categoryId, userId, false);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<MultipleChoiceQuestionResponse>> call, Response<ApiResponse<MultipleChoiceQuestionResponse>> res) {
                if (res.isSuccessful() && res.body() != null && res.body().getResult() != null) {
                    MultipleChoiceQuestionResponse q = res.body().getResult();
                    Intent intent = new Intent(QuizTrueFalseActivity.this, QuizMultipleChoiceActivity.class);
                    intent.putExtra("WORD_ID", q.getWordId());
                    intent.putExtra("WORD", q.getWord());
                    intent.putExtra("OPTIONS", new ArrayList<>(q.getOptions()));
                    intent.putExtra("TOTAL_QUIZ_COUNT", questionIndex);
                    intent.putExtra("USER_ID", userId);
                    intent.putExtra("QUIZ_TYPE", quizType);

                    intent.putExtra("IS_TEST_MODE", true);
                    intent.putExtra("CATEGORY_ID", categoryId);
                    intent.putExtra("QUESTION_INDEX", questionIndex);
                    intent.putExtra("CORRECT_ANSWERS_COUNT", correctAnswersCount);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(QuizTrueFalseActivity.this, "Lỗi khi lấy câu hỏi", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MultipleChoiceQuestionResponse>> call, Throwable t) {
                Toast.makeText(QuizTrueFalseActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showError(Response<?> response) {
        try {
            String error = response.errorBody() != null ? response.errorBody().string() : "Lỗi không xác định";
            Log.e("API", "Lỗi: " + error);
        } catch (Exception e) {
            Log.e("API", "Không đọc được lỗi: " + e.getMessage());
        }
        finish();
    }
}

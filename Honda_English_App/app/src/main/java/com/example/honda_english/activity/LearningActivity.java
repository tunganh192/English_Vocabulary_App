package com.example.honda_english.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.honda_english.R;
import com.example.honda_english.api.ApiService;
import com.example.honda_english.api.RetrofitClient;
import com.example.honda_english.model.*;
import com.example.honda_english.model.LearnedWord.*;
import com.example.honda_english.model.Question.*;
import com.example.honda_english.model.Word.*;
import com.example.honda_english.fragment.CompletionFragment;
import com.example.honda_english.util.PrefUtils;
import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LearningActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private TextView tvEnglishWord, tvVietnameseMeaning, txtRemember;
    private ImageView btnRemember, btnSpeaker;
    private ProgressBar progressBarLoading;
    LinearLayout layoutWord, layoutRemember;

    private List<Word> wordList = new ArrayList<>();
    private ApiService apiService;
    private PrefUtils prefUtils;
    private TextToSpeech tts;
    private SharedPreferences sharedPreferences;

    private int currentIndex = 0, wordsLearnedCount = 0, questionIndex = 0, correctAnswers = 0, quizType = 2;
    private static final int REQUIRED_QUIZ_COUNT = 15;
    private ActivityResultLauncher<Intent> quizActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handleQuizResult(result.getData());
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        prefUtils = new PrefUtils(this);
        sharedPreferences = getSharedPreferences("TTS_Prefs", MODE_PRIVATE);
        apiService = RetrofitClient.getClient(this).create(ApiService.class);
        tts = new TextToSpeech(this, this);

        initViews();
        progressBarLoading.setVisibility(View.VISIBLE);
        layoutWord.setVisibility(View.GONE);
        layoutRemember.setVisibility(View.GONE);


        findViewById(R.id.imgBack).setOnClickListener(v -> finish());
        btnSpeaker.setOnClickListener(v -> speakCurrentWord());
        btnRemember.setOnClickListener(v -> markAsRemembered());

    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS && tts.setLanguage(Locale.US) >= 0) {
            long categoryId = getIntent().getLongExtra("CATEGORY_ID", -1);
            progressBarLoading.setVisibility(View.GONE);
            layoutWord.setVisibility(View.VISIBLE);
            layoutRemember.setVisibility(View.VISIBLE);
            loadWords(categoryId);
        } else {
            showToast("Bạn cần tải ngôn ngữ để xử dụng chức năng này!");
        }
    }
    
    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    private void initViews() {
        tvEnglishWord = findViewById(R.id.tvEnglishWord);
        tvVietnameseMeaning = findViewById(R.id.tvVietnameseMeaning);
        txtRemember = findViewById(R.id.txtRemember);
        btnRemember = findViewById(R.id.btnRemember);
        btnSpeaker = findViewById(R.id.btnSpeaker);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        layoutWord = findViewById(R.id.layoutWord);
        layoutRemember = findViewById(R.id.layoutRemember);

    }

    private void loadWords(long categoryId) {
        apiService.getWordsByCategory(categoryId, 1, 100).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<List<WordResponse>>>> call, Response<ApiResponse<PageResponse<List<WordResponse>>>> res) {
                if (res.isSuccessful() && res.body() != null) {
                    for (WordResponse w : res.body().getResult().getItems()) {
                        wordList.add(new Word(w.getEnglishWord(), w.getVietnameseMeaning(), w.getId(), w.getPronunciation()));
                    }
                    wordList.sort(Comparator.comparingLong(Word::getId));
                    showWord(0);
                } else {
                    showToast("Error loading words");
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<List<WordResponse>>>> call, Throwable t) {
                showToast("Network error: " + t.getMessage());
                finish();
            }
        });
    }

    private void markAsRemembered() {
        Word word = wordList.get(currentIndex);
        wordsLearnedCount++;
        apiService.createLearnedWord(new LearnedWordCreationRequest(prefUtils.getUserId(), word.getId())).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<LearnedWordResponse>> call, Response<ApiResponse<LearnedWordResponse>> res) {
                moveToNextWordOrQuiz();
            }

            @Override
            public void onFailure(Call<ApiResponse<LearnedWordResponse>> call, Throwable t) {
            }
        });
    }

    private void moveToNextWordOrQuiz() {
        currentIndex = (currentIndex + 1) % wordList.size();
        if (wordsLearnedCount % 2 == 0) {
            showQuiz(getIntent().getLongExtra("CATEGORY_ID", -1), prefUtils.getUserId());
        } else {
            showWord(currentIndex);
        }
    }

    private void showWord(int index) {
        Word word = wordList.get(index);
        tvEnglishWord.setText(word.getEnglish());
        tvVietnameseMeaning.setText(word.getVietnamese());
        txtRemember.setText("Đã nhớ!");
        speak(word.getEnglish());
    }

    private void speakCurrentWord() {
        if (!wordList.isEmpty())
            speak(wordList.get(currentIndex).getEnglish());
    }

    private void speak(String text) {
        float rate = sharedPreferences.getInt("speechRate", 100) / 100f;
        float pitch = sharedPreferences.getInt("pitch", 100) / 100f;
        tts.setSpeechRate(rate);
        tts.setPitch(pitch);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void showQuiz(long categoryId, String userId) {
        if (questionIndex >= REQUIRED_QUIZ_COUNT) {
            showCompletionScreen();
            return;
        }

        switch (quizType) {
            case 2:
                apiService.getTrueFalseQuestion(categoryId, userId, false)
                        .enqueue(new Callback<ApiResponse<TrueFalseQuestionResponse>>() {
                            @Override
                            public void onResponse(Call<ApiResponse<TrueFalseQuestionResponse>> call, Response<ApiResponse<TrueFalseQuestionResponse>> response) {
                                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                                    TrueFalseQuestionResponse question = response.body().getResult();
                                    Intent intent = new Intent(LearningActivity.this, QuizTrueFalseActivity.class);
                                    intent.putExtra("USER_ID", userId);
                                    intent.putExtra("WORD_ID", question.getWordId());
                                    intent.putExtra("WORD", question.getWord());
                                    intent.putExtra("DISPLAYED_MEANING", question.getDisplayedMeaning());
                                    intent.putExtra("QUESTION_INDEX", questionIndex + 1);
                                    quizActivityResultLauncher.launch(intent);
                                    quizType = 4;
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResponse<TrueFalseQuestionResponse>> call, Throwable t) {
                                showToast("Lỗi mạng. Vui lòng thử lại ");
                            }
                        });
                break;

            case 4:
                apiService.generateFourOptionsQuestions(categoryId, userId, false)
                        .enqueue(new Callback<ApiResponse<MultipleChoiceQuestionResponse>>() {
                            @Override
                            public void onResponse(Call<ApiResponse<MultipleChoiceQuestionResponse>> call, Response<ApiResponse<MultipleChoiceQuestionResponse>> response) {
                                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                                    MultipleChoiceQuestionResponse question = response.body().getResult();
                                    Intent intent = new Intent(LearningActivity.this, QuizMultipleChoiceActivity.class);
                                    intent.putExtra("USER_ID", userId);
                                    intent.putExtra("WORD_ID", question.getWordId());
                                    intent.putExtra("WORD", question.getWord());
                                    intent.putExtra("OPTIONS", new ArrayList<>(question.getOptions()));
                                    intent.putExtra("QUESTION_INDEX", questionIndex + 1);
                                    intent.putExtra("QUIZ_TYPE", quizType);
                                    quizActivityResultLauncher.launch(intent);
                                    quizType = 6;
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResponse<MultipleChoiceQuestionResponse>> call, Throwable t) {
                                showToast("Lỗi mạng. Vui lòng thử lại ");
                            }
                        });
                break;

            case 6:
                apiService.generateSixOptionsQuestion(categoryId, userId, false)
                        .enqueue(new Callback<ApiResponse<MultipleChoiceQuestionResponse>>() {
                            @Override
                            public void onResponse(Call<ApiResponse<MultipleChoiceQuestionResponse>> call, Response<ApiResponse<MultipleChoiceQuestionResponse>> response) {
                                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                                    MultipleChoiceQuestionResponse question = response.body().getResult();
                                    Intent intent = new Intent(LearningActivity.this, QuizMultipleChoiceActivity.class);
                                    intent.putExtra("WORD_ID", question.getWordId());
                                    intent.putExtra("WORD", question.getWord());
                                    intent.putExtra("OPTIONS", new ArrayList<>(question.getOptions()));
                                    intent.putExtra("QUESTION_INDEX", questionIndex + 1);
                                    intent.putExtra("USER_ID", userId);
                                    intent.putExtra("QUIZ_TYPE", quizType);
                                    quizActivityResultLauncher.launch(intent);
                                    quizType = 2;
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResponse<MultipleChoiceQuestionResponse>> call, Throwable t) {
                                showToast("Lỗi mạng. Vui lòng thử lại ");
                            }
                        });
                break;
        }
    }

    private void showCompletionScreen() {
        CompletionFragment fragment = new CompletionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("CORRECT_ANSWERS", correctAnswers);
        bundle.putInt("TOTAL_QUESTIONS", REQUIRED_QUIZ_COUNT);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).addToBackStack(null).commit();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void resetLearningSession() {
        currentIndex = 0;
        wordsLearnedCount = 0;
        questionIndex = 0;
        correctAnswers = 0;
        quizType = 2;
        Collections.shuffle(wordList);
        showWord(currentIndex);
    }

    private void handleQuizResult(Intent data) {
        questionIndex++;
        boolean correct = data.getBooleanExtra("IS_CORRECT", false);

        if (correct) {
            correctAnswers++;
            currentIndex = (currentIndex + 1) % wordList.size();
            showWord(currentIndex);
        } else {
            displayWrongAnswer(data);
        }

        if (questionIndex >= REQUIRED_QUIZ_COUNT) showCompletionScreen();
    }

    private void displayWrongAnswer(Intent data) {
        String word = data.getStringExtra("WORD");
        String correctAnswer = data.getStringExtra("CORRECT_ANSWER");
        String userAnswer = data.getStringExtra("USER_ANSWER");
        String userChoice = data.getStringExtra("USER_CHOICE");
        ArrayList<String> options = data.getStringArrayListExtra("OPTIONS");

        tvEnglishWord.setText(word);
        speak(word);

        if (options != null) {
            try {
                int correctId = Integer.parseInt(correctAnswer);
                int userId = Integer.parseInt(userAnswer);
                tvVietnameseMeaning.setText(options.get(correctId));
                txtRemember.setText("Bạn đã chọn: " + options.get(userId));
            } catch (Exception e) {
                showToast("Error parsing answer");
            }
        } else {
            tvVietnameseMeaning.setText(correctAnswer);
            txtRemember.setText("Bạn đã chọn: " + userChoice);
        }
    }
}

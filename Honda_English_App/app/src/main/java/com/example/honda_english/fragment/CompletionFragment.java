package com.example.honda_english.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.honda_english.R;
import com.example.honda_english.activity.LearningActivity;
import com.example.honda_english.activity.QuizTrueFalseActivity;
import com.example.honda_english.activity.SettingsActivity;
import com.example.honda_english.util.PrefUtils;
import com.example.honda_english.api.ApiService;
import com.example.honda_english.api.RetrofitClient;
import com.example.honda_english.model.ApiResponse;
import com.example.honda_english.model.Question.TrueFalseQuestionResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletionFragment extends Fragment {
    private TextView tvCongrats, tvScore;
    private ImageView imgCelebration;
    private Button btnContinue, btnBack;
    private ApiService apiService;
    private PrefUtils prefUtils;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completion, container, false);

        tvCongrats = view.findViewById(R.id.tvCongrats);
        tvScore = view.findViewById(R.id.tvScore);
        imgCelebration = view.findViewById(R.id.imgCelebration);
        btnContinue = view.findViewById(R.id.btnContinue);
        btnBack = view.findViewById(R.id.btnBack);

        apiService = RetrofitClient.getClient(getContext()).create(ApiService.class);
        prefUtils = new PrefUtils(getContext());

        Bundle bundle = getArguments();
        int correctAnswers = 0;
        int totalQuestions = 15;
        if (bundle != null) {
            correctAnswers = bundle.getInt("CORRECT_ANSWERS", 0);
            totalQuestions = bundle.getInt("TOTAL_QUESTIONS", 15);
            tvScore.setText(correctAnswers + "/" + totalQuestions);
        } else {
            tvScore.setText("0/15");
        }

        boolean isTestMode = getActivity() != null && getActivity().getIntent() != null
                && getActivity().getIntent().getBooleanExtra("IS_TEST_MODE", false);
        long categoryId = getActivity() != null && getActivity().getIntent() != null
                ? getActivity().getIntent().getLongExtra("CATEGORY_ID", -1) : -1L;

        btnContinue.setOnClickListener(v -> {
            if (getActivity() != null) {
                if (isTestMode) {
                    apiService.getTrueFalseQuestion(categoryId, prefUtils.getUserId(), false).enqueue(new Callback<ApiResponse<TrueFalseQuestionResponse>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<TrueFalseQuestionResponse>> call, Response<ApiResponse<TrueFalseQuestionResponse>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                                TrueFalseQuestionResponse question = response.body().getResult();
                                Intent intent = new Intent(getContext(), QuizTrueFalseActivity.class);
                                intent.putExtra("WORD_ID", question.getWordId());
                                intent.putExtra("WORD", question.getWord());
                                intent.putExtra("DISPLAYED_MEANING", question.getDisplayedMeaning());
                                intent.putExtra("TOTAL_QUIZ_COUNT", 1);
                                intent.putExtra("USER_ID", prefUtils.getUserId());
                                intent.putExtra("QUIZ_TYPE", 2);
                                intent.putExtra("IS_TEST_MODE", true);
                                intent.putExtra("CATEGORY_ID", categoryId);
                                intent.putExtra("CURRENT_QUESTION_COUNT", 1); // Bắt đầu từ 1
                                intent.putExtra("CORRECT_ANSWERS_COUNT", 0);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "Lỗi khi lấy câu hỏi", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<TrueFalseQuestionResponse>> call, Throwable t) {
                        }
                    });
                } else if (getActivity() instanceof LearningActivity) {
                    ((LearningActivity) getActivity()).resetLearningSession();
                    getParentFragmentManager().popBackStack();
                }
            }
        });

        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                Intent intent;
                if (isTestMode) {
                    intent = new Intent(getActivity(), SettingsActivity.class);
                } else {
                    intent = new Intent(getActivity(), LearningActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }
}
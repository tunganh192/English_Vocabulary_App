package com.example.honda_english.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.honda_english.R;
import com.example.honda_english.util.PrefUtils;
import com.example.honda_english.adapter.VocabularyAdapter;
import com.example.honda_english.api.ApiService;
import com.example.honda_english.api.RetrofitClient;
import com.example.honda_english.model.ApiResponse;
import com.example.honda_english.model.PageResponse;
import com.example.honda_english.model.Word.Word;
import com.example.honda_english.model.Word.WordResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LearnedWordsFragment extends Fragment {
    private RecyclerView recyclerView;
    private VocabularyAdapter adapter;
    private List<Word> learnedWordsList;
    private ApiService apiService;
    private PrefUtils prefUtils;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learned_words, container, false);

        prefUtils = new PrefUtils(getContext());

        setupToolbar();
        setupRecyclerView(view);

        apiService = RetrofitClient.getClient(getContext()).create(ApiService.class);
        fetchLearnedWords(1, 10);

        return view;
    }

    private void setupToolbar() {
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
            if (toolbarTitle != null) {
                toolbarTitle.setText("Từ đã thuộc");
            }
            toolbar.setNavigationIcon(R.drawable.back);
            toolbar.setNavigationOnClickListener(v -> returnToSettingsUI());
        }
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.rvLearnedWords);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        learnedWordsList = new ArrayList<>();
        adapter = new VocabularyAdapter(learnedWordsList);
        recyclerView.setAdapter(adapter);
    }

    private void returnToSettingsUI() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        View scrollView = requireActivity().findViewById(R.id.scrollViewSettings);
        View bottomNavigation = requireActivity().findViewById(R.id.bottomNavigation);
        View fragmentContainer = requireActivity().findViewById(R.id.fragmentContainer);
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);

        if (scrollView != null) {
            scrollView.setVisibility(View.VISIBLE);
        }
        if (bottomNavigation != null) {
            bottomNavigation.setVisibility(View.VISIBLE);
        }
        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.GONE);
        }
        if (toolbarTitle != null) {
            toolbarTitle.setText("Cài đặt");
        }

        toolbar.setNavigationIcon(null);
    }

    private void fetchLearnedWords(int pageNo, int pageSize) {
        String userId = prefUtils.getUserId();

        apiService.getLearnedWords(userId, pageNo, pageSize).enqueue(new Callback<ApiResponse<PageResponse<List<WordResponse>>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<List<WordResponse>>>> call, Response<ApiResponse<PageResponse<List<WordResponse>>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    PageResponse<List<WordResponse>> pageResponse = response.body().getResult();
                    List<WordResponse> wordResponses = pageResponse.getItems();

                    if (pageNo == 1) {
                        learnedWordsList.clear();
                    }
                    for (WordResponse wordResponse : wordResponses) {
                        Word word = new Word(
                                wordResponse.getEnglishWord(),
                                wordResponse.getVietnameseMeaning(),
                                wordResponse.getId(),
                                wordResponse.getPronunciation()
                        );
                        learnedWordsList.add(word);
                    }
                    if (pageNo < pageResponse.getTotalPages()){
                        fetchLearnedWords(pageNo + 1, pageSize);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi lấy từ đã học", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<List<WordResponse>>>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
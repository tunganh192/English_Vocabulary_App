package com.example.honda_english.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.example.honda_english.model.Word.DeleteWordsRequest;
import com.example.honda_english.model.Word.Word;
import com.example.honda_english.model.Word.WordCreationRequest;
import com.example.honda_english.model.Word.WordResponse;
import com.example.honda_english.model.PageResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VocabularyFragment extends Fragment {
    private RecyclerView recyclerView;
    private VocabularyAdapter adapter;
    private List<Word> wordList;
    private ApiService apiService;
    private FloatingActionButton fabAddWord;
    private FloatingActionButton fabDeleteWords;
    private long categoryId;
    private String categoryTitle;
    private PrefUtils prefUtils;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vocabulary, container, false);

        prefUtils = new PrefUtils(requireContext());

        userId = prefUtils.getUserId();

        Bundle args = getArguments();
        if (args != null) {
            categoryId = args.getLong("CATEGORY_ID");
            categoryTitle = args.getString("CATEGORY_TITLE");
        }

        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
            if (toolbarTitle != null) {
                toolbarTitle.setText("Từ vựng");
            }

            toolbar.setNavigationIcon(R.drawable.back);
            toolbar.setNavigationOnClickListener(v -> {
                navigateToMyLessonFragment();
            });
        }

        recyclerView = view.findViewById(R.id.recyclerViewWords);
        fabAddWord = view.findViewById(R.id.fabAddWord);
        fabDeleteWords = view.findViewById(R.id.fabDeleteWords);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        wordList = new ArrayList<>();
        adapter = new VocabularyAdapter(wordList);
        recyclerView.setAdapter(adapter);

        apiService = RetrofitClient.getClient(getContext()).create(ApiService.class);
        loadWordsFromApi();

        fabAddWord.setOnClickListener(v -> {
            showAddWordDialog();
        });

        fabDeleteWords.setOnClickListener(v -> {
            showDeleteWordsDialog();
        });

        return view;
    }

    private void showDeleteWordsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delete_words, null);
        builder.setView(dialogView);

        ListView lvWords = dialogView.findViewById(R.id.lvWords);
        CheckBox cbSelectAll = dialogView.findViewById(R.id.cbSelectAll);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        List<String> wordDisplay = new ArrayList<>();
        List<Long> wordIds = new ArrayList<>();
        for (Word word : wordList) {
            wordDisplay.add(word.getEnglish() + " - " + word.getVietnamese());
            wordIds.add(word.getId());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_multiple_choice, wordDisplay);
        lvWords.setAdapter(adapter);
        lvWords.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (int i = 0; i < lvWords.getCount(); i++) {
                lvWords.setItemChecked(i, isChecked);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        btnConfirm.setOnClickListener(v -> {
            SparseBooleanArray checkedItems = lvWords.getCheckedItemPositions();
            List<Long> selectedIds = new ArrayList<>();
            for (int i = 0; i < checkedItems.size(); i++) {
                if (checkedItems.valueAt(i)) {
                    selectedIds.add(wordIds.get(checkedItems.keyAt(i)));
                }
            }

            if (selectedIds.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất một từ vựng", Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.dismiss();
            showConfirmDeleteDialog(selectedIds);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void showConfirmDeleteDialog(List<Long> selectedIds) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa " + selectedIds.size() + " từ vựng? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialogInterface, which) -> deleteWords(selectedIds))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteWords(List<Long> selectedIds) {
        DeleteWordsRequest request = new DeleteWordsRequest(selectedIds);
        apiService.deactivateWords(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    wordList.removeIf(word -> selectedIds.contains(word.getId()));
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Đã xóa " + selectedIds.size() + " từ vựng", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi xóa từ vựng", Toast.LENGTH_SHORT).show();
                    Log.e("VocabularyFragment", "Deactivation failed: " + (response.body() != null ? response.body().getMessage() : response.message()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("VocabularyFragment", "Network error: " + t.getMessage());
            }
        });
    }
    private void navigateToMyLessonFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        if (fragmentManager.popBackStackImmediate(MyLessonFragment.class.getName(), 0)) {
            return;
        } else {
            MyLessonFragment myLessonFragment = new MyLessonFragment();
            Bundle args = new Bundle();
            args.putLong("CATEGORY_ID", categoryId);
            myLessonFragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, myLessonFragment)
                    .addToBackStack(MyLessonFragment.class.getName())
                    .commit();
        }
    }

    private void loadWordsFromApi() {
        wordList.clear();
        fetchWords(1, 10);
    }

    private void fetchWords(int pageNo, int pageSize) {
        apiService.getWordsByCategory(categoryId, pageNo, pageSize).enqueue(new Callback<ApiResponse<PageResponse<List<WordResponse>>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<List<WordResponse>>>> call, Response<ApiResponse<PageResponse<List<WordResponse>>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    PageResponse<List<WordResponse>> pageResponse = response.body().getResult();
                    List<WordResponse> words = pageResponse.getItems();

                    if (words != null && !words.isEmpty()) {
                        for (WordResponse wordResponse : words) {
                            wordList.add(new Word(
                                    wordResponse.getEnglishWord(),
                                    wordResponse.getVietnameseMeaning(),
                                    wordResponse.getId(),
                                    wordResponse.getPronunciation()
                            ));
                        }
                        adapter.notifyDataSetChanged();
                        if (pageNo < pageResponse.getTotalPages()) {
                            fetchWords(pageNo + 1, pageSize);
                        }
                    } else if (wordList.isEmpty()) {
                        Toast.makeText(getContext(), "Không có từ vựng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi khi lấy từ vựng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<List<WordResponse>>>> call, Throwable t) {
            }
        });
    }

    private void showAddWordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_word, null);
        builder.setView(dialogView);

        EditText etEnglishWord = dialogView.findViewById(R.id.etEnglishWord);
        EditText etPronunciation = dialogView.findViewById(R.id.etPronunciation);
        EditText etVietnameseMeaning = dialogView.findViewById(R.id.etVietnameseMeaning);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String englishWord = etEnglishWord.getText().toString().trim();
            String pronunciation = etPronunciation.getText().toString().trim();
            String vietnameseMeaning = etVietnameseMeaning.getText().toString().trim();

            if (pronunciation.isEmpty()) {
                pronunciation = null;
            }

            if (TextUtils.isEmpty(englishWord)) {
                etEnglishWord.setError("Vui lòng nhập từ tiếng Anh");
                return;
            }
            if (englishWord.length() > 100) {
                etEnglishWord.setError("Từ tiếng Anh tối đa 100 ký tự");
                return;
            }

            if (pronunciation != null && pronunciation.length() > 50) {
                etPronunciation.setError("Phiên âm tối đa 50 ký tự");
                return;
            }
            if (TextUtils.isEmpty(vietnameseMeaning)) {
                etVietnameseMeaning.setError("Vui lòng nhập nghĩa tiếng Việt");
                return;
            }
            if (vietnameseMeaning.length() > 100) {
                etVietnameseMeaning.setError("Nghĩa tiếng Việt tối đa 100 ký tự");
                return;
            }

            WordCreationRequest request = new WordCreationRequest();
            request.setEnglishWord(englishWord);
            request.setPronunciation(pronunciation);
            request.setVietnameseMeaning(vietnameseMeaning);
            request.setCategoryId(categoryId);
            request.setCreatedBy(userId);

            apiService.createWord(request).enqueue(new Callback<ApiResponse<WordResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<WordResponse>> call, Response<ApiResponse<WordResponse>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                        WordResponse newWord = response.body().getResult();
                        wordList.add(new Word(newWord.getEnglishWord(), newWord.getVietnameseMeaning(), newWord.getId(), newWord.getPronunciation()));
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Đã thêm từ vựng", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi thêm từ vựng", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<WordResponse>> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }
}
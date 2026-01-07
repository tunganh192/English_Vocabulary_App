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
import com.example.honda_english.adapter.FlashcardAdapter;
import com.example.honda_english.api.ApiService;
import com.example.honda_english.api.RetrofitClient;
import com.example.honda_english.model.ApiResponse;
import com.example.honda_english.model.Category.CategoryCreationRequest;
import com.example.honda_english.model.Category.CategoryResponse;
import com.example.honda_english.model.Category.DeleteCategoriesRequest;
import com.example.honda_english.model.Category.Flashcard;
import com.example.honda_english.model.PageResponse;
import com.example.honda_english.model.Statistic.WordCountResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyLessonFragment extends Fragment {

    private RecyclerView recyclerView;
    private FlashcardAdapter adapter;
    private List<Flashcard> flashcardList = new ArrayList<>();
    private ApiService apiService;
    private FloatingActionButton fabAddCategory, fabDeleteCategory;
    private PrefUtils prefUtils;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_lesson, container, false);
        prefUtils = new PrefUtils(requireContext());

        setupToolbar();
        initViews(view);
        setupApiService();
        loadFlashcardsFromApi();

        fabAddCategory.setOnClickListener(v -> {
            if (!checkUserRole("TEACHER", "Chỉ giáo viên mới có thể thêm bài học!")) return;
            showAddCategoryDialog();
        });

        fabDeleteCategory.setOnClickListener(v -> {
            if (!checkUserRole("TEACHER", "Chỉ giáo viên mới có thể xóa bài học!")) return;
            showDeleteCategoryDialog();
        });

        return view;
    }

    private void setupToolbar() {
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        if (toolbar == null) return;
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        if (toolbarTitle != null) toolbarTitle.setText("Bài học của tôi");
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(v -> returnToSettingsUI());
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewCategories);
        fabAddCategory = view.findViewById(R.id.fabAddCategory);
        fabDeleteCategory = view.findViewById(R.id.fabDeleteCategory);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FlashcardAdapter(flashcardList, this::openVocabularyFragment);
        recyclerView.setAdapter(adapter);
    }

    private void setupApiService() {
        apiService = RetrofitClient.getClient(getContext()).create(ApiService.class);
    }

    private void loadFlashcardsFromApi() {
        flashcardList.clear();
        fetchCategoriesRecursive(1, 10);
    }

    private void fetchCategoriesRecursive(int pageNo, int pageSize) {
        apiService.getCategoriesByCreator(prefUtils.getUserId(), pageNo, pageSize)
                .enqueue(new Callback<ApiResponse<PageResponse<List<CategoryResponse>>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PageResponse<List<CategoryResponse>>>> call, Response<ApiResponse<PageResponse<List<CategoryResponse>>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                            PageResponse<List<CategoryResponse>> page = response.body().getResult();
                            List<CategoryResponse> categories = page.getItems();
                            if (categories != null && !categories.isEmpty()) {
                                for (CategoryResponse category : categories) {
                                    Flashcard flashcard = new Flashcard(
                                            category.getId(),
                                            category.getName(),
                                            0,
                                            R.drawable.note1,
                                            category.getCode());
                                    flashcardList.add(flashcard);
                                    fetchWordCountForFlashcard(flashcard);
                                }
                            }
                            if (pageNo + 1 < page.getTotalPages()) {
                                fetchCategoriesRecursive(pageNo + 1, pageSize);
                            } else updateAdapter();
                        } else {
                            updateAdapter();
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<PageResponse<List<CategoryResponse>>>> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        updateAdapter();
                    }
                });
    }

    private void fetchWordCountForFlashcard(Flashcard flashcard) {
        apiService.getWordCountCategory(flashcard.getId())
                .enqueue(new Callback<ApiResponse<WordCountResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<WordCountResponse>> call, Response<ApiResponse<WordCountResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                            flashcard.setWordCount(response.body().getResult().getWordCount());
                            updateAdapter();
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<WordCountResponse>> call, Throwable t) {
                        Log.e("MyLessonFragment", "Lỗi lấy số từ: " + t.getMessage());
                        updateAdapter();
                    }
                });
    }

    private void updateAdapter() {
        flashcardList.sort(Comparator.comparingLong(Flashcard::getId));
        adapter.notifyDataSetChanged();
    }

    private void openVocabularyFragment(Flashcard flashcard) {
        VocabularyFragment fragment = new VocabularyFragment();
        Bundle args = new Bundle();
        args.putLong("CATEGORY_ID", flashcard.getId());
        args.putString("CATEGORY_TITLE", flashcard.getTitle());
        fragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(MyLessonFragment.class.getName())
                .commit();
    }

    private boolean checkUserRole(String role, String message) {
        if (!role.equals(prefUtils.getUserRole())) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.etCategoryName);
        EditText etCode = dialogView.findViewById(R.id.etCategoryCode);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String code = etCode.getText().toString().trim();

            if (!validateCategoryInput(name, code, etName, etCode)) return;

            CategoryCreationRequest request = new CategoryCreationRequest();
            request.setName(name);
            request.setCode(TextUtils.isEmpty(code) ? null : code);
            request.setCreatedBy(prefUtils.getUserId());
            request.setIconUrl("note1");

            apiService.createCategory(request).enqueue(new Callback<ApiResponse<CategoryResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<CategoryResponse>> call, Response<ApiResponse<CategoryResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<CategoryResponse> apiResponse = response.body();
                        CategoryResponse newCategory = apiResponse.getResult();
                        flashcardList.add(new Flashcard(
                                newCategory.getId(),
                                newCategory.getName(),
                                0,
                                R.drawable.note1,
                                newCategory.getCode()
                        ));
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Đã thêm danh mục", Toast.LENGTH_SHORT).show();
                    } else {
                            if (response.errorBody() != null) {
                                String errorJson = null;
                                try {
                                    errorJson = response.errorBody().string();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                ApiResponse errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                                if (errorResponse != null && errorResponse.getCode() == 2002) {
                                    Toast.makeText(getContext(), "Mã danh mục đã tồn tại, vui lòng nhập mã khác", Toast.LENGTH_SHORT).show();
                                }
                                if (errorResponse != null && errorResponse.getCode() == 2012) {
                                    Toast.makeText(getContext(), "Mã danh mục đã tồn tại, vui lòng nhập mã khác", Toast.LENGTH_SHORT).show();
                                }
                            }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<CategoryResponse>> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private boolean validateCategoryInput(String name, String code, EditText etName, EditText etCode) {
        if (TextUtils.isEmpty(name)) {
            etName.setError("Vui lòng nhập tên danh mục");
            return false;
        }
        if (name.length() > 100) {
            etName.setError("Tên danh mục tối đa 100 ký tự");
            return false;
        }
        if (TextUtils.isEmpty(code)) {
            etCode.setError("Vui lòng nhập mã danh mục");
            return false;
        }
        if (code.length() < 5 || code.length() > 50) {
            etCode.setError("Mã danh mục phải từ 5 đến 50 ký tự");
            return false;
        }

        return true;
    }

    private void showDeleteCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delete_category, null);
        builder.setView(dialogView);

        ListView lvCategories = dialogView.findViewById(R.id.lvCategories);
        CheckBox cbSelectAll = dialogView.findViewById(R.id.cbSelectAll);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        List<String> categoryCodes = new ArrayList<>();
        List<Long> categoryIds = new ArrayList<>();
        for (Flashcard card : flashcardList) {
            categoryCodes.add(card.getCode() != null ? ""+  card.getTitle()+ "-" + card.getCode() : "Mã: " + card.getId());
            categoryIds.add(card.getId());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_multiple_choice, categoryCodes);
        lvCategories.setAdapter(adapter);
        lvCategories.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        cbSelectAll.setOnCheckedChangeListener((btn, isChecked) -> {
            for (int i = 0; i < lvCategories.getCount(); i++) {
                lvCategories.setItemChecked(i, isChecked);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        btnConfirm.setOnClickListener(v -> {
            SparseBooleanArray checked = lvCategories.getCheckedItemPositions();
            List<Long> selectedIds = new ArrayList<>();
            for (int i = 0; i < checked.size(); i++) {
                if (checked.valueAt(i)) {
                    selectedIds.add(categoryIds.get(checked.keyAt(i)));
                }
            }
            if (selectedIds.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất một danh mục", Toast.LENGTH_SHORT).show();
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
                .setMessage("Bạn có chắc muốn xóa " + selectedIds.size() + " danh mục? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteCategories(selectedIds))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteCategories(List<Long> selectedIds) {
        apiService.deactivateCategories(new DeleteCategoriesRequest(selectedIds))
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                        if (response.isSuccessful()) {
                            flashcardList.removeIf(f -> selectedIds.contains(f.getId()));
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), "Đã xóa " + selectedIds.size() + " danh mục", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Lỗi khi xóa danh mục", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void returnToSettingsUI() {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        View scrollView = requireActivity().findViewById(R.id.scrollViewSettings);
        View bottomNav = requireActivity().findViewById(R.id.bottomNavigation);
        View fragmentContainer = requireActivity().findViewById(R.id.fragmentContainer);
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar != null ? toolbar.findViewById(R.id.toolbar_title) : null;

        if (scrollView != null) scrollView.setVisibility(View.VISIBLE);
        if (bottomNav != null) bottomNav.setVisibility(View.VISIBLE);
        if (fragmentContainer != null) fragmentContainer.setVisibility(View.GONE);
        if (toolbarTitle != null) toolbarTitle.setText("Cài đặt");

        if (toolbar != null) toolbar.setNavigationIcon(null);
    }
}

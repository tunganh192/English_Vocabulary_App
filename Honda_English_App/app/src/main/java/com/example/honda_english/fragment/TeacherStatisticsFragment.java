package com.example.honda_english.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.example.honda_english.adapter.StudentStatsAdapter;
import com.example.honda_english.api.ApiService;
import com.example.honda_english.api.RetrofitClient;
import com.example.honda_english.model.ApiResponse;
import com.example.honda_english.model.Category.CategoryResponse;
import com.example.honda_english.model.PageResponse;
import com.example.honda_english.model.Statistic.LearnedWordAccuracyResponse;
import com.example.honda_english.model.Statistic.StudentStats;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherStatisticsFragment extends Fragment {

    private TextView tvTotalStudents, tvAverageWordsLearned, tvAverageCorrectRate;
    private RecyclerView rvStudentStats;
    private Spinner spinnerCategories;
    private StudentStatsAdapter adapter;
    private List<StudentStats> studentStatsList;
    private List<CategoryResponse> categoryList;
    private ApiService apiService;
    private PrefUtils prefUtils;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_statistics, container, false);

        prefUtils = new PrefUtils(requireContext());

        userId = prefUtils.getUserId();

        setupToolbar();

        initViews(view);
        setupRecyclerView();
        setupSpinner();

        apiService = RetrofitClient.getClient(getContext()).create(ApiService.class);
        loadCategories();

        return view;
    }

    private void initViews(View view) {
        tvTotalStudents = view.findViewById(R.id.tvTotalStudents);
        tvAverageWordsLearned = view.findViewById(R.id.tvAverageWordsLearned);
        tvAverageCorrectRate = view.findViewById(R.id.tvAverageCorrectRate);
        rvStudentStats = view.findViewById(R.id.rvStudentStats);
        spinnerCategories = view.findViewById(R.id.spinnerCategories);
    }

    private void setupRecyclerView() {
        studentStatsList = new ArrayList<>();
        adapter = new StudentStatsAdapter(studentStatsList);
        rvStudentStats.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStudentStats.setAdapter(adapter);
    }

    private void setupSpinner() {
        categoryList = new ArrayList<>();

        List<String> defaultList = new ArrayList<>();
        defaultList.add("Đang tải bài học...");
        ArrayAdapter<String> defaultAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, defaultList);
        defaultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(defaultAdapter);
    }

    private void loadCategories() {
        categoryList.clear();
        fetchCategories(1);
    }

    private void fetchCategories(int pageNo) {

        if (userId == null || userId.isEmpty()) {
            updateSpinnerWithError();
            return;
        }

        Log.d("TeacherStatsFragment", "Fetching categories for userID: " + userId + ", page: " + pageNo);
        apiService.getCategoriesByCreator(userId, pageNo, 100).enqueue(new Callback<ApiResponse<PageResponse<List<CategoryResponse>>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<List<CategoryResponse>>>> call, Response<ApiResponse<PageResponse<List<CategoryResponse>>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    PageResponse<List<CategoryResponse>> pageResponse = response.body().getResult();
                    categoryList.addAll(pageResponse.getItems());

                    if (pageNo < pageResponse.getTotalPages()) {
                        fetchCategories(pageNo + 1);
                    } else {
                        handleCategoriesLoaded();
                    }
                } else {
                    Log.e("TeacherStatsFragment", "Failed to fetch categories: " + (response != null ? response.message() : "Response is null"));
                    Toast.makeText(getContext(), "Lỗi khi lấy danh sách bài học", Toast.LENGTH_SHORT).show();
                    updateSpinnerWithError();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<List<CategoryResponse>>>> call, Throwable t) {
                Log.e("TeacherStatsFragment", "Network error while fetching categories: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                updateSpinnerWithError();
            }
        });
    }

    private void handleCategoriesLoaded() {
        if (categoryList.isEmpty()) {
            Toast.makeText(getContext(), "Không có bài học nào", Toast.LENGTH_SHORT).show();
            updateSpinnerWithError();
            return;
        }

        List<String> categoryNames = new ArrayList<>();
        for (CategoryResponse category : categoryList) {
            String name = category.getName() != null ? category.getName() : "Bài học không tên";
            categoryNames.add(name);
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();

        setupSpinnerListener();
    }

    private void setupSpinnerListener() {
        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < categoryList.size()) {
                    loadStatistics(categoryList.get(position).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateStats(0, 0, 0.0, new ArrayList<>());
            }
        });
    }

    private void updateSpinnerWithError() {
        List<String> errorList = new ArrayList<>();
        errorList.add("Không có bài học nào");
        ArrayAdapter<String> errorAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, errorList);
        errorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(errorAdapter);
        updateStats(0, 0, 0.0, new ArrayList<>());
    }

    private void loadStatistics(Long categoryId) {
        studentStatsList.clear();
        adapter.notifyDataSetChanged();

        fetchCategoryAccuracyStats(categoryId, 1);
    }

    private void fetchCategoryAccuracyStats(Long categoryId, int pageNo) {
        apiService.getCategoryAccuracyStats(categoryId, pageNo, 100).enqueue(new Callback<ApiResponse<PageResponse<List<LearnedWordAccuracyResponse>>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<List<LearnedWordAccuracyResponse>>>> call, Response<ApiResponse<PageResponse<List<LearnedWordAccuracyResponse>>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    PageResponse<List<LearnedWordAccuracyResponse>> pageResponse = response.body().getResult();
                    List<LearnedWordAccuracyResponse> statsList = pageResponse.getItems();

                    if (pageNo == 1) {
                        tvTotalStudents.setText("Tổng số học sinh: " + pageResponse.getTotalElements());
                    }

                    for (LearnedWordAccuracyResponse stat : statsList) {
                        StudentStats studentStats = new StudentStats(
                                stat.getUserId(),
                                stat.getUserName(),
                                (int) stat.getLearnedCount(),
                                stat.getCorrectCount(),
                                stat.getTotalCount()
                        );
                        studentStatsList.add(studentStats);
                    }

                    if (pageNo < pageResponse.getTotalPages()) {
                        fetchCategoryAccuracyStats(categoryId, pageNo + 1);
                    } else {
                        handleStatsLoaded(pageResponse);
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi khi lấy thống kê học sinh", Toast.LENGTH_SHORT).show();
                    updateStats(0, 0, 0.0, new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<List<LearnedWordAccuracyResponse>>>> call, Throwable t) {
                Log.e("TeacherStatsFragment", "Network error: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                updateStats(0, 0, 0.0, new ArrayList<>());
            }
        });
    }

    private void handleStatsLoaded(PageResponse<List<LearnedWordAccuracyResponse>> pageResponse) {
        if (studentStatsList.isEmpty()) {
            updateStats(0, 0, 0.0, new ArrayList<>());
        } else {
            updateStats(
                    (int) pageResponse.getTotalElements(),
                    calculateAverageWords(studentStatsList),
                    calculateAverageCorrectRate(studentStatsList),
                    new ArrayList<>(studentStatsList)
            );
        }
    }

    private void updateStats(int totalStudents, double avgWordsLearned, double avgCorrectRate, List<StudentStats> statsList) {
        tvTotalStudents.setText("Tổng số học sinh: " + totalStudents);
        tvAverageWordsLearned.setText(String.format("Số từ trung bình đã học: %.1f", avgWordsLearned));
        tvAverageCorrectRate.setText(String.format("Tỷ lệ trả lời đúng trung bình: %.1f%%", avgCorrectRate));

        studentStatsList.clear();
        studentStatsList.addAll(statsList);

        adapter.notifyDataSetChanged();

        if (rvStudentStats.getVisibility() != View.VISIBLE) {
            rvStudentStats.setVisibility(View.VISIBLE);
        }
    }

    private double calculateAverageWords(List<StudentStats> statsList) {
        if (statsList.isEmpty()) return 0;
        int total = 0;
        for (StudentStats stats : statsList) {
            total += stats.getWordsLearned();
        }
        return (double) total / statsList.size();
    }

    private double calculateAverageCorrectRate(List<StudentStats> statsList) {
        if (statsList.isEmpty()) return 0;
        long totalCorrect = 0;
        long totalQuestions = 0;
        for (StudentStats stats : statsList) {
            totalCorrect += stats.getCorrectCount();
            totalQuestions += stats.getTotalCount();
        }
        return totalQuestions > 0 ? (double) totalCorrect / totalQuestions * 100 : 0;
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
}

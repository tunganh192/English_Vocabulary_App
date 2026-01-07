package com.example.honda_english.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.honda_english.R;
import com.example.honda_english.util.PrefUtils;
import com.example.honda_english.adapter.CategoryAdapter;
import com.example.honda_english.api.ApiService;
import com.example.honda_english.api.RetrofitClient;
import com.example.honda_english.model.*;
import com.example.honda_english.model.Category.Category;
import com.example.honda_english.model.Category.CategoryResponse;
import com.example.honda_english.model.Statistic.LearnedWordPercentageResponse;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {
    private List<Category> categoryList = new ArrayList<>();
    private CategoryAdapter adapter;
    private ApiService apiService;
    private ProgressBar progressLoading;
    private String title = "";
    private PrefUtils prefUtils;

    private long parentId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        prefUtils = new PrefUtils(this);
        title = getIntent().getStringExtra("title");
        parentId = getIntent().getLongExtra("CATEGORY_ID", -1);
        userId = prefUtils.getUserId();

        setupToolbar();
        setupRecyclerView();
        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        progressLoading = findViewById(R.id.progressLoading);
        progressLoading.setVisibility(View.VISIBLE);

        fetchSubcategoriesSequential(parentId, 1, 10);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ((TextView) findViewById(R.id.toolbar_title)).setText(title);
        findViewById(R.id.imgLeftIcon).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new CategoryAdapter(categoryList, (id, name, progress) -> {
            Intent intent = new Intent(this, VocabularyActivity.class);
            intent.putExtra("CATEGORY_ID", id);
            intent.putExtra("CATEGORY_NAME", name);
            intent.putExtra("CATEGORY_PARENT", title);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void fetchSubcategoriesSequential(long parentId, int page, int size) {
        apiService.getSubcategories(parentId, page, size).enqueue(new Callback<ApiResponse<PageResponse<List<CategoryResponse>>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<List<CategoryResponse>>>> call, Response<ApiResponse<PageResponse<List<CategoryResponse>>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    List<CategoryResponse> items = response.body().getResult().getItems();
                    if (items != null && !items.isEmpty()) {
                        for (CategoryResponse categoryResponse : items) {
                            Category category = new Category(categoryResponse.getId(), title, categoryResponse.getName());
                            categoryList.add(category);
                        }
                        if (page < response.body().getResult().getTotalPages()) {
                            fetchSubcategoriesSequential(parentId, page + 1, size);
                        } else {
                            fetchProgressSequential(0);
                        }
                    } else {
                        onAllLoaded();
                    }
                } else {
                    onAllLoaded();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<List<CategoryResponse>>>> call, Throwable t) {
                Log.e("API", "Subcategory failure: " + t.getMessage());
                onAllLoaded();
            }
        });
    }

    private void fetchProgressSequential(int index) {
        if (index >= categoryList.size()) {
            onAllLoaded();
            return;
        }

        Category category = categoryList.get(index);
        apiService.getLearnedWordPercentage(category.getId(), userId).enqueue(new Callback<ApiResponse<LearnedWordPercentageResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LearnedWordPercentageResponse>> call, Response<ApiResponse<LearnedWordPercentageResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    category.setProgress(response.body().getResult().getPercentage());
                } else {
                    category.setProgress(0);
                }
                fetchProgressSequential(index + 1);
            }

            @Override
            public void onFailure(Call<ApiResponse<LearnedWordPercentageResponse>> call, Throwable t) {
                category.setProgress(0);
                fetchProgressSequential(index + 1);
            }
        });
    }

    private void onAllLoaded() {
        progressLoading.setVisibility(View.GONE);

        categoryList.sort(Comparator.comparingLong(Category::getId));
        if (categoryList.isEmpty()) {
            Toast.makeText(this, "Không có danh mục con nào", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
    }
}

package com.example.honda_english.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.honda_english.R;
import com.example.honda_english.adapter.FlashcardAdapter;
import com.example.honda_english.api.ApiService;
import com.example.honda_english.api.RetrofitClient;
import com.example.honda_english.fragment.CategoryDetailFragment;
import com.example.honda_english.util.PrefUtils;
import com.example.honda_english.model.ApiResponse;
import com.example.honda_english.model.Category.CategoryResponse;
import com.example.honda_english.model.Category.Flashcard;
import com.example.honda_english.model.PageResponse;
import com.example.honda_english.model.Statistic.WordCountResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LessonsActivity extends AppCompatActivity {
    private RecyclerView recyclerView, recyclerViewSearch;
    private FlashcardAdapter joinedAdapter, searchAdapter;
    private List<Flashcard> joinedFlashcardList = new ArrayList<>();
    private List<Flashcard> searchFlashcardList = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;
    private ProgressBar progressLoading;
    private EditText edtSearchName, edtSearchCode;
    private ImageView imgBack;
    private Spinner spinnerSort;
    private Button btnSearch, btnRetern;
    private TextView tvSearchResults, textViewTitle;
    private View searchSection, fragmentContainer;
    private ApiService apiService;
    private int pendingRequests = 0;
    private PrefUtils prefUtils;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);
        EdgeToEdge.enable(this);
        prefUtils = new PrefUtils(this);
        userId = prefUtils.getUserId();
        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        initViews();
        setupAdapters();
        setupSpinner();
        setupListeners();
        loadJoinedCategories();
        setupBottomNavigation();
    }

    private void initViews() {
        progressLoading = findViewById(R.id.progressLoading);
        edtSearchName = findViewById(R.id.etSearchName);
        edtSearchCode = findViewById(R.id.etSearchCode);
        spinnerSort = findViewById(R.id.spinnerSort);
        btnSearch = findViewById(R.id.btnSearch);
        btnRetern = findViewById(R.id.btnRetern);
        imgBack = findViewById(R.id.imgBack);
        tvSearchResults = findViewById(R.id.tvSearchResults);
        textViewTitle = findViewById(R.id.tvTextt);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewSearch = findViewById(R.id.recyclerViewSearch);
        searchSection = findViewById(R.id.search_section);
        fragmentContainer = findViewById(R.id.fragment_container);
    }

    private void setupAdapters() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(this));

        joinedAdapter = new FlashcardAdapter(joinedFlashcardList, this::showCategoryDetailFragment);
        recyclerView.setAdapter(joinedAdapter);

        searchAdapter = new FlashcardAdapter(searchFlashcardList, this::showCategoryDetailFragment);
        recyclerViewSearch.setAdapter(searchAdapter);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);
    }

    private void setupListeners() {
        btnSearch.setOnClickListener(v -> {
            if (!checkLoginBeforeAction()) return;
            String name = edtSearchName.getText().toString().trim();
            String code = edtSearchCode.getText().toString().trim();
            if (name.isEmpty() && code.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên hoặc mã danh mục", Toast.LENGTH_SHORT).show();
                return;
            }
            searchCategories(name, code, getSortByValue(spinnerSort.getSelectedItemPosition()), 1, 10);
        });

        btnRetern.setOnClickListener(v -> {
            recyclerViewSearch.setVisibility(View.GONE);
            tvSearchResults.setVisibility(View.GONE);
            textViewTitle.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            edtSearchName.setText("");
            edtSearchCode.setText("");
        });

        imgBack.setOnClickListener(v -> {
            searchSection.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            if (!searchFlashcardList.isEmpty()) {
                tvSearchResults.setVisibility(View.VISIBLE);
                recyclerViewSearch.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            fragmentContainer.setVisibility(View.VISIBLE);
            imgBack.setVisibility(View.GONE);
            getSupportFragmentManager().popBackStack();
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_lessons);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(LessonsActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_lessons) {
                return true;
            } else if (itemId == R.id.navigation_statistics) {
                startActivity(new Intent(LessonsActivity.this, StatisticsActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_settings) {
                startActivity(new Intent(LessonsActivity.this, SettingsActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private boolean checkLoginBeforeAction() {
        if (!prefUtils.isLoggedIn() || userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để sử dụng chức năng này", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void loadJoinedCategories() {
        progressLoading.setVisibility(View.VISIBLE);
        joinedFlashcardList.clear();
        joinedAdapter.notifyDataSetChanged();
        fetchJoinedCategories(1, 10);
    }

    private void fetchJoinedCategories(int pageNo, int pageSize) {
        Log.d("API", "GET /joined/" + userId + "?pageNo=" + pageNo + "&pageSize=" + pageSize);
        apiService.getJoinedCategoriesByUserId(userId, pageNo, pageSize).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<List<CategoryResponse>>>> call,
                                   Response<ApiResponse<PageResponse<List<CategoryResponse>>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    var page = response.body().getResult();
                    var categories = page.getItems();
                    if (categories != null && !categories.isEmpty()) {
                        pendingRequests += categories.size();
                        categories.forEach(category -> {
                            Flashcard flashcard = new Flashcard(category.getId(), category.getName(), 0, R.drawable.note1, category.getCode());
                            joinedFlashcardList.add(flashcard);
                            apiService.getWordCountCategory(category.getId()).enqueue(new Callback<>() {
                                @Override
                                public void onResponse(Call<ApiResponse<WordCountResponse>> call, Response<ApiResponse<WordCountResponse>> response) {
                                    if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                                        flashcard.setWordCount(response.body().getResult().getWordCount());
                                    }
                                    pendingRequests--;
                                    updateJoinedAdapterIfComplete();
                                }
                                @Override
                                public void onFailure(Call<ApiResponse<WordCountResponse>> call, Throwable t) {
                                    pendingRequests--;
                                    updateJoinedAdapterIfComplete();
                                }
                            });
                        });
                        if (pageNo < page.getTotalPages()) {
                            fetchJoinedCategories(pageNo + 1, pageSize);
                        }
                    } else {
                        updateJoinedAdapterIfComplete();
                    }
                } else {
                    updateJoinedAdapterIfComplete();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<PageResponse<List<CategoryResponse>>>> call, Throwable t) {
                updateJoinedAdapterIfComplete();
            }
        });
    }

    private void searchCategories(String name, String code, String sortBy, int pageNo, int pageSize) {
        progressLoading.setVisibility(View.VISIBLE);
        searchFlashcardList.clear();
        searchAdapter.notifyDataSetChanged();

        recyclerViewSearch.setVisibility(View.GONE);
        tvSearchResults.setVisibility(View.GONE);
        textViewTitle.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        Log.d("API", "GET /search?name=" + name + "&code=" + code + "&sortBy=" + sortBy);
        apiService.searchCategories(name, code, pageNo, pageSize, sortBy).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<PageResponse<List<CategoryResponse>>> call, Response<PageResponse<List<CategoryResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    var categories = response.body().getItems();
                    if (categories != null && !categories.isEmpty()) {
                        pendingRequests += categories.size();
                        categories.forEach(category -> {
                            Flashcard flashcard = new Flashcard(category.getCode(), category.getCreatedBy(), R.drawable.note1, category.getId(), category.getName(), 0);
                            searchFlashcardList.add(flashcard);
                            apiService.getWordCountCategory(category.getId()).enqueue(new Callback<>() {
                                @Override
                                public void onResponse(Call<ApiResponse<WordCountResponse>> call, Response<ApiResponse<WordCountResponse>> response) {
                                    if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                                        flashcard.setWordCount(response.body().getResult().getWordCount());
                                    }
                                    pendingRequests--;
                                    updateSearchAdapterIfComplete();
                                }
                                @Override
                                public void onFailure(Call<ApiResponse<WordCountResponse>> call, Throwable t) {
                                    pendingRequests--;
                                    updateSearchAdapterIfComplete();
                                }
                            });
                        });
                        if (pageNo < response.body().getTotalPages()) {
                            searchCategories(name, code, sortBy, pageNo + 1, pageSize);
                        }
                    } else {
                        updateSearchAdapterIfComplete();
                        Toast.makeText(LessonsActivity.this, "Không tìm thấy danh mục", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    updateSearchAdapterIfComplete();
                    Toast.makeText(LessonsActivity.this, "Lỗi khi tìm kiếm danh mục", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<PageResponse<List<CategoryResponse>>> call, Throwable t) {
                updateSearchAdapterIfComplete();
                Toast.makeText(LessonsActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateJoinedAdapterIfComplete() {
        if (pendingRequests == 0) {
            progressLoading.setVisibility(View.GONE);
            joinedAdapter.notifyDataSetChanged();
        }
    }

    private void updateSearchAdapterIfComplete() {
        if (pendingRequests == 0) {
            progressLoading.setVisibility(View.GONE);
            searchAdapter.notifyDataSetChanged();
            tvSearchResults.setVisibility(View.VISIBLE);
            recyclerViewSearch.setVisibility(View.VISIBLE);
        }
    }

    private String getSortByValue(int position) {
        switch (position) {
            case 0: return "code:asc";
            case 1: return "code:desc";
            case 2: return "name:asc";
            case 3: return "name:desc";
            default: return "name:asc";
        }
    }

    private void showCategoryDetailFragment(Flashcard flashcard) {
        if (!checkLoginBeforeAction()) return;
        searchSection.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        recyclerViewSearch.setVisibility(View.GONE);
        tvSearchResults.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
        imgBack.setVisibility(View.VISIBLE);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = CategoryDetailFragment.newInstance(
                flashcard.getId(),
                flashcard.getTitle(),
                flashcard.getCode()
        );
        transaction.replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
    }

    @Override
    public void onBackPressed() {
        if (fragmentContainer.getVisibility() == View.VISIBLE) {
            searchSection.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            if (!searchFlashcardList.isEmpty()) {
                tvSearchResults.setVisibility(View.VISIBLE);
                recyclerViewSearch.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            fragmentContainer.setVisibility(View.VISIBLE);
            imgBack.setVisibility(View.GONE);
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}

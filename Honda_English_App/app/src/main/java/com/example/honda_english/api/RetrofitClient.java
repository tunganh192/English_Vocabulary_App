package com.example.honda_english.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.honda_english.adapter.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.threeten.bp.LocalDate;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.honda_english.activity.LoginActivity;
import com.example.honda_english.util.PrefUtils;
import com.example.honda_english.model.ApiResponse;
import com.example.honda_english.model.Authentication.AuthenticationResponse;
import com.example.honda_english.model.Authentication.RefreshRequest;

public class RetrofitClient { //Tạo một Retrofit instance. và cấu hình kết nối
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Retrofit retrofit = null;
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final Object TOKEN_REFRESH_LOCK = new Object();

    private static final Map<String, Set<String>> PUBLIC_ENDPOINTS = Map.ofEntries( //map bất biến
            Map.entry("/user", Set.of("POST")),
            Map.entry("/auth/login", Set.of("POST")),
            Map.entry("/auth/introspect", Set.of("POST")),
            Map.entry("/auth/logout", Set.of("POST")),
            Map.entry("/auth/refresh", Set.of("POST")),
            Map.entry("/category/system-generated/parents", Set.of("GET")),
            Map.entry("/category/generated/subcategories", Set.of("GET")),
            Map.entry("/word/user/subcategory/", Set.of("GET")),
            Map.entry("/word/count/system-parent-category/", Set.of("GET")),
            Map.entry("/word/count/category/", Set.of("GET"))
    );


    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Tạo AuthInterceptor để thêm token và xử lý refresh
            Interceptor authInterceptor = chain -> {  //Phương thức intercept sẽ được gọi mỗi khi một HTTP request được thực hiện.
                Request originalRequest = chain.request();
                String path = originalRequest.url().encodedPath();
                String method = originalRequest.method();

                // Kiểm tra public endpoint
                boolean isPublicEndpoint = PUBLIC_ENDPOINTS.entrySet().stream() //trả về tập hợp các "cặp key-value" trong map.
                        .anyMatch(entry -> path.equals(entry.getKey()) && entry.getValue().contains(method));  // Duyệt từng entry trong stream  Trả về true ngay khi tìm thấy 1 phần tử thỏa mãn điều kiện. nếu k có trả về false

                if (isPublicEndpoint) {
                    return chain.proceed(originalRequest); // để gửi request đi như bình thường.
                }

                // Lấy token
                SharedPreferences prefs = getSharedPreferences(context);
                String token = prefs.getString(KEY_TOKEN, null);
                Request.Builder requestBuilder = originalRequest.newBuilder();

                if (token != null && !token.isEmpty()) {
                    requestBuilder.header("Authorization", "Bearer " + token);
                }

                // Thử gửi request
                Response response = chain.proceed(requestBuilder.build());

                // Xử lý lỗi 401 (token hết hạn)
                if (response.code() == 401) {
                    try {
                        String errorBody = response.body() != null ? response.body().string() : "";
                        ApiResponse errorResponse = new Gson().fromJson(errorBody, ApiResponse.class);
                        int errorCode = errorResponse.getCode();

                        if (errorCode == 1005) { // Token hết hạn
                            Log.d("RetrofitClient", "Access token expired, attempting to refresh...");

                            // Đồng bộ hóa refresh token
                            synchronized (TOKEN_REFRESH_LOCK) {  //Chỉ 1 thread được phép chạy đoạn refresh token tại 1 thời điểm.
                                // Kiểm tra lại token sau khi vào lock
                                String currentToken = prefs.getString(KEY_TOKEN, null);
                                if (!token.equals(currentToken)) {
                                    // Token đã được refresh bởi thread khác
                                    Log.d("RetrofitClient", "Token đã được cập nhật bởi thread khác");
                                    Request newRequest = originalRequest.newBuilder()
                                            .header("Authorization", "Bearer " + currentToken)
                                            .build();
                                    response.close();
                                    return chain.proceed(newRequest);
                                }

                                // Gọi API refresh token
                                String newToken = refreshToken(context, token);
                                if (newToken != null) {
                                    // Lưu token mới
                                    prefs.edit().putString(KEY_TOKEN, newToken).apply();

                                    // Retry request với token mới
                                    Request newRequest = originalRequest.newBuilder()
                                            .header("Authorization", "Bearer " + newToken)
                                            .build();
                                    response.close();
                                    return chain.proceed(newRequest);
                                } else {
                                    // Refresh thất bại -> logout
                                    logout(context);
                                    response.close();
                                    return response;
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("RetrofitClient", "Lỗi xử lý 401: " + e.getMessage());
                    }
                }

                return response;
            };

            // Tạo OkHttpClient
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(logging)
                    .build();

            // Khởi tạo Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)  // chỉ định thuật toán mã hóa  cho khóa chính
                    .build();
            return EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e("RetrofitClient", "Lỗi khởi tạo EncryptedSharedPreferences: " + e.getMessage());
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    private static String refreshToken(Context context, String oldToken) {
        ApiService apiService = getClient(context).create(ApiService.class);
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setToken(oldToken);

        try {
            retrofit2.Response<ApiResponse<AuthenticationResponse>> response = apiService.refresh(refreshRequest).execute();
            if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                AuthenticationResponse authResponse = response.body().getResult();
                if (authResponse.isAuthenticated()) {
                    return authResponse.getToken();
                }
            } else {
                Log.e("RetrofitClient", "Refresh token failed: " + (response.errorBody() != null ? response.errorBody().string() : "Unknown error"));
            }
        } catch (IOException e) {
            Log.e("RetrofitClient", "Network error during token refresh: " + e.getMessage());
        }
        return null;
    }

    private static void logout(Context context) {
        // Xóa dữ liệu người dùng
        PrefUtils prefUtils = new PrefUtils(context);
        prefUtils.clearUser();

        // Chuyển về MainActivity
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
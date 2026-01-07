package com.example.honda_english.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class PrefUtils {
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    // Remember Me
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";

    private SharedPreferences prefs;

    public PrefUtils(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            prefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

    // --- Lấy dữ liệu ---
    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, null);
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && getUserId() != null && getToken() != null;
    }

    public boolean isRememberedMe() {
        return prefs.getBoolean(KEY_REMEMBER, false);
    }

    public String getSavedUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }

    public String getSavedPassword() {
        return prefs.getString(KEY_PASSWORD, "");
    }

    // --- Lưu dữ liệu ---
    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public void saveUser(String userId, String userRole) {
        prefs.edit()
                .putString(KEY_USER_ID, userId)
                .putString(KEY_USER_ROLE, userRole)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }

    public void saveLoginCredentials(String username, String password) {
        prefs.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_PASSWORD, password)
                .putBoolean(KEY_REMEMBER, true)
                .apply();
    }

    // --- Xóa dữ liệu ---
    public void clearLoginCredentials() {
        prefs.edit()
                .remove(KEY_USERNAME)
                .remove(KEY_PASSWORD)
                .remove(KEY_REMEMBER)
                .apply();
    }

    public void clearUser() {
        prefs.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_USER_ID)
                .remove(KEY_USER_ROLE)
                .remove(KEY_IS_LOGGED_IN)
                .apply();
    }
}
package com.example.honda_english.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.honda_english.R;
import com.example.honda_english.util.PrefUtils;
import com.example.honda_english.util.enums.Role;
import com.example.honda_english.api.ApiService;
import com.example.honda_english.api.RetrofitClient;
import com.example.honda_english.model.ApiResponse;
import com.example.honda_english.model.Authentication.AuthenticationRequest;
import com.example.honda_english.model.Authentication.AuthenticationResponse;
import com.example.honda_english.model.User.UserCreationRequest;
import com.example.honda_english.model.User.UserResponse;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class LoginActivity extends AppCompatActivity {
    private EditText usernameInput, passwordInput, registerUsernameInput, registerPasswordInput, confirmPasswordInput, displayNameInput, dateOfBirthInput;
    private Button loginButton, registerButton;
    private ImageButton cancelRegisterButton, cancelLoginButton;
    private CheckBox rememberMeCheckBox;
    private TextView signupText, loginMessage, registerMessage;
    private RadioGroup roleRadioGroup;
    private View registerLayout, loginLayout;

    private ApiService apiService;
    private PrefUtils prefUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        AndroidThreeTen.init(this);

        prefUtils = new PrefUtils(this);
        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        initViews();
        loadSavedCredentials();
        setupListeners();
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> handleLogin());
        signupText.setOnClickListener(v -> showRegisterLayout());
        cancelLoginButton.setOnClickListener(v -> navigateToHome());
        cancelRegisterButton.setOnClickListener(v -> showLoginLayout());
        dateOfBirthInput.setOnClickListener(v -> showDatePicker());
        registerButton.setOnClickListener(v -> handleRegistration());
    }

    //LOGIN

    private void handleLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showLoginMessage("Hãy nhập tài khoản và mật khẩu!");
            return;
        }

        AuthenticationRequest loginRequest = new AuthenticationRequest(password, username);

        apiService.login(loginRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthenticationResponse>> call, Response<ApiResponse<AuthenticationResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    AuthenticationResponse auth = response.body().getResult();
                    if (auth.isAuthenticated()) {
                        prefUtils.saveToken(auth.getToken());
                        fetchUserInfo(username);
                    } else {
                        showLoginMessage("Đăng nhập không thành công. Vui lòng kiểm tra lại thông tin.");
                    }
                } else {
                    showLoginMessage("Đăng nhập thất bại. Vui lòng thử lại.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthenticationResponse>> call, Throwable t) {
                showLoginMessage("Lỗi kết nối mạng. Vui lòng kiểm tra và thử lại.");
            }
        });
    }

    private void fetchUserInfo(String username) {
        apiService.getUserByUserName(username).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body().getResult();
                    prefUtils.saveUser(user.getId(), user.getRole());

                    if (rememberMeCheckBox.isChecked()) {
                        saveCredentials(usernameInput.getText().toString(), passwordInput.getText().toString());
                    } else {
                        clearCredentials();
                    }

                    navigateToHome();
                } else {
                    showLoginMessage("Không thể lấy thông tin người dùng. Vui lòng thử lại sau.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                showLoginMessage("Lỗi kết nối mạng khi lấy thông tin người dùng.");
            }
        });
    }

    //REGISTER

    private void handleRegistration() {
        registerMessage.setVisibility(View.GONE);
        registerMessage.setText("");

        String username = registerUsernameInput.getText().toString().trim();
        String password = registerPasswordInput.getText().toString().trim();
        String confirmPass = confirmPasswordInput.getText().toString().trim();
        String displayName = displayNameInput.getText().toString().trim();
        String dob = dateOfBirthInput.getText().toString().trim();
        String role = roleRadioGroup.getCheckedRadioButtonId() == R.id.roleUser
                ? Role.USER.toString() : Role.TEACHER.toString();

        if (!validateRegistration(username, password, confirmPass, displayName, dob)) return;

        LocalDate birthDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        UserCreationRequest request = new UserCreationRequest(5, birthDate, displayName, password, role, username);

        apiService.createUser(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful()) {
                    showRegisterSuccess();
                } else {
                    if (response.errorBody() != null) {
                        String errorJson = null;
                        try {
                            errorJson = response.errorBody().string();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        ApiResponse errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                        if (errorResponse != null && errorResponse.getCode() == 1002) {
                            showRegisterMessage("Tài khoản đã tồn tại. Vui lòng nhập tài khoản khác");
                        }
                        if (errorResponse != null && errorResponse.getCode() == 1032) {
                            showRegisterMessage("Tài khoản đã tồn tại. Vui lòng nhập tài khoản khác");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                showRegisterMessage("Lỗi kết nối mạng. Vui lòng kiểm tra và thử lại.");
            }
        });
    }

    private boolean validateRegistration(String username, String password, String confirmPassword, String name, String dob) {
        if (username.isEmpty() || username.length() < 4 || username.length() > 20) {
            showRegisterMessage("Tên đăng nhập phải từ 4 đến 20 ký tự");
            return false;
        }
        if (password.isEmpty() || password.length() < 6 || password.length() > 64) {
            showRegisterMessage("Mật khẩu phải từ 6 đến 64 ký tự");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showRegisterMessage("Mật khẩu không khớp");
            return false;
        }
        if (name.isEmpty() || name.length() > 50) {
            showRegisterMessage("Tên hiển thị phải từ 1 đến 50 ký tự");
            return false;
        }
        if (dob.isEmpty()) {
            showRegisterMessage("Ngày sinh không được để trống");
            return false;
        }

        try {
            LocalDate birthDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            int age = LocalDate.now().getYear() - birthDate.getYear();
            if (age < 6) {
                showRegisterMessage("Phải từ 6 tuổi trở lên");
                return false;
            }
        } catch (Exception e) {
            showRegisterMessage("Ngày sinh không hợp lệ! Định dạng: MM/dd/yyyy");
            return false;
        }

        return true;
    }

    //UI

    private void showRegisterLayout() {
        loginLayout.setVisibility(View.GONE);
        registerLayout.setVisibility(View.VISIBLE);
    }

    private void showLoginLayout() {
        registerLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.VISIBLE);
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày sinh")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
            dateOfBirthInput.setText(date);
        });

        picker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void showRegisterMessage(String message) {
        registerMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        registerMessage.setText(message);
        registerMessage.setVisibility(View.VISIBLE);
    }
    private void showLoginMessage(String message) {
        loginMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        loginMessage.setText(message);
        loginMessage.setVisibility(View.VISIBLE);
    }

    private void showRegisterSuccess() {
        showLoginLayout();
        showLoginMessage("Đăng ký thành công");
    }

    //Preferences
    private void loadSavedCredentials() {
        rememberMeCheckBox.setChecked(prefUtils.isRememberedMe());
        if (prefUtils.isRememberedMe()) {
            usernameInput.setText(prefUtils.getSavedUsername());
            passwordInput.setText(prefUtils.getSavedPassword());
        }
    }

    private void saveCredentials(String username, String password) {
        prefUtils.saveLoginCredentials(username, password);
    }

    private void clearCredentials() {
        prefUtils.clearLoginCredentials();
        rememberMeCheckBox.setChecked(false);
    }


    private void initViews() {
        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        registerUsernameInput = findViewById(R.id.registerUsername);
        registerPasswordInput = findViewById(R.id.registerPassword);
        confirmPasswordInput = findViewById(R.id.confirmPassword);
        displayNameInput = findViewById(R.id.displayName);
        dateOfBirthInput = findViewById(R.id.dateOfBirth);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        cancelRegisterButton = findViewById(R.id.cancelRegisterButton);
        cancelLoginButton = findViewById(R.id.cancelLogicButton);
        rememberMeCheckBox = findViewById(R.id.remembercheckBox);
        signupText = findViewById(R.id.signupText);
        loginMessage = findViewById(R.id.loginMessage);
        registerMessage = findViewById(R.id.registerMessage);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        loginLayout = findViewById(R.id.loginLayout);
        registerLayout = findViewById(R.id.registerLayout);
    }
}

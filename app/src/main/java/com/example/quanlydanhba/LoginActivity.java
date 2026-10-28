package com.example.quanlydanhba;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // Tài khoản cố định theo đề bài (chỉ dùng cho bài tập, không dùng cho app thật)
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "123456";

    private EditText edtUsername;
    private EditText edtPassword;
    private TextView tvLoginError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        tvLoginError = findViewById(R.id.tvLoginError);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        // Đã đăng nhập thành công và đang đóng màn hình: bỏ qua lần bấm lặp
        // để không mở 2 MainActivity
        if (isFinishing()) {
            return;
        }

        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError(R.string.error_login_empty);
            return;
        }
        if (!USERNAME.equals(username) || !PASSWORD.equals(password)) {
            showError(R.string.error_login_wrong);
            return;
        }

        startActivity(new Intent(this, MainActivity.class));
        // Đóng Login để nút Back ở Main không quay lại màn hình đăng nhập
        finish();
    }

    private void showError(@StringRes int messageRes) {
        tvLoginError.setText(messageRes);
        tvLoginError.setVisibility(View.VISIBLE);
    }
}

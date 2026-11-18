package com.example.appointmentmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
    private DatabaseHelper db;
    private EditText etUsername, etPassword;
    private TextView tvError;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DatabaseHelper(this);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);
        Button btn = findViewById(R.id.btnLogin);
        btn.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString();
            if (username.isEmpty()) { etUsername.setError("Vui lòng nhập tên đăng nhập"); return; }
            if (password.isEmpty()) { etPassword.setError("Vui lòng nhập mật khẩu"); return; }
            if (db.checkLogin(username, password)) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                tvError.setText("Sai tên đăng nhập hoặc mật khẩu");
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }
    @Override protected void onDestroy() { super.onDestroy(); if (db != null) db.close(); }
}

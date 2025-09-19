package com.example.silkroad_iot.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import androidx.appcompat.app.AppCompatActivity;
import com.example.silkroad_iot.databinding.ActivityForgotPasswordBinding;
import com.google.android.material.snackbar.Snackbar;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Olvidé mi contraseña");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        b.toolbar.setNavigationOnClickListener(v -> finish());

        b.btnSendCode.setOnClickListener(v -> {
            String email = b.inputEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                b.inputEmail.setError("Requerido");
            } else {
                Snackbar.make(b.getRoot(), "Se envió código a " + email, Snackbar.LENGTH_LONG).show();
                // Aquí luego integras Firebase Auth o tu lógica de envío de código
            }
        });
    }
}
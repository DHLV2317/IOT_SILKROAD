package com.example.silkroad_iot.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.appcompat.app.AppCompatActivity;
import com.example.silkroad_iot.MainActivity;
import com.example.silkroad_iot.databinding.ActivityRegisterVerifyBinding;
import com.example.silkroad_iot.data.UserStore;
import com.google.android.material.snackbar.Snackbar;

public class RegisterVerifyActivity extends AppCompatActivity {
    private ActivityRegisterVerifyBinding b;
    private final UserStore store = UserStore.get();
    private String email;

    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        b = ActivityRegisterVerifyBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        b.toolbar.setNavigationOnClickListener(v -> finish());

        email = getIntent().getStringExtra("email");
        b.tvInfo.setText("A 4-digit code was sent to " + email);

        b.tvResend.setOnClickListener(v -> {
            String code = store.resendRegistrationCode(email);
            Snackbar.make(b.getRoot(), "Nuevo código: " + code, Snackbar.LENGTH_LONG).show();
        });

        b.btnContinue.setOnClickListener(v -> {
            String code = b.inputCode.getText().toString().trim();
            if (TextUtils.isEmpty(code)){ b.inputCode.setError("Requerido"); return; }
            if (code.length()!=4){ b.inputCode.setError("Código de 4 dígitos"); return; }

            if (!store.verifyRegistrationCode(email, code)) {
                b.inputCode.setError("Código inválido");
                return;
            }
            store.finalizeRegistration(email);
            Snackbar.make(b.getRoot(), "Registro verificado. Ahora puedes iniciar sesión.", Snackbar.LENGTH_LONG).show();

            Intent backToLogin = new Intent(this, MainActivity.class);
            backToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(backToLogin);
            finish();
        });
    }
}
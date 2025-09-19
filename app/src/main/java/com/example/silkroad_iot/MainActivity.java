package com.example.silkroad_iot;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.appcompat.app.AppCompatActivity;
import com.example.silkroad_iot.databinding.ActivityMainBinding;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.ui.auth.ForgotPasswordActivity;
import com.example.silkroad_iot.ui.auth.RegisterActivity;
import com.example.silkroad_iot.ui.client.ClientHomeActivity;
import com.example.silkroad_iot.ui.client.ClientOnboardingActivity;
import com.example.silkroad_iot.ui.guide.GuideHomeActivity;
import com.example.silkroad_iot.ui.admin.AdminHomeActivity;
import com.example.silkroad_iot.ui.superadmin.SuperAdminHomeActivity;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding b;
    private final UserStore store = UserStore.get();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);

        // Login
        b.btnLogin.setOnClickListener(v -> {
            String email = b.inputEmail.getText().toString().trim();
            String pass  = b.inputPass.getText().toString().trim();

            if (TextUtils.isEmpty(email)) { b.inputEmail.setError("Requerido"); return; }
            if (TextUtils.isEmpty(pass))  { b.inputPass.setError("Requerido"); return; }

            if (store.login(email, pass)) {
                User u = store.getLogged();
                Intent next;

                if (u.getRole() == User.Role.CLIENT) {
                    if (u.isClientProfileCompleted()) {
                        next = new Intent(this, ClientHomeActivity.class);
                    } else {
                        next = new Intent(this, ClientOnboardingActivity.class);
                    }
                } else if (u.getRole() == User.Role.GUIDE) {
                    next = new Intent(this, GuideHomeActivity.class);
                } else if (u.getRole() == User.Role.ADMIN) {
                    next = new Intent(this, AdminHomeActivity.class);
                } else if (u.getRole() == User.Role.SUPERADMIN) {
                    next = new Intent(this, SuperAdminHomeActivity.class);
                } else {
                    next = new Intent(this, ClientHomeActivity.class);
                }

                startActivity(next);
                finish();
            } else {
                Snackbar.make(b.getRoot(), "Credenciales inválidas", Snackbar.LENGTH_SHORT).show();
            }
        });

        // Ir a registro
        b.btnGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        // Forgot password
        b.tvForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }
}
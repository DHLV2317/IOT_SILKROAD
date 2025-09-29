package com.example.silkroad_iot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.data.AdminRepository;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityMainBinding;
import com.example.silkroad_iot.ui.admin.AdminCompanyDetailActivity;
import com.example.silkroad_iot.ui.admin.AdminToursActivity;
import com.example.silkroad_iot.ui.auth.ForgotPasswordActivity;
import com.example.silkroad_iot.ui.auth.RegisterActivity;
import com.example.silkroad_iot.ui.client.ClientHomeActivity;
import com.example.silkroad_iot.ui.client.ClientOnboardingActivity;
import com.example.silkroad_iot.ui.guide.GuideHomeActivity;
import com.example.silkroad_iot.ui.guide.GuidePendingApprovalActivity;
import com.example.silkroad_iot.ui.superadmin.SuperAdminHomeActivity;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding b;
    private final UserStore store = UserStore.get();

    private static final String PREFS = "app_prefs";
    private static final String KEY_COMPANY_DONE = "admin_company_done";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);

        b.btnLogin.setOnClickListener(v -> {
            String email = b.inputEmail.getText().toString().trim();
            String pass  = b.inputPass.getText().toString().trim();

            if (TextUtils.isEmpty(email)) { b.inputEmail.setError("Requerido"); return; }
            if (TextUtils.isEmpty(pass))  { b.inputPass.setError("Requerido");  return; }

            if (store.login(email, pass)) {
                User u = store.getLogged();
                Intent next;

                if (u.getRole() == User.Role.CLIENT) {
                    next = u.isClientProfileCompleted()
                            ? new Intent(this, ClientHomeActivity.class)
                            : new Intent(this, ClientOnboardingActivity.class);

                } else if (u.getRole() == User.Role.GUIDE) {
                    // Guía: si no está aprobado -> pantalla de pendiente
                    next = u.isGuideApproved()
                            ? new Intent(this, GuideHomeActivity.class)
                            : new Intent(this, GuidePendingApprovalActivity.class);

                } else if (u.getRole() == User.Role.ADMIN) {
                    // Admin: primera vez -> completar Empresa
                    AdminRepository.Company c = AdminRepository.get().getOrCreateCompany();
                    boolean incompleto = c == null ||
                            TextUtils.isEmpty(c.name) ||
                            TextUtils.isEmpty(c.email) ||
                            TextUtils.isEmpty(c.phone) ||
                            TextUtils.isEmpty(c.address);

                    SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
                    boolean marcado = sp.getBoolean(KEY_COMPANY_DONE, false);

                    next = (incompleto || !marcado)
                            ? new Intent(this, AdminCompanyDetailActivity.class).putExtra("firstRun", true)
                            : new Intent(this, AdminToursActivity.class);

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

        b.btnGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, SuperAdminHomeActivity.class)));

        b.tvForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }
}
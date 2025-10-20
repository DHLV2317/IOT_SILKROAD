package com.example.silkroad_iot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;
    private final UserStore store = UserStore.get();

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private static final String PREFS = "app_prefs";
    private static final String KEY_COMPANY_DONE = "admin_company_done";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);

        // 🔹 Inicializa Firebase
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        b.btnLogin.setOnClickListener(v -> doLogin());
        b.btnGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
        b.tvForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }

    // ======================================================
    // 🔐 LOGIN
    // ======================================================
    private void doLogin() {
        String email = safe(b.inputEmail.getText());
        String pass  = safe(b.inputPass.getText());

        if (email.isEmpty()) { b.inputEmail.setError("Requerido"); return; }
        if (pass.isEmpty())  { b.inputPass.setError("Requerido");  return; }

        setLoading(true);

        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(res -> {
                    // 1️⃣ Buscar el usuario en Firestore (colección "usuarios")
                    db.collection("usuarios")
                            .whereEqualTo("email", email)
                            .limit(1)
                            .get()
                            .addOnSuccessListener(snap -> {
                                setLoading(false);

                                if (snap.isEmpty()) {
                                    Snackbar.make(b.getRoot(),
                                            "No se encontró el perfil en Firestore (colección 'usuarios').",
                                            Snackbar.LENGTH_LONG).show();
                                    return;
                                }

                                DocumentSnapshot d = snap.getDocuments().get(0);

                                // 2️⃣ Mapear a modelo local
                                User u = new User();
                                u.setEmail(email);
                                u.setUid(auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null);
                                u.setName(nz(d.getString("nombre"))); // usamos campo "nombre"
                                String rol = nz(d.getString("rol")).toLowerCase();

                                switch (rol) {
                                    case "empresa":
                                    case "admin":
                                        u.setRole(User.Role.ADMIN);
                                        break;
                                    case "guia":
                                        u.setRole(User.Role.GUIDE);
                                        Boolean aprobado = d.getBoolean("aprobado");
                                        u.setGuideApproved(aprobado != null && aprobado);
                                        u.setGuideApprovalStatus(aprobado != null && aprobado ? "APPROVED" : "PENDING");
                                        break;
                                    case "superadmin":
                                        u.setRole(User.Role.SUPERADMIN);
                                        break;
                                    default:
                                        u.setRole(User.Role.CLIENT);
                                        u.setClientProfileCompleted(true);
                                }

                                // 3️⃣ IDs opcionales
                                u.setCompanyId(nz(d.getString("empresaId")));
                                u.setGuideId(nz(d.getString("guiaId")));

                                // 4️⃣ Guardar sesión y redirigir
                                store.setLogged(u);
                                routeAfterLogin(u);
                            })
                            .addOnFailureListener(e -> {
                                setLoading(false);
                                Snackbar.make(b.getRoot(),
                                        "Error leyendo Firestore: " + e.getMessage(),
                                        Snackbar.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Snackbar.make(b.getRoot(),
                            "Login fallido: " + e.getMessage(),
                            Snackbar.LENGTH_LONG).show();
                });
    }

    // ======================================================
    // 🧭 RUTEO SEGÚN ROL
    // ======================================================
    private void routeAfterLogin(User u) {
        Intent next;

        switch (u.getRole()) {
            case CLIENT:
                next = u.isClientProfileCompleted()
                        ? new Intent(this, ClientHomeActivity.class)
                        : new Intent(this, ClientOnboardingActivity.class);
                break;

            case GUIDE:
                next = u.isGuideApproved()
                        ? new Intent(this, GuideHomeActivity.class)
                        : new Intent(this, GuidePendingApprovalActivity.class);
                break;

            case ADMIN:
                // Si aún usas la verificación de empresa local:
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
                break;

            case SUPERADMIN:
                next = new Intent(this, SuperAdminHomeActivity.class);
                break;

            default:
                next = new Intent(this, ClientHomeActivity.class);
        }

        startActivity(next);
        finish();
    }

    // ======================================================
    // 🧩 HELPERS
    // ======================================================
    private void setLoading(boolean loading) {
        b.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        b.btnLogin.setEnabled(!loading);
        b.btnGoRegister.setEnabled(!loading);
        b.tvForgot.setEnabled(!loading);
    }

    private static String safe(CharSequence cs){ return cs == null ? "" : cs.toString().trim(); }
    private static String nz(String s){ return s == null ? "" : s; }
}
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

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        b.btnLogin.setOnClickListener(v -> doLogin());
        b.btnGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
        b.tvForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }

    private void doLogin() {
        String email = safe(b.inputEmail.getText());
        String pass  = safe(b.inputPass.getText());

        if (email.isEmpty()) { b.inputEmail.setError("Requerido"); return; }
        if (pass.isEmpty())  { b.inputPass.setError("Requerido");  return; }

        setLoading(true);

        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(res -> {
                    db.collection("usuarios")
                            .whereEqualTo("email", email)
                            .limit(1)
                            .get()
                            .addOnSuccessListener(snap -> {
                                if (snap.isEmpty()) {
                                    setLoading(false);
                                    Snackbar.make(b.getRoot(),
                                            "No se encontró el perfil en 'usuarios'.",
                                            Snackbar.LENGTH_LONG).show();
                                    return;
                                }

                                DocumentSnapshot d = snap.getDocuments().get(0);

                                // 1) Construir User base
                                User u = new User();
                                u.setEmail(email);
                                u.setUid(auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null);
                                u.setName(nz(d.getString("nombre")));
                                u.setCompanyId(nz(d.getString("empresaId")));
                                u.setGuideId(nz(d.getString("guiaId")));

                                // 2) Rol desde doc (puede venir vacío)
                                String rolRaw = nz(d.getString("rol")).trim().toLowerCase();
                                if (rolRaw.equals("empresa") || rolRaw.equals("admin")) {
                                    u.setRole(User.Role.ADMIN);
                                } else if (rolRaw.equals("guia") || rolRaw.equals("guide")) {
                                    u.setRole(User.Role.GUIDE);
                                } else if (rolRaw.equals("superadmin")) {
                                    u.setRole(User.Role.SUPERADMIN);
                                } else if (rolRaw.equals("cliente") || rolRaw.equals("client")) {
                                    u.setRole(User.Role.CLIENT);
                                } else {
                                    // Inferir si no hay "rol"
                                    if (!u.getGuideId().isEmpty()) {
                                        u.setRole(User.Role.GUIDE);
                                    } else {
                                        u.setRole(User.Role.CLIENT); // temporal, luego intentamos mejorar
                                    }
                                }

                                // 3) Aprobación guía (acepta varios nombres de campo)
                                boolean approvedBool =
                                        (d.getBoolean("aprobado") != null && d.getBoolean("aprobado")) ||
                                                (d.getBoolean("guideApproved") != null && d.getBoolean("guideApproved"));
                                String status = nz(d.getString("guideApprovalStatus"));
                                if (approvedBool) status = "APPROVED";
                                if (status.isEmpty()) status = "PENDING";
                                u.setGuideApproved(approvedBool ||
                                        "approved".equalsIgnoreCase(status) ||
                                        "aprobado".equalsIgnoreCase(status));
                                u.setGuideApprovalStatus(status.toUpperCase());

                                // 4) Si ya tenemos un rol fiable -> rutear
                                //    Si quedó como CLIENT, intentamos inferir ADMIN por colecciones secundarias y por correos demo.
                                if (u.getRole() != User.Role.CLIENT) {
                                    setLoading(false);
                                    store.setLogged(u);
                                    routeAfterLogin(u);
                                } else {
                                    inferAdminOrDemoAndRoute(u);
                                }
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

    /** Si el rol quedó como CLIENT, intentamos:
     *  a) Buscar en 'administradores' por correo/email → ADMIN
     *  b) Fallback por correos de demo → ADMIN / SUPERADMIN / GUIDE
     */
    private void inferAdminOrDemoAndRoute(User u) {
        final String email = u.getEmail();

        db.collection("administradores")
                .whereEqualTo("correo", email)
                .limit(1)
                .get()
                .addOnSuccessListener(s1 -> {
                    if (!s1.isEmpty()) {
                        u.setRole(User.Role.ADMIN);
                        finishRouting(u);
                        return;
                    }
                    // Intentar con campo "email" (por si usas otro nombre)
                    db.collection("administradores")
                            .whereEqualTo("email", email)
                            .limit(1)
                            .get()
                            .addOnSuccessListener(s2 -> {
                                if (!s2.isEmpty()) {
                                    u.setRole(User.Role.ADMIN);
                                    finishRouting(u);
                                } else {
                                    // Fallback de cuentas demo
                                    applyDemoFallback(u);
                                    finishRouting(u);
                                }
                            })
                            .addOnFailureListener(e -> {
                                applyDemoFallback(u);
                                finishRouting(u);
                            });
                })
                .addOnFailureListener(e -> {
                    applyDemoFallback(u);
                    finishRouting(u);
                });
    }

    private void applyDemoFallback(User u) {
        String email = nz(u.getEmail()).toLowerCase();
        if (email.equals("admin@demo.com")) {
            u.setRole(User.Role.ADMIN);
        } else if (email.equals("superadmin@demo.com")) {
            u.setRole(User.Role.SUPERADMIN);
        } else if (email.equals("guide@demo.com")) {
            u.setRole(User.Role.GUIDE);
            u.setGuideApproved(true);
            u.setGuideApprovalStatus("APPROVED");
        } else if (email.equals("client@demo.com")) {
            u.setRole(User.Role.CLIENT);
            u.setClientProfileCompleted(true);
        }
    }

    private void finishRouting(User u) {
        setLoading(false);
        store.setLogged(u);
        routeAfterLogin(u);
    }

    // ========================== RUTEO (if/else) ==========================
    private void routeAfterLogin(User u) {
        Intent next;

        if (u.getRole() == User.Role.CLIENT) {
            if (u.isClientProfileCompleted()) {
                next = new Intent(this, ClientHomeActivity.class);
            } else {
                next = new Intent(this, ClientOnboardingActivity.class);
            }
        } else if (u.getRole() == User.Role.GUIDE) {
            if (u.isGuideApproved()) {
                next = new Intent(this, GuideHomeActivity.class);
            } else {
                next = new Intent(this, GuidePendingApprovalActivity.class);
            }
        } else if (u.getRole() == User.Role.ADMIN) {
            AdminRepository.Company c = AdminRepository.get().getOrCreateCompany();
            boolean incompleto = c == null ||
                    TextUtils.isEmpty(c.name) ||
                    TextUtils.isEmpty(c.email) ||
                    TextUtils.isEmpty(c.phone) ||
                    TextUtils.isEmpty(c.address);

            SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
            boolean marcado = sp.getBoolean(KEY_COMPANY_DONE, false);

            if (incompleto || !marcado) {
                next = new Intent(this, AdminCompanyDetailActivity.class).putExtra("firstRun", true);
            } else {
                next = new Intent(this, AdminToursActivity.class);
            }
        } else if (u.getRole() == User.Role.SUPERADMIN) {
            next = new Intent(this, SuperAdminHomeActivity.class);
        } else {
            next = new Intent(this, ClientHomeActivity.class);
        }

        next.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(next);
        finish();
    }

    // ============================ HELPERS ============================
    private void setLoading(boolean loading) {
        b.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        b.btnLogin.setEnabled(!loading);
        b.btnGoRegister.setEnabled(!loading);
        b.tvForgot.setEnabled(!loading);
    }

    private static String safe(CharSequence cs){ return cs == null ? "" : cs.toString().trim(); }
    private static String nz(String s){ return s == null ? "" : s; }
}
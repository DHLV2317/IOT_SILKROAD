package com.example.silkroad_iot.ui.guide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.MainActivity;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityGuidePendingApprovalBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Date;

public class GuidePendingApprovalActivity extends AppCompatActivity {

    private ActivityGuidePendingApprovalBinding binding;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final UserStore store = UserStore.get();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuidePendingApprovalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Cuenta Pendiente");

        setupUserInfo();
        setupClickListeners();
    }

    private void setupUserInfo() {
        User guide = store.getLogged();
        if (guide == null) return;

        String ln = guide.getLastName() == null ? "" : (" " + guide.getLastName());
        binding.txtGuideName.setText((guide.getName() == null ? "" : guide.getName()) + ln);
        binding.txtGuideEmail.setText(guide.getEmail());

        if (guide.getDocumentNumber() != null) {
            String dt = guide.getDocumentType() == null ? "Doc" : guide.getDocumentType();
            binding.txtDocumentInfo.setText(dt + ": " + guide.getDocumentNumber());
        }
        if (guide.getPhone() != null) binding.txtPhoneInfo.setText("Teléfono: " + guide.getPhone());
        if (guide.getLanguages() != null) binding.txtLanguagesInfo.setText("Idiomas: " + guide.getLanguages());

        showStatus(guide.getGuideApprovalStatus());
    }

    private void setupClickListeners() {
        binding.btnBackToLogin.setOnClickListener(v -> {
            store.logout();
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        });

        binding.btnRefreshStatus.setOnClickListener(v -> refreshFromFirestore());
    }

    private void setLoading(boolean loading) {
        binding.btnRefreshStatus.setEnabled(!loading);
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showStatus(String status) {
        if (status == null) status = "PENDING";
        switch (status) {
            case "PENDING":
                binding.txtStatusInfo.setText("⏳ Pendiente de Aprobación");
                binding.txtStatusDescription.setText("Tu solicitud está siendo revisada por nuestro equipo de administradores.");
                break;
            case "REJECTED":
                binding.txtStatusInfo.setText("❌ Solicitud Rechazada");
                binding.txtStatusDescription.setText("Lo sentimos, tu solicitud ha sido rechazada. Contacta con soporte para más información.");
                break;
            case "APPROVED":
                binding.txtStatusInfo.setText("✅ Aprobado");
                binding.txtStatusDescription.setText("¡Listo! Puedes ingresar a tu panel de guía.");
                break;
            default:
                binding.txtStatusInfo.setText("⏳ En Revisión");
                binding.txtStatusDescription.setText("Tu solicitud está siendo procesada.");
        }
    }

    /** Consulta Firestore: colección 'usuarios', búsqueda por email, lee 'aprobado' y 'guideApprovalStatus' */
    private void refreshFromFirestore() {
        User u = store.getLogged();
        if (u == null || u.getEmail() == null || u.getEmail().isEmpty()) {
            Snackbar.make(binding.getRoot(), "No hay sesión de guía cargada.", Snackbar.LENGTH_LONG).show();
            return;
        }

        setLoading(true);
        db.collection("usuarios")
                .whereEqualTo("email", u.getEmail())
                .limit(1)
                .get()
                .addOnSuccessListener(snap -> {
                    setLoading(false);
                    if (snap.isEmpty()) {
                        Snackbar.make(binding.getRoot(), "No se encontró tu perfil en Firestore.", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    DocumentSnapshot d = snap.getDocuments().get(0);

                    // Campo oficial de aprobación (bool)
                    Boolean aprobado = d.getBoolean("aprobado");
                    // Campo textual opcional
                    String status = d.getString("guideApprovalStatus");

                    // Normalizamos: si 'aprobado' true => status APPROVED
                    if (aprobado != null && aprobado) status = "APPROVED";
                    if (status == null) status = (aprobado != null && aprobado) ? "APPROVED" : "PENDING";

                    // Actualiza el usuario en memoria
                    u.setGuideApproved(aprobado != null && aprobado);
                    u.setGuideApprovalStatus(status);
                    store.updateLogged(u);

                    // Pinta UI
                    showStatus(status);
                    binding.txtLastCheck.setText("Última verificación: " +
                            DateFormat.getDateTimeInstance().format(new Date()));

                    // Si ya fue aprobado, navega al Home
                    if (u.isGuideApproved()) {
                        Toast.makeText(this, "¡Tu cuenta de guía ya fue aprobada!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, GuideHomeActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Snackbar.make(binding.getRoot(), "Error consultando estado: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                });
    }
}
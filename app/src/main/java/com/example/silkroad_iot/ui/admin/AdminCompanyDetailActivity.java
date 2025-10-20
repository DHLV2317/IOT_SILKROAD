package com.example.silkroad_iot.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.data.EmpresaFb;
import com.example.silkroad_iot.databinding.ActivityAdminCompanyDetailBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminCompanyDetailActivity extends AppCompatActivity {

    private ActivityAdminCompanyDetailBinding b;
    private FirebaseFirestore db;
    private EmpresaFb empresa; // modelo Firestore
    private boolean firstRun;

    private static final String PREFS = "app_prefs";
    private static final String KEY_COMPANY_DONE = "admin_company_done";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityAdminCompanyDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Empresa");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        b.toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        firstRun = getIntent().getBooleanExtra("firstRun", false);
        String id = getIntent().getStringExtra("id");

        if (id != null && !id.isEmpty()) {
            cargarEmpresaDesdeFirestore(id);
        } else {
            empresa = new EmpresaFb();
        }

        b.btnSaveCompany.setOnClickListener(v -> guardarEmpresaEnFirestore());
    }

    /**
     * Carga la empresa desde Firestore según su ID.
     */
    private void cargarEmpresaDesdeFirestore(String id) {
        db.collection("empresas").document(id).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        empresa = doc.toObject(EmpresaFb.class);
                        if (empresa != null) empresa.setId(doc.getId());
                        rellenarCampos();
                    } else {
                        Toast.makeText(this, "No se encontró la empresa", Toast.LENGTH_SHORT).show();
                        empresa = new EmpresaFb();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    /**
     * Llena los inputs con la data de Firestore.
     */
    private void rellenarCampos() {
        if (empresa == null) return;
        b.inputCompanyName.setText(nullToEmpty(empresa.getNombre()));
        b.inputEmail.setText(nullToEmpty(empresa.getEmail()));
        b.inputPhone.setText(nullToEmpty(empresa.getTelefono()));
        b.inputAddress.setText(nullToEmpty(empresa.getDireccion()));
        b.inputLat.setText(String.valueOf(empresa.getLat()));
        b.inputLng.setText(String.valueOf(empresa.getLng()));
    }

    /**
     * Guarda o actualiza la empresa en Firestore.
     */
    private void guardarEmpresaEnFirestore() {
        String name = b.inputCompanyName.getText().toString().trim();
        String email = b.inputEmail.getText().toString().trim();
        String phone = b.inputPhone.getText().toString().trim();
        String address = b.inputAddress.getText().toString().trim();
        String sLat = b.inputLat.getText().toString().trim();
        String sLng = b.inputLng.getText().toString().trim();

        if (TextUtils.isEmpty(name)) { b.inputCompanyName.setError("Requerido"); return; }
        if (TextUtils.isEmpty(email)) { b.inputEmail.setError("Requerido"); return; }
        if (TextUtils.isEmpty(phone)) { b.inputPhone.setError("Requerido"); return; }
        if (TextUtils.isEmpty(address)) { b.inputAddress.setError("Requerido"); return; }

        double lat = safeDouble(sLat);
        double lng = safeDouble(sLng);

        empresa.setNombre(name);
        empresa.setEmail(email);
        empresa.setTelefono(phone);
        empresa.setDireccion(address);
        empresa.setLat(lat);
        empresa.setLng(lng);

        // 🔹 Guardar en Firestore
        if (empresa.getId() != null && !empresa.getId().isEmpty()) {
            // Actualizar documento existente
            db.collection("empresas").document(empresa.getId())
                    .set(empresa)
                    .addOnSuccessListener(aVoid -> onSaveSuccess("Empresa actualizada"))
                    .addOnFailureListener(e -> showError(e.getMessage()));
        } else {
            // Crear nuevo documento
            DocumentReference ref = db.collection("empresas").document();
            empresa.setId(ref.getId());
            ref.set(empresa)
                    .addOnSuccessListener(aVoid -> onSaveSuccess("Empresa registrada"))
                    .addOnFailureListener(e -> showError(e.getMessage()));
        }
    }

    private void onSaveSuccess(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit().putBoolean(KEY_COMPANY_DONE, true).apply();

        if (firstRun) {
            Intent i = new Intent(this, AdminToursActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finishAffinity();
        } else {
            finish();
        }
    }

    private void showError(String msg) {
        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_LONG).show();
    }

    private String nullToEmpty(String s) { return s == null ? "" : s; }

    private double safeDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0d; }
    }
}
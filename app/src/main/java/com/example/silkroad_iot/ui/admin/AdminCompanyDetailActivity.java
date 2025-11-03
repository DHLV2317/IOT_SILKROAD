package com.example.silkroad_iot.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.EmpresaFb;
import com.example.silkroad_iot.databinding.ActivityAdminCompanyDetailBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminCompanyDetailActivity extends AppCompatActivity {

    private ActivityAdminCompanyDetailBinding b;
    private FirebaseFirestore db;
    private EmpresaFb empresa;
    private boolean firstRun;

    private static final String PREFS = "app_prefs";
    private static final String KEY_COMPANY_DONE = "admin_company_done";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityAdminCompanyDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // Toolbar
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Empresa");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        b.toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        firstRun = getIntent().getBooleanExtra("firstRun", false);
        String id = getIntent().getStringExtra("id");

        if (!TextUtils.isEmpty(id)) {
            cargarEmpresaDesdeFirestore(id);
        } else {
            empresa = new EmpresaFb();
            // Imagen por defecto en header
            Glide.with(this)
                    .load(R.drawable.ic_image_24)
                    .into(b.imgLogo);
        }

        b.btnSaveCompany.setOnClickListener(v -> guardarEmpresaEnFirestore());

        // (Opcional) Cambiar logo: aquí abrirías un picker y luego subes a Storage
        b.btnChangePhoto.setOnClickListener(v ->
                Toast.makeText(this, "Implementar picker de imagen y subir a Storage", Toast.LENGTH_SHORT).show()
        );
    }

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

    private void rellenarCampos() {
        if (empresa == null) return;

        b.inputCompanyName.setText(n(empresa.getNombre()));
        b.inputEmail.setText(n(empresa.getEmail()));
        b.inputPhone.setText(n(empresa.getTelefono()));
        b.inputAddress.setText(n(empresa.getDireccion()));
        b.inputLat.setText(String.valueOf(empresa.getLat()));
        b.inputLng.setText(String.valueOf(empresa.getLng()));

        // Título bajo el header
        b.txtCompanyTitle.setText(n(empresa.getNombre()).isEmpty() ? "Nombre de la empresa" : empresa.getNombre());

        // Cargar imagen (campo 'imagen')
        if (!TextUtils.isEmpty(empresa.getImagen())) {
            Glide.with(this)
                    .load(empresa.getImagen())
                    .placeholder(R.drawable.ic_image_24)
                    .error(R.drawable.ic_image_24)
                    .centerCrop()
                    .into(b.imgLogo);
        } else {
            Glide.with(this).load(R.drawable.ic_image_24).into(b.imgLogo);
        }
    }

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

        double lat = pDouble(sLat);
        double lng = pDouble(sLng);

        if (empresa == null) empresa = new EmpresaFb();
        empresa.setNombre(name);
        empresa.setEmail(email);
        empresa.setTelefono(phone);
        empresa.setDireccion(address);
        empresa.setLat(lat);
        empresa.setLng(lng);

        // Guardar/actualizar
        if (!TextUtils.isEmpty(empresa.getId())) {
            db.collection("empresas").document(empresa.getId())
                    .set(empresa)
                    .addOnSuccessListener(aVoid -> onSaveSuccess("Empresa actualizada"))
                    .addOnFailureListener(e -> showError(e.getMessage()));
        } else {
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

    private void showError(String m) { Toast.makeText(this, "Error: " + m, Toast.LENGTH_LONG).show(); }
    private String n(String s) { return s == null ? "" : s; }
    private double pDouble(String s) { try { return Double.parseDouble(s); } catch (Exception e) { return 0d; } }
}
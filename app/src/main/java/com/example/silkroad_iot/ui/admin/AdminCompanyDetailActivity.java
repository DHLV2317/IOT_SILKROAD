package com.example.silkroad_iot.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.data.AdminRepository;
import com.example.silkroad_iot.databinding.ActivityAdminCompanyDetailBinding;

public class AdminCompanyDetailActivity extends AppCompatActivity {
    private ActivityAdminCompanyDetailBinding b;
    private final AdminRepository repo = AdminRepository.get();
    private AdminRepository.Company model;
    private boolean firstRun;

    private static final String PREFS = "app_prefs";
    private static final String KEY_COMPANY_DONE = "admin_company_done";

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        b = ActivityAdminCompanyDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Empresa");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        b.toolbar.setNavigationOnClickListener(v -> finish());

        firstRun = getIntent().getBooleanExtra("firstRun", false);
        String id = getIntent().getStringExtra("id");
        model = (id != null) ? repo.findCompany(id) : repo.getOrCreateCompany();

        if (model != null){
            b.inputCompanyName.setText(nullToEmpty(model.name));
            b.inputEmail.setText(nullToEmpty(model.email));
            b.inputPhone.setText(nullToEmpty(model.phone));
            b.inputAddress.setText(nullToEmpty(model.address));
            b.inputLat.setText(String.valueOf(model.lat));
            b.inputLng.setText(String.valueOf(model.lng));
        }

        b.btnSaveCompany.setOnClickListener(v -> {
            String name = b.inputCompanyName.getText().toString().trim();
            String email = b.inputEmail.getText().toString().trim();
            String phone = b.inputPhone.getText().toString().trim();
            String address = b.inputAddress.getText().toString().trim();
            String sLat = b.inputLat.getText().toString().trim();
            String sLng = b.inputLng.getText().toString().trim();

            if (TextUtils.isEmpty(name))  { b.inputCompanyName.setError("Requerido"); return; }
            if (TextUtils.isEmpty(email)) { b.inputEmail.setError("Requerido"); return; }
            if (TextUtils.isEmpty(phone)) { b.inputPhone.setError("Requerido"); return; }
            if (TextUtils.isEmpty(address)){ b.inputAddress.setError("Requerido"); return; }

            double lat = safeDouble(sLat);
            double lng = safeDouble(sLng);

            model.name = name;
            model.email = email;
            model.phone = phone;
            model.address = address;
            model.lat = lat;
            model.lng = lng;

            // marcar como completado
            getSharedPreferences(PREFS, MODE_PRIVATE)
                    .edit().putBoolean(KEY_COMPANY_DONE, true).apply();

            if (firstRun) {
                // primera vez -> ir a Tours y limpiar back stack
                startActivity(new Intent(this, AdminToursActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                finishAffinity();
            } else {
                finish();
            }
        });
    }

    private String nullToEmpty(String s){ return s==null? "" : s; }
    private double safeDouble(String s){
        try { return Double.parseDouble(s); } catch (Exception e){ return 0d; }
    }
}
package com.example.silkroad_iot.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.AdminRepository;
import com.example.silkroad_iot.databinding.ActivityAdminGuideDetailBinding;

public class AdminGuideDetailActivity extends AppCompatActivity {

    private ActivityAdminGuideDetailBinding b;
    private final AdminRepository repo = AdminRepository.get();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityAdminGuideDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        b.toolbar.setNavigationOnClickListener(v -> finish());
        setTitle("Detalles del guía");

        int index = getIntent().getIntExtra("index", -1);
        if (index < 0 || index >= repo.getGuides().size()) { finish(); return; }
        AdminRepository.Guide g = repo.getGuides().get(index);

        Glide.with(this).load(R.drawable.ic_person_24).into(b.img);

        b.tName.setText(empty(g.name));
        b.tLangs.setText(empty(g.langs));
        b.tState.setText(empty(g.state));
        b.tEmail.setText(TextUtils.isEmpty(g.email) ? "—" : g.email);
        b.tPhone.setText(TextUtils.isEmpty(g.phone) ? "—" : g.phone);
        b.tCurrentTour.setText(TextUtils.isEmpty(g.currentTour) ? "Ninguno" : g.currentTour);

        // Historial simple (una línea por tour)
        b.boxHistory.removeAllViews();
        if (g.history != null && !g.history.isEmpty()){
            for (String s : g.history){
                android.widget.TextView tv = new android.widget.TextView(this);
                tv.setText("• " + s);
                b.boxHistory.addView(tv);
            }
        } else {
            b.tNoHistory.setVisibility(View.VISIBLE);
        }

        b.btnBack.setOnClickListener(v -> finish());
    }

    private String empty(String s){ return TextUtils.isEmpty(s) ? "—" : s; }
}
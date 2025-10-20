package com.example.silkroad_iot.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.ui.common.BaseDrawerActivity;

public class AdminProfileActivity extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inserta tu layout dentro del drawer y usa el toolbar del drawer
        setupDrawer(R.layout.activity_admin_profile, R.menu.menu_drawer_admin, "Perfil");

        ImageView img = findViewById(R.id.imgProfile);
        Button btn = findViewById(R.id.btnEditCompany);

        Glide.with(this).load(R.drawable.ic_launcher_foreground).into(img);

        // Abre la pantalla de edición de empresa que YA tienes
        Runnable goCompany = () ->
                startActivity(new Intent(this, AdminCompanyDetailActivity.class));

        img.setOnClickListener(v -> goCompany.run());
        if (btn != null) btn.setOnClickListener(v -> goCompany.run());
    }

    // No hay item "Perfil" en el menú; no marcamos ninguno
    @Override protected int defaultMenuId() { return 0; }
}
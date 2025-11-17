package com.example.silkroad_iot.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.ui.common.BaseDrawerActivity;

public class AdminProfileActivity extends BaseDrawerActivity {

    private static final String PREFS = "app_prefs";
    private static final String KEY_ADMIN_NAME  = "admin_name";
    private static final String KEY_ADMIN_EMAIL = "admin_email";
    private static final String KEY_ADMIN_PHOTO = "admin_photo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_admin_profile, R.menu.menu_drawer_admin, "Perfil");

        ImageView img   = findViewById(R.id.imgProfile);
        TextView tName  = findViewById(R.id.tName);
        TextView tEmail = findViewById(R.id.tEmail);
        Button btn      = findViewById(R.id.btnEditCompany);

        var prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String name  = prefs.getString(KEY_ADMIN_NAME,  "Admin");
        String email = prefs.getString(KEY_ADMIN_EMAIL, "admin@demo.com");
        String photo = prefs.getString(KEY_ADMIN_PHOTO, "");

        tName.setText(name);
        tEmail.setText(email);

        Glide.with(this)
                .load(photo.isEmpty() ? R.drawable.ic_person_24 : photo)
                .placeholder(R.drawable.ic_person_24)
                .error(R.drawable.ic_person_24)
                .into(img);

        img.setOnClickListener(v ->
                startActivity(new Intent(this, AdminCompanyDetailActivity.class))
        );
        btn.setOnClickListener(v ->
                startActivity(new Intent(this, AdminCompanyDetailActivity.class))
        );
    }

    @Override
    protected int defaultMenuId() { return 0; }
}
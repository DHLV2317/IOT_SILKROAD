package com.example.silkroad_iot.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.AdminRepository;
import com.example.silkroad_iot.databinding.ContentAdminToursBinding;
import com.example.silkroad_iot.ui.common.BaseDrawerActivity;

public class AdminToursActivity extends BaseDrawerActivity {
    private ContentAdminToursBinding c;
    private final AdminRepository repo = AdminRepository.get();
    private AdminToursAdapter adapter;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setupDrawer(R.layout.content_admin_tours, R.menu.menu_drawer_admin, "Mis Tours");

        FrameLayout container = findViewById(R.id.contentContainer);
        c = ContentAdminToursBinding.bind(container.getChildAt(0));

        c.recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminToursAdapter(repo.getTours());
        c.recycler.setAdapter(adapter);

        c.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s == null ? "" : s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // (+) → Wizard
        c.btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AdminTourWizardActivity.class)));
    }

    // Vuelve del wizard y recarga lista
    @Override protected void onResume() {
        super.onResume();
        adapter = new AdminToursAdapter(repo.getTours());
        c.recycler.setAdapter(adapter);
    }

    @Override protected int defaultMenuId() { return R.id.m_tours; }
}
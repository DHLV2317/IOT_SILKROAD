package com.example.silkroad_iot.ui.superadmin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.silkroad_iot.databinding.ActivitySuperAdminHomeBinding;

public class SuperAdminHomeActivity extends AppCompatActivity {
    private ActivitySuperAdminHomeBinding b;
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        b = ActivitySuperAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar()!=null) getSupportActionBar().setTitle("SuperAdmin - Inicio");
    }
}
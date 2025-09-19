package com.example.silkroad_iot.ui.guide;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.silkroad_iot.databinding.ActivityGuideHomeBinding;

public class GuideHomeActivity extends AppCompatActivity {
    private ActivityGuideHomeBinding b;
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        b = ActivityGuideHomeBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar()!=null) getSupportActionBar().setTitle("Guía - Inicio");
    }
}
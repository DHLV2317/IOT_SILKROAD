package com.example.silkroad_iot.ui.client;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silkroad_iot.data.Company;
import com.example.silkroad_iot.databinding.ActivityToursBinding;

public class ToursActivity extends AppCompatActivity {
    ActivityToursBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityToursBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // Obtener la empresa y tours desde el Intent
        Company company = (Company) getIntent().getSerializableExtra("company");

        if (company != null) {
            setTitle("Tours de " + company.getN());
            b.rvTours.setLayoutManager(new LinearLayoutManager(this));
            b.rvTours.setAdapter(new TourAdapter(company.tours));
        }
    }
}


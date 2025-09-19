package com.example.silkroad_iot.ui.client;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.data.Tour;
import com.example.silkroad_iot.databinding.ActivityTourDetailBinding;

public class TourDetailActivity extends AppCompatActivity {
    ActivityTourDetailBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityTourDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // Obtener el tour
        Tour tour = (Tour) getIntent().getSerializableExtra("tour");

        if (tour != null) {
            b.tTourName.setText(tour.name);
            b.tTourDescription.setText(tour.description);
            b.tTourPrice.setText("S/. " + tour.price);
            b.btnAdd.setText("Agregar S/. " + tour.price);
            Glide.with(this).load(tour.imageUrl).into(b.imgTour);
        }
    }
}

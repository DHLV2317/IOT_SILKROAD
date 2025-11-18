package com.example.silkroad_iot.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.databinding.ActivityTourDetailBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TourDetailActivity extends AppCompatActivity {

    private ActivityTourDetailBinding b;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityTourDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Detalles del tour");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        b.toolbar.setNavigationOnClickListener(v -> finish());

        TourFB tour = (TourFB) getIntent().getSerializableExtra("tour");
        if (tour == null) {
            Toast.makeText(this, "No se recibió el tour", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("TOUR_DETAIL", "Mostrando detalles de: " + tour.getDisplayName());

        // Nombre
        b.tTourName.setText(tour.getDisplayName());

        // Imagen
        String imgUrl = tour.getDisplayImageUrl();
        Glide.with(this)
                .load(imgUrl)
                .placeholder(com.example.silkroad_iot.R.drawable.ic_launcher_foreground)
                .error(com.example.silkroad_iot.R.drawable.ic_launcher_foreground)
                .into(b.imgTour);

        // Descripción
        String desc = tour.getDescription();
        b.tTourDescription.setText(desc != null && !desc.isEmpty() ? desc : "Sin descripción.");

        // Cupo / personas
        int cupo = tour.getCuposTotalesSafe();
        if (cupo <= 0) cupo = tour.getDisplayPeople();
        b.tTourPeople.setText("Cupo: " + (cupo > 0 ? cupo : 0) + " personas");

        // Fechas
        if (tour.getDateFrom() != null) {
            b.tTourDateFrom.setText("Inicio: " + sdf.format(tour.getDateFrom()));
        } else {
            b.tTourDateFrom.setText("Inicio: -");
        }

        if (tour.getDateTo() != null) {
            b.tTourDateTo.setText("Fin: " + sdf.format(tour.getDateTo()));
        } else {
            b.tTourDateTo.setText("Fin: -");
        }

        // Duración / Paradas (texto básico)
        if (tour.getDuration() != null && !tour.getDuration().isEmpty()) {
            b.tTourDuration.setText("Duración: " + tour.getDuration());
        } else if (tour.getIdParadasList() != null && !tour.getIdParadasList().isEmpty()) {
            b.tTourDuration.setText("Paradas: " + tour.getIdParadasList().size());
        } else {
            b.tTourDuration.setText("Duración: -");
        }

        // Botón → Confirmar tour (reservar)
        double price = tour.getDisplayPrice();
        b.btnAdd.setText("Reservar S/. " + price);

        b.btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(this, ConfirmTourActivity.class);
            i.putExtra("tour", tour);
            startActivity(i);
        });
    }
}
package com.example.silkroad_iot.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.databinding.ActivityTourDetailBinding;

public class TourDetailActivity extends AppCompatActivity {
    ActivityTourDetailBinding b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityTourDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        getSupportActionBar().setTitle("Detalles del Tour");

        TourFB tour = (TourFB) getIntent().getSerializableExtra("tour");

        if (tour == null) {
            Toast.makeText(this, "No se recibió el tour", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("TOUR_DETAIL", "Mostrando detalles de: " + tour.getNombre());

        // Mostrar datos básicos
        b.tTourName.setText(tour.getNombre());
        b.btnAdd.setText("Agregar S/. " + tour.getPrecio());

        Glide.with(this)
                .load(tour.getImagen())
                .into(b.imgTour);

        // Mostrar descripción
        b.tTourDescription.setText(tour.getDescription() != null ? tour.getDescription() : "Sin descripción");

        // Mostrar campos adicionales
        b.tTourLangs.setText("Idiomas: " + (tour.getLangs() != null ? tour.getLangs() : "No especificado"));
        b.tTourPeople.setText("Cupo: " + tour.getCantidad_personas() + " personas");

        if (tour.getDateFrom() != null)
            b.tTourDateFrom.setText("Inicio: " + new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a").format(tour.getDateFrom()));
        else
            b.tTourDateFrom.setText("Inicio: -");

        if (tour.getDateTo() != null)
            b.tTourDateTo.setText("Fin: " + new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a").format(tour.getDateTo()));
        else
            b.tTourDateTo.setText("Fin: -");

        b.tTourDuration.setText("Duración: " + (tour.getDuration() != null ? tour.getDuration() : "-"));

        // Acción del botón
        b.btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(this, ConfirmTourActivity.class);
            i.putExtra("tour", tour);
            startActivity(i);
        });
    }

}

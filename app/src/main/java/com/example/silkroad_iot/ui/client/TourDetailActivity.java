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

        // Gallery: construir lista de imágenes usando imagen principal y posibles imageUrl
        java.util.List<String> images = new java.util.ArrayList<>();
        if (tour.getImagen() != null && !tour.getImagen().isEmpty()) images.add(tour.getImagen());
        if (tour.getImageUrl() != null && !tour.getImageUrl().isEmpty() && !images.contains(tour.getImageUrl())) images.add(tour.getImageUrl());
        // Las paradas no tienen imágenes en el modelo actual
        // if (tour.getParadas() != null) {
        //     for (com.example.silkroad_iot.data.ParadaFB p : tour.getParadas()) {
        //         if (p.getImagen() != null && !p.getImagen().isEmpty()) images.add(p.getImagen());
        //     }
        // }

        if (!images.isEmpty()) {
            GalleryAdapter ga = new GalleryAdapter(images);
            b.vpGallery.setAdapter(ga);
        }
        // El layout ya muestra la galería con vpGallery, no necesitamos imgTour

        // Mostrar descripción
        b.tTourDescription.setText(tour.getDescription() != null ? tour.getDescription() : "");

        // Mostrar campos adicionales
        b.tTourPeople.setText(tour.getCantidad_personas() + " personas");
        
        // Las fechas se muestran en la UI de otra forma o no se muestran en este layout
        // if (tour.getDateFrom() != null)
        //     b.tTourDateFrom.setText("Inicio: " + new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a").format(tour.getDateFrom()));
        // else
        //     b.tTourDateFrom.setText("Inicio: -");
        //
        // if (tour.getDateTo() != null)
        //     b.tTourDateTo.setText("Fin: " + new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a").format(tour.getDateTo()));
        // else
        //     b.tTourDateTo.setText("Fin: -");

        b.tTourDuration.setText((tour.getId_paradas() != null ? tour.getId_paradas().size() : 0) + " paradas");
        
        // Acción del botón
        b.btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(this, ConfirmTourActivity.class);
            i.putExtra("tour", tour);
            startActivity(i);
        });
    }

}

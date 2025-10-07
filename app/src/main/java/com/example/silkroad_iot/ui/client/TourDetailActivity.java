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

        // 🧭 Configurar toolbar
        setSupportActionBar(b.toolbar);
        getSupportActionBar().setTitle("Detalles del Tour");

        // 📦 Obtener tour desde el intent
        TourFB tour = (TourFB) getIntent().getSerializableExtra("tour");

        if (tour == null) {
            Toast.makeText(this, "No se recibió el tour", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("TOUR_DETAIL", "Mostrando detalles de: " + tour.getNombre());

        // 🖼️ Mostrar datos
        b.tTourName.setText(tour.getNombre());
        //b.tTourDescription.setText(tour.getDescripcion() != null ? tour.getDescripcion() : "Sin descripción");
        b.tTourDescription.setText("Sin descripción");
        b.tTourPrice.setText("S/. " + tour.getPrecio());
        b.btnAdd.setText("Agregar S/. " + tour.getPrecio());

        Glide.with(this)
                .load(tour.getImagen())
                .into(b.imgTour);

        // 🎟️ Botón para confirmar compra / reservar tour
        b.btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(this, ConfirmTourActivity.class);
            i.putExtra("tour", tour);
            startActivity(i);
        });
    }
}

package com.example.silkroad_iot.ui.client;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.Stop;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.databinding.ActivityStopsBinding;

import java.util.List;

public class StopsActivity extends AppCompatActivity {
    private RecyclerView rvStops;
    private TextView tvStopsTitle, tvEmptyMessage;
    private ActivityStopsBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar binding
        b = ActivityStopsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // Configurar Toolbar
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lugares a visitar");
        }

        rvStops = b.rvStops;
        tvStopsTitle = b.tvStopsTitle;

        // ✅ Nuevo: TextView opcional para mostrar mensaje vacío
        tvEmptyMessage = b.tvEmptyMessage; // asegúrate de tener esto en tu XML

        // Obtener el Tour
        TourFB tour = (TourFB) getIntent().getSerializableExtra("tour");

        if (tour == null) {
            finish(); // No tour, salir
            return;
        }

        // Mostrar nombre del tour
        tvStopsTitle.setText("Lugares a visitar - " + tour.getNombre());

        // Configurar RecyclerView
        rvStops.setLayoutManager(new LinearLayoutManager(this));

        List<Stop> stops = tour.getStops();

        if (stops == null || stops.isEmpty()) {
            rvStops.setVisibility(View.GONE);
            tvEmptyMessage.setVisibility(View.VISIBLE);
            tvEmptyMessage.setText("Este tour aún no tiene paradas registradas.");
            return;
        }

        // Mostrar lista si hay
        tvEmptyMessage.setVisibility(View.GONE);
        rvStops.setVisibility(View.VISIBLE);
        StopAdapter adapter = new StopAdapter(stops);
        rvStops.setAdapter(adapter);
    }
}

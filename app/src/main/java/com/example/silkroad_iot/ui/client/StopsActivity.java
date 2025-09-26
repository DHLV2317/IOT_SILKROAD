package com.example.silkroad_iot.ui.client;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.Stop;
import com.example.silkroad_iot.data.Tour;

import java.util.ArrayList;
import java.util.List;

public class StopsActivity extends AppCompatActivity {
    private RecyclerView rvStops;
    private TextView tvStopsTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops);

        rvStops = findViewById(R.id.rvStops);
        tvStopsTitle = findViewById(R.id.tvStopsTitle);

        // Obtener el tour desde el intent
        Tour tour = (Tour) getIntent().getSerializableExtra("tour");

        if (tour == null) {
            finish(); // no hay tour, salimos
            return;
        }

        tvStopsTitle.setText("Lugares a visitar - " + tour.name);

        rvStops.setLayoutManager(new LinearLayoutManager(this));

        // Si no hay paradas, creamos unas de ejemplo
        if (tour.stops == null || tour.stops.isEmpty()) {
            List<Stop> exampleStops = new ArrayList<>();
            exampleStops.add(new Stop("Museo de la Cultura", "Av. Principal 123", "30 min", 10.0));
            exampleStops.add(new Stop("Mirador Andino", "Calle Secundaria 456", "45 min", 12.5));
            tour.stops = exampleStops;
        }

        StopAdapter adapter = new StopAdapter(tour.stops);
        rvStops.setAdapter(adapter);
    }
}

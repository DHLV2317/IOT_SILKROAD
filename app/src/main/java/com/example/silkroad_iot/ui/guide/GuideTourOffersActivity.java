package com.example.silkroad_iot.ui.guide;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.TourOffer;
import com.example.silkroad_iot.databinding.ActivityGuideTourOffersBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GuideTourOffersActivity extends AppCompatActivity {

    private ActivityGuideTourOffersBinding binding;
    private TourOfferAdapter tourOfferAdapter;
    private List<TourOffer> tourOfferList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuideTourOffersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ofertas de Tours");

        setupRecyclerView();
        loadTourOffers();
    }

    private void setupRecyclerView() {
        tourOfferList = new ArrayList<>();
        tourOfferAdapter = new TourOfferAdapter(tourOfferList);
        binding.recyclerViewTourOffers.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewTourOffers.setAdapter(tourOfferAdapter);
    }

    private void loadTourOffers() {
        // Datos estáticos de ejemplo como se solicitó
        tourOfferList.add(new TourOffer("City Tour Lima Colonial", "150 soles", "Turismo Lima S.A.C."));
        tourOfferList.add(new TourOffer("Machu Picchu Full Day", "400 soles", "Aventuras Cusco EIRL"));
        tourOfferList.add(new TourOffer("Valle Sagrado de los Incas", "250 soles", "InkaTrek Perú"));
        tourOfferList.add(new TourOffer("Líneas de Nazca (Sobrevuelo)", "500 soles", "Nazca Explorer"));
        tourOfferList.add(new TourOffer("Tour Gastronómico Barranco", "180 soles", "Lima Foodie Tours"));

        tourOfferAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
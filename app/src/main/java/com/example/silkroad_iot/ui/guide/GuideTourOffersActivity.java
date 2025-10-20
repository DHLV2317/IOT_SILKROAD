package com.example.silkroad_iot.ui.guide;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silkroad_iot.data.TourOffer;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityGuideTourOffersBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GuideTourOffersActivity extends AppCompatActivity {

    private ActivityGuideTourOffersBinding binding;
    private TourOfferAdapter tourOfferAdapter;
    private final List<TourOffer> tourOfferList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuideTourOffersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ofertas de Tours");

        db = FirebaseFirestore.getInstance();

        binding.recyclerViewTourOffers.setLayoutManager(new LinearLayoutManager(this));
        tourOfferAdapter = new TourOfferAdapter(tourOfferList);
        binding.recyclerViewTourOffers.setAdapter(tourOfferAdapter);

        loadTourOffers();
    }

    private void loadTourOffers() {
        User u = UserStore.get().getLogged();
        String email = (u!=null ? u.getEmail() : null);
        if (email == null || email.isEmpty()) {
            tourOfferList.clear();
            tourOfferAdapter.notifyDataSetChanged();
            return;
        }

        db.collection("ofertas")
                .whereEqualTo("guideEmail", email)
                .get()
                .addOnSuccessListener(snap -> {
                    tourOfferList.clear();
                    for (QueryDocumentSnapshot d : snap) {
                        TourOffer offer = d.toObject(TourOffer.class);
                        // por si tu modelo no tiene id:
                        // offer.setId(d.getId());
                        tourOfferList.add(offer);
                    }
                    tourOfferAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // dejar la lista como esté; podrías mostrar un Toast si quieres
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
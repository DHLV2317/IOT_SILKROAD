package com.example.silkroad_iot.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.ParadaFB;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.databinding.ActivityAdminTourDetailViewBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdminTourDetailViewActivity extends AppCompatActivity {

    private ActivityAdminTourDetailViewBinding b;
    private FirebaseFirestore db;
    private TourFB tour; // modelo cargado
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityAdminTourDetailViewBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        b.toolbar.setNavigationOnClickListener(v -> finish());
        setTitle("Detalle del tour");

        db = FirebaseFirestore.getInstance();

        String tourId = getIntent().getStringExtra("tourId");
        if (TextUtils.isEmpty(tourId)) {
            // fallback: si venía como objeto
            tour = (TourFB) getIntent().getSerializableExtra("tour");
            if (tour == null || TextUtils.isEmpty(tour.getId())) { finish(); return; }
            bindTour();
            loadParadas(tour.getId());
        } else {
            showStopsLoading(true, null); // por si tarda la carga inicial
            db.collection("tours").document(tourId).get()
                    .addOnSuccessListener(d -> {
                        tour = d.toObject(TourFB.class);
                        if (tour == null) { finish(); return; }
                        if (TextUtils.isEmpty(tour.getId())) tour.setId(d.getId());
                        bindTour();
                        loadParadas(tour.getId());
                    })
                    .addOnFailureListener(e -> {
                        showStopsLoading(false, getString(R.string.error_loading_tour));
                        Snackbar.make(b.getRoot(), "Error cargando tour: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                        finish();
                    });
        }

        // Editar (abre el form de edición/creación)
        b.btnEdit.setOnClickListener(v -> {
            if (tour == null) return;
            Intent it = new Intent(this, AdminTourDetailActivity.class);
            it.putExtra("id", tour.getId());
            startActivity(it);
        });

        // Eliminar
        b.btnDelete.setOnClickListener(v -> {
            if (tour == null || TextUtils.isEmpty(tour.getId())) return;
            b.btnDelete.setEnabled(false);
            db.collection("tours").document(tour.getId())
                    .delete()
                    .addOnSuccessListener(unused -> {
                        Snackbar.make(b.getRoot(), "Tour eliminado", Snackbar.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        b.btnDelete.setEnabled(true);
                        Snackbar.make(b.getRoot(), "Error: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // si vuelves de editar, recarga paradas
        if (tour != null && !TextUtils.isEmpty(tour.getId())) {
            loadParadas(tour.getId());
        }
    }

    private void bindTour() {
        // Nombre y descripción
        b.tName.setText(nz(tour.getDisplayName()));
        b.tDesc.setText(nz(tour.getDescription()));

        // Duración e idiomas
        b.tDuration.setText("Duración: " + (TextUtils.isEmpty(tour.getDuration()) ? "—" : tour.getDuration()));
        b.tLangs.setText("Idiomas: " + (TextUtils.isEmpty(tour.getLangs()) ? "—" : tour.getLangs()));

        // Fechas
        String fechas = "—";
        if (tour.getDateFrom() != null && tour.getDateTo() != null) {
            fechas = sdf.format(tour.getDateFrom()) + " - " + sdf.format(tour.getDateTo());
        }
        b.tDates.setText("Fechas: " + fechas);

        // Precio / Personas
        b.tPrice.setText(String.format(Locale.getDefault(), "S/ %.2f", tour.getDisplayPrice()));
        b.tPeople.setText(String.valueOf(tour.getDisplayPeople()));

        // Imagen
        String img = tour.getDisplayImageUrl();
        if (TextUtils.isEmpty(img)) {
            Glide.with(this).load(R.drawable.ic_menu_24).into(b.img);
        } else {
            Glide.with(this).load(img)
                    .placeholder(R.drawable.ic_menu_24)
                    .error(R.drawable.ic_menu_24)
                    .into(b.img);
        }

        // Guía / Pago
        b.tGuide.setText(TextUtils.isEmpty(tour.getAssignedGuideName()) ? "—" : tour.getAssignedGuideName());
        b.tPayment.setText(tour.getPaymentProposal() != null && tour.getPaymentProposal() > 0
                ? String.format(Locale.getDefault(), "S/ %.2f", tour.getPaymentProposal())
                : "—");

        // Servicios
        b.boxServices.removeAllViews();
        List<TourFB.ServiceFB> services = tour.getServices();
        if (services != null && !services.isEmpty()) {
            for (TourFB.ServiceFB sv : services) {
                String name = nz(sv.getName());
                Boolean included = sv.getIncluded();
                Double price = sv.getPrice();
                String label = name + ((included != null && included) ? " · Incluido"
                        : (" · S/ " + (price == null ? "0" : price)));
                Chip chip = new Chip(this);
                chip.setText(label);
                chip.setChipBackgroundColorResource(R.color.pill_gray);
                b.boxServices.addView(chip);
            }
        }
    }

    private void loadParadas(String tourId) {
        b.boxStops.removeAllViews();
        showStopsLoading(true, null);

        db.collection("tours").document(tourId).collection("paradas")
                .orderBy("orden") // si usas el campo "orden"
                .get()
                .addOnSuccessListener(snap -> {
                    if (snap.isEmpty()) {
                        showStopsLoading(false, getString(R.string.empty_stops));
                        return;
                    }
                    // hay paradas
                    showStopsLoading(false, null);
                    for (QueryDocumentSnapshot d : snap) {
                        ParadaFB p = d.toObject(ParadaFB.class);
                        p.setId(d.getId());

                        String title = !isEmpty(p.getAddress()) ? p.getAddress() : nz(p.getNombre());
                        String minutes = (p.getMinutes() == null) ? "0" : String.valueOf(p.getMinutes());

                        Chip chip = new Chip(this);
                        chip.setText(title + " · " + minutes + " min");
                        chip.setChipBackgroundColorResource(R.color.pill_gray);
                        b.boxStops.addView(chip);
                    }
                })
                .addOnFailureListener(e -> {
                    showStopsLoading(false, getString(R.string.error_loading_stops));
                    Snackbar.make(b.getRoot(), "No se pudieron cargar las paradas", Snackbar.LENGTH_SHORT).show();
                });
    }

    private void showStopsLoading(boolean loading, String emptyMsg) {
        b.progressStops.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (!loading) {
            if (emptyMsg != null) {
                b.tEmptyStops.setText(emptyMsg);
                b.tEmptyStops.setVisibility(View.VISIBLE);
            } else {
                b.tEmptyStops.setVisibility(View.GONE);
            }
        } else {
            b.tEmptyStops.setVisibility(View.GONE);
        }
    }

    private static String nz(String s){ return s==null? "" : s; }
    private static boolean isEmpty(String s){ return s == null || s.trim().isEmpty(); }
}
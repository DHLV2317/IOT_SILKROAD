package com.example.silkroad_iot.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.AdminRepository;
import com.example.silkroad_iot.data.AdminRepository.Tour;
import com.example.silkroad_iot.databinding.ActivityAdminTourDetailViewBinding;
import com.google.android.material.chip.Chip;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminTourDetailViewActivity extends AppCompatActivity {

    private ActivityAdminTourDetailViewBinding b;
    private final AdminRepository repo = AdminRepository.get();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityAdminTourDetailViewBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        b.toolbar.setNavigationOnClickListener(v -> finish());
        setTitle("Detalle del tour");

        int index = getIntent().getIntExtra("index", -1);
        final Tour tour = repo.getTourAt(index);   // ✅ sin getTours().get(index)
        if (tour == null) { finish(); return; }

        // --- Datos básicos ---
        b.tName.setText(nullSafe(tour.name));
        b.tDesc.setText(nullSafe(tour.description));
        b.tDuration.setText("Duración: " + nullSafe(getStringField(tour, "duration")));
        b.tLangs.setText("Idiomas: " + nullSafe(getStringField(tour, "langs")));

        Date df = getDateField(tour, "dateFrom"); // suele ser null; usamos FechaTour más abajo
        Date dt = getDateField(tour, "dateTo");
        String rango = (df != null && dt != null)
                ? (sdf.format(df) + " - " + sdf.format(dt))
                : (tour.FechaTour != null ? sdf.format(tour.FechaTour) : "—");
        b.tDates.setText("Fechas: " + rango);

        b.tPrice.setText("S/ " + tour.price);
        b.tPeople.setText(String.valueOf(tour.people));

        // --- Imagen ---
        if (TextUtils.isEmpty(tour.imageUrl)) {
            Glide.with(this).load(R.drawable.ic_menu_24).into(b.img);
        } else {
            Glide.with(this).load(tour.imageUrl)
                    .placeholder(R.drawable.ic_menu_24)
                    .into(b.img);
        }

        // --- Paradas -> boxStops ---
        b.boxStops.removeAllViews();
        List<?> stops = getListField(tour, "stops");
        if (stops != null) {
            for (Object s : stops) {
                String address = nullSafe(getStringField(s, "address"));
                Number minutes = getNumberField(s, "minutes");
                String m = minutes == null ? "0" : String.valueOf(minutes);
                Chip chip = new Chip(this);
                chip.setText(address + " · " + m + " min");
                chip.setChipBackgroundColorResource(R.color.pill_gray);
                chip.setTextAppearance(
                        com.google.android.material.R.style.TextAppearance_MaterialComponents_Body2);
                b.boxStops.addView(chip);
            }
        }

        // --- Servicios -> boxServices ---
        b.boxServices.removeAllViews();
        List<?> services = getListField(tour, "services");
        if (services != null) {
            for (Object sv : services) {
                String name = nullSafe(getStringField(sv, "name"));
                Boolean included = getBooleanField(sv, "included");
                Number price = getNumberField(sv, "price");
                String label = name + (included != null && included
                        ? " · Incluido"
                        : (" · S/ " + (price == null ? "0" : price)));
                Chip chip = new Chip(this);
                chip.setText(label);
                chip.setChipBackgroundColorResource(R.color.pill_gray);
                chip.setTextAppearance(
                        com.google.android.material.R.style.TextAppearance_MaterialComponents_Body2);
                b.boxServices.addView(chip);
            }
        }

        // --- Guía / Pago ---
        String guideName = getStringField(tour, "assignedGuideName");
        b.tGuide.setText(TextUtils.isEmpty(guideName) ? "—" : guideName);

        Number proposal = getNumberField(tour, "paymentProposal");
        b.tPayment.setText(proposal != null && proposal.doubleValue() > 0 ? "S/ " + proposal : "—");

        // --- Botones ---
        b.btnEdit.setOnClickListener(v -> {
            // Abrir el WIZARD en modo edición
            Intent it = new Intent(this, AdminTourWizardActivity.class);
            it.putExtra("editIndex", index);
            startActivity(it);
        });

        b.btnDelete.setOnClickListener(v -> {
            // Eliminamos desde la lista interna
            AdminRepository.Tour toRemove = repo.getTourAt(index);
            if (toRemove != null) {
                repo.getTours().remove(toRemove);  // aquí sí podemos usar remove
            }
            finish();
        });
    }

    // ---------- Helpers reflexión segura ----------
    private static String nullSafe(String s) { return s == null ? "" : s; }
    private static String getStringField(Object obj, String field) {
        Object v = getFieldValue(obj, field); return v == null ? "" : String.valueOf(v);
    }
    private static Date getDateField(Object obj, String field) {
        Object v = getFieldValue(obj, field); return (v instanceof Date) ? (Date) v : null;
    }
    private static Number getNumberField(Object obj, String field) {
        Object v = getFieldValue(obj, field); return (v instanceof Number) ? (Number) v : null;
    }
    private static Boolean getBooleanField(Object obj, String field) {
        Object v = getFieldValue(obj, field); return (v instanceof Boolean) ? (Boolean) v : null;
    }
    @SuppressWarnings("unchecked")
    private static List<?> getListField(Object obj, String field) {
        Object v = getFieldValue(obj, field); return (v instanceof List) ? (List<?>) v : null;
    }
    private static Object getFieldValue(Object obj, String field) {
        if (obj == null) return null;
        try { Field f = obj.getClass().getDeclaredField(field); f.setAccessible(true); return f.get(obj); }
        catch (Throwable ignore) { return null; }
    }
}
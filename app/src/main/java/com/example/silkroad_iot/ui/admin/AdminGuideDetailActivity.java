package com.example.silkroad_iot.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.GuideFb;
import com.example.silkroad_iot.databinding.ActivityAdminGuideDetailBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminGuideDetailActivity extends AppCompatActivity {

    private ActivityAdminGuideDetailBinding b;
    private FirebaseFirestore db;
    private GuideFb guide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityAdminGuideDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // Toolbar
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Detalles del guía");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        b.toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        String guideId = getIntent().getStringExtra("guideId");
        if (TextUtils.isEmpty(guideId)) {
            Toast.makeText(this, "Falta guideId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarGuia(guideId);

        b.btnBack.setOnClickListener(v -> finish());
    }

    private void cargarGuia(String guideId) {
        db.collection("guias").document(guideId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(this, "Guía no encontrado", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    guide = doc.toObject(GuideFb.class);
                    if (guide != null) guide.setId(doc.getId());
                    pintarUI();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void pintarUI() {
        if (guide == null) return;

        // Foto (header)
        if (!TextUtils.isEmpty(guide.getFotoUrl())) {
            Glide.with(this)
                    .load(guide.getFotoUrl())
                    .placeholder(R.drawable.ic_person_24)
                    .error(R.drawable.ic_person_24)
                    .centerCrop()
                    .into(b.img);
        } else {
            Glide.with(this).load(R.drawable.ic_person_24).into(b.img);
        }

        // Encabezado
        b.tName.setText(textOrDash(guide.getNombre()));
        b.tState.setText(textOrDash(guide.getEstado()));

        // Datos
        b.tLangs.setText(textOrDash(guide.getLangs()));
        b.tEmail.setText(textOrDash(guide.getEmail()));
        b.tPhone.setText(textOrDash(guide.getTelefono()));
        b.tCurrentTour.setText(TextUtils.isEmpty(guide.getTourActual()) ? "Ninguno" : guide.getTourActual());

        // Historial
        b.boxHistory.removeAllViews();
        List<String> hist = guide.getHistorial();
        if (hist != null && !hist.isEmpty()) {
            for (String s : hist) {
                TextView tv = new TextView(this);
                tv.setText("• " + s);
                b.boxHistory.addView(tv);
            }
            b.tNoHistory.setVisibility(android.view.View.GONE);
        } else {
            b.tNoHistory.setVisibility(android.view.View.VISIBLE);
        }
    }

    private String textOrDash(String s) { return TextUtils.isEmpty(s) ? "—" : s; }
}
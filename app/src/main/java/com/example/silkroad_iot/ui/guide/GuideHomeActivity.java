package com.example.silkroad_iot.ui.guide;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityGuideHomeBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GuideHomeActivity extends AppCompatActivity {
    private ActivityGuideHomeBinding b;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        b = ActivityGuideHomeBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar()!=null) getSupportActionBar().setTitle("Guía - Panel de Control");

        db = FirebaseFirestore.getInstance();
        setupClickListeners();
        loadGuideFromFirestore();
    }

    private void loadGuideFromFirestore() {
        User local = UserStore.get().getLogged();
        final String guideEmail = (local != null ? local.getEmail() : null);
        if (guideEmail == null || guideEmail.trim().isEmpty()) {
            b.txtWelcomeGuide.setText("Bienvenido");
            b.txtGuideStatus.setText("Estado: —");
            return;
        }

        db.collection("guias")
                .whereEqualTo("email", guideEmail)
                .limit(1)
                .get()
                .addOnSuccessListener(snap -> {
                    if (snap.isEmpty()) {
                        b.txtWelcomeGuide.setText("Bienvenido");
                        b.txtGuideStatus.setText("Estado: (no encontrado)");
                    } else {
                        DocumentSnapshot d = snap.getDocuments().get(0);
                        String nombre = d.getString("nombre");
                        String estado = d.getString("estado");
                        b.txtWelcomeGuide.setText("¡Bienvenido, " + (nombre==null?"Guía":nombre) + "!");
                        b.txtGuideStatus.setText("Estado: " + (estado==null?"—":estado));
                    }
                })
                .addOnFailureListener(e -> {
                    b.txtWelcomeGuide.setText("Bienvenido");
                    b.txtGuideStatus.setText("Estado: error");
                });
    }

    private void setupClickListeners() {
        b.cardTourOffers.setOnClickListener(v ->
                startActivity(new Intent(this, GuideTourOffersActivity.class)));
        b.cardLocationTracking.setOnClickListener(v ->
                startActivity(new Intent(this, GuideLocationTrackingActivity.class)));
        b.cardQRScanner.setOnClickListener(v ->
                startActivity(new Intent(this, GuideQRScannerActivity.class)));
        b.cardProfile.setOnClickListener(v ->
                startActivity(new Intent(this, GuideProfileActivity.class)));
    }
}
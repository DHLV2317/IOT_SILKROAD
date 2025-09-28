package com.example.silkroad_iot.ui.guide;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityGuideHomeBinding;

public class GuideHomeActivity extends AppCompatActivity {
    private ActivityGuideHomeBinding b;
    
    @Override 
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        b = ActivityGuideHomeBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar()!=null) getSupportActionBar().setTitle("Guía - Panel de Control");
        
        setupUserInfo();
        setupClickListeners();
    }

    private void setupUserInfo() {
        User guide = UserStore.get().getLogged();
        if (guide != null) {
            b.txtWelcomeGuide.setText("¡Bienvenido, " + guide.getName() + "!");
            
            // Mostrar estado de aprobación
            if (guide.isGuideApproved()) {
                b.txtGuideStatus.setText("Estado: Aprobado ✓");
            } else {
                b.txtGuideStatus.setText("Estado: Pendiente de aprobación ⏳");
            }
        }
    }

    private void setupClickListeners() {
        // Ver Ofertas de Tours
        b.cardTourOffers.setOnClickListener(v -> {
            startActivity(new Intent(this, GuideTourOffersActivity.class));
        });

        // Registrar Ubicación
        b.cardLocationTracking.setOnClickListener(v -> {
            startActivity(new Intent(this, GuideLocationTrackingActivity.class));
        });

        // Escanear QR
        b.cardQRScanner.setOnClickListener(v -> {
            startActivity(new Intent(this, GuideQRScannerActivity.class));
        });

        // Mi Perfil
        b.cardProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, GuideProfileActivity.class));
        });
    }
}
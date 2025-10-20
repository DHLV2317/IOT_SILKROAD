package com.example.silkroad_iot.ui.guide;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityGuideLocationTrackingBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GuideLocationTrackingActivity extends AppCompatActivity {

    private ActivityGuideLocationTrackingBinding b;
    private static final int REQ = 1001;

    private final double[] demoLatitudes = {-12.0464, -12.0431, -12.0397, -12.0375};
    private final double[] demoLongitudes = {-77.0428, -77.0282, -77.0351, -77.0624};
    private final String[] demoStops = {
            "Plaza de Armas - Lima Centro",
            "Catedral de Lima",
            "Palacio de Gobierno",
            "Museo Larco"
    };
    private int currentStopIndex = 0;
    private FirebaseFirestore db;
    private String guideDocId;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityGuideLocationTrackingBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Registrar Ubicación");
        }

        db = FirebaseFirestore.getInstance();
        resolveGuideDocIdAndInit();
    }

    private void resolveGuideDocIdAndInit() {
        User u = UserStore.get().getLogged();
        String email = (u != null ? u.getEmail() : null);
        if (email == null || email.isEmpty()) { setupViews(); return; }

        db.collection("guias")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(snap -> {
                    if (!snap.isEmpty()) {
                        guideDocId = snap.getDocuments().get(0).getId();
                        setupViews();
                    } else {
                        db.collection("guias")
                                .whereEqualTo("correo", email)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(snap2 -> {
                                    guideDocId = snap2.isEmpty() ? null : snap2.getDocuments().get(0).getId();
                                    setupViews();
                                })
                                .addOnFailureListener(e -> setupViews());
                    }
                })
                .addOnFailureListener(e -> setupViews());
    }

    private void setupViews() {
        updateCurrentStop();
        b.btnRegisterLocation.setOnClickListener(v -> registerCurrentLocation());
        b.btnNextStop.setOnClickListener(v -> goToNextStop());
        b.btnFinishTour.setOnClickListener(v -> finishTour());
        checkLocationPermission();
    }

    private void updateCurrentStop() {
        if (currentStopIndex < demoStops.length) {
            b.txtCurrentStop.setText("Parada actual: " + demoStops[currentStopIndex]);
            b.txtStopCounter.setText("Parada " + (currentStopIndex + 1) + " de " + demoStops.length);
            b.txtDemoCoordinates.setText(
                    String.format("Coord. Demo: %.4f, %.4f",
                            demoLatitudes[currentStopIndex],
                            demoLongitudes[currentStopIndex]));
            b.btnNextStop.setVisibility(View.VISIBLE);
            b.btnFinishTour.setVisibility(currentStopIndex == demoStops.length - 1 ? View.VISIBLE : View.GONE);
        } else {
            b.txtCurrentStop.setText("¡Tour completado!");
            b.btnNextStop.setVisibility(View.GONE);
            b.btnFinishTour.setVisibility(View.VISIBLE);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ);
        } else {
            b.txtGpsStatus.setText("GPS: Habilitado ✓");
        }
    }

    private void registerCurrentLocation() {
        if (currentStopIndex >= demoStops.length) {
            Toast.makeText(this, "Tour ya completado", Toast.LENGTH_SHORT).show();
            return;
        }
        double lat = demoLatitudes[currentStopIndex];
        double lng = demoLongitudes[currentStopIndex];
        String stop = demoStops[currentStopIndex];
        long ts = System.currentTimeMillis();

        b.txtLocationHistory.append("📍 " + stop + "  ("+lat+", "+lng+")\n");

        if (guideDocId != null) {
            Map<String,Object> m = new HashMap<>();
            m.put("stop", stop);
            m.put("lat", lat);
            m.put("lng", lng);
            m.put("timestamp", ts);
            db.collection("guias").document(guideDocId)
                    .collection("ubicaciones")
                    .add(m);
        }

        Snackbar.make(b.getRoot(), "✓ Ubicación registrada en " + stop, Snackbar.LENGTH_LONG).show();
        b.btnNextStop.setEnabled(true);
    }

    private void goToNextStop() {
        if (currentStopIndex < demoStops.length - 1) {
            currentStopIndex++;
            updateCurrentStop();
            b.btnNextStop.setEnabled(false);
        }
    }

    private void finishTour() {
        Snackbar.make(b.getRoot(), "🎉 ¡Tour finalizado!", Snackbar.LENGTH_LONG).show();
        new android.os.Handler().postDelayed(this::finish, 1500);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                b.txtGpsStatus.setText("GPS: Habilitado ✓");
            } else {
                b.txtGpsStatus.setText("GPS: Permiso denegado ❌");
                Snackbar.make(b.getRoot(), "Permiso de ubicación requerido", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
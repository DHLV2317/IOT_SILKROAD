package com.example.silkroad_iot.ui.guide;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityGuideQrScannerBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GuideQRScannerActivity extends AppCompatActivity {

    private ActivityGuideQrScannerBinding b;
    private static final int CAM_REQ = 1002;
    private FirebaseFirestore db;
    private String guideDocId;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityGuideQrScannerBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Finalizar Servicio - QR");
        }

        db = FirebaseFirestore.getInstance();
        resolveGuideDocId();

        b.btnSimulateQRScan.setOnClickListener(v -> simulateQRScan());
        b.btnManualCheckout.setOnClickListener(v -> manualCheckout());
        checkCameraPermission();
    }

    private void resolveGuideDocId() {
        User u = UserStore.get().getLogged();
        String email = (u!=null? u.getEmail(): null);
        if (email == null || email.isEmpty()) return;

        db.collection("guias").whereEqualTo("email", email).limit(1).get()
                .addOnSuccessListener(snap -> {
                    if (!snap.isEmpty()) guideDocId = snap.getDocuments().get(0).getId();
                });
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAM_REQ);
        } else {
            b.txtCameraStatus.setText("Cámara: Habilitada ✓");
        }
    }

    private void simulateQRScan() {
        String clientId = "CLI-2024-001";
        String tourId = "TOUR-LIMA-001";
        long ts = System.currentTimeMillis();

        b.txtScanResult.setText("✅ QR ESCANEADO\nCliente: "+clientId+"\nTour: "+tourId);
        b.txtInstructions.setText("✓ Servicio finalizado correctamente.");
        Snackbar.make(b.getRoot(), "✓ Check-out exitoso", Snackbar.LENGTH_LONG).show();

        if (guideDocId != null) {
            Map<String,Object> m = new HashMap<>();
            m.put("clientId", clientId);
            m.put("tourId", tourId);
            m.put("timestamp", ts);
            db.collection("guias").document(guideDocId)
                    .collection("checkouts")
                    .add(m);
        }
    }

    private void manualCheckout() {
        long ts = System.currentTimeMillis();
        b.txtScanResult.setText("⚠️ Check-out manual\n" + new java.util.Date(ts));
        b.txtInstructions.setText("Registrado como finalización manual.");
        Snackbar.make(b.getRoot(), "⚠️ Check-out manual", Snackbar.LENGTH_LONG).show();

        if (guideDocId != null) {
            Map<String,Object> m = new HashMap<>();
            m.put("manual", true);
            m.put("timestamp", ts);
            db.collection("guias").document(guideDocId)
                    .collection("checkouts")
                    .add(m);
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAM_REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                b.txtCameraStatus.setText("Cámara: Habilitada ✓");
            } else {
                b.txtCameraStatus.setText("Cámara: Permiso denegado ❌");
                Snackbar.make(b.getRoot(), "Permiso de cámara necesario", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
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
            getSupportActionBar().setTitle("Escanear QR de reserva");
        }

        db = FirebaseFirestore.getInstance();
        resolveGuideDocId();

        // Simulación de escaneo (para pruebas)
        b.btnSimulateQRScan.setOnClickListener(v -> {
            // Ejemplo de QR real:
            // RESERVA|<id_reserva>|<id_tour>|<id_usuario>|PAX:<pax>
            String sampleQr = "RESERVA|abc123|tour567|cliente@mail.com|PAX:2";
            onQrDecoded(sampleQr);
        });

        // Check-out manual (por si hay problemas con el QR/cámara)
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

    /**
     * Aquí llega el texto del QR decodificado.
     * Formato esperado:
     *   RESERVA|<id_reserva>|<id_tour>|<id_usuario>|PAX:<pax>
     */
    private void onQrDecoded(String text) {
        ParsedReservation parsed = parseReservationQr(text);
        if (parsed == null) {
            b.txtScanResult.setText("QR inválido");
            b.txtInstructions.setText("Asegúrate de escanear un código QR de reserva válido.");
            Snackbar.make(b.getRoot(), "QR inválido", Snackbar.LENGTH_LONG).show();
            return;
        }

        b.txtScanResult.setText("QR leído:\nReserva: " + parsed.reservaId +
                "\nTour: " + parsed.tourId +
                "\nCliente: " + parsed.userId +
                "\nPAX: " + parsed.pax);

        updateReservationState(parsed);
    }

    /** Cambia el estado de la reserva: pendiente → check-in → check-out → finalizada */
    private void updateReservationState(ParsedReservation p) {
        db.collection("tours_history")
                .document(p.reservaId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        b.txtInstructions.setText("No se encontró la reserva en el sistema.");
                        Snackbar.make(b.getRoot(), "Reserva no encontrada", Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    String estadoActual = doc.getString("estado");
                    if (estadoActual == null) estadoActual = "pendiente";
                    String nuevoEstado;

                    switch (estadoActual.toLowerCase()) {
                        case "pendiente":
                            nuevoEstado = "check-in";
                            break;
                        case "check-in":
                            nuevoEstado = "check-out";
                            break;
                        case "check-out":
                            nuevoEstado = "finalizada";
                            break;
                        default:
                            nuevoEstado = estadoActual; // no cambiamos si ya es finalizada/cancelada
                            break;
                    }

                    db.collection("tours_history")
                            .document(p.reservaId)
                            .update("estado", nuevoEstado)
                            .addOnSuccessListener(aVoid -> {
                                String msg = "Estado actualizado: " + nuevoEstado;
                                b.txtInstructions.setText(msg);
                                Snackbar.make(b.getRoot(), msg, Snackbar.LENGTH_LONG).show();

                                // log opcional en subcolección del guía
                                if (guideDocId != null) {
                                    Map<String,Object> log = new HashMap<>();
                                    log.put("reservaId", p.reservaId);
                                    log.put("tourId", p.tourId);
                                    log.put("userId", p.userId);
                                    log.put("pax", p.pax);
                                    log.put("nuevoEstado", nuevoEstado);
                                    log.put("timestamp", System.currentTimeMillis());
                                    db.collection("guias").document(guideDocId)
                                            .collection("checkins")
                                            .add(log);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Snackbar.make(b.getRoot(), "Error al actualizar estado", Snackbar.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(b.getRoot(), "Error leyendo la reserva", Snackbar.LENGTH_LONG).show();
                });
    }

    /** Check-out manual sin QR (solo registro local */
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
                    .collection("checkins")
                    .add(m);
        }
    }

    // Parser de texto del QR
    private ParsedReservation parseReservationQr(String text) {
        if (text == null) return null;
        String[] parts = text.split("\\|");
        if (parts.length < 5) return null;
        if (!"RESERVA".equalsIgnoreCase(parts[0])) return null;

        String reservaId = parts[1];
        String tourId    = parts[2];
        String userId    = parts[3];
        int pax          = 1;
        try {
            String paxPart = parts[4]; // PAX:<n>
            if (paxPart.startsWith("PAX:")) {
                pax = Integer.parseInt(paxPart.substring(4));
            }
        } catch (Exception ignore){}

        if (reservaId.isEmpty() || tourId.isEmpty() || userId.isEmpty()) return null;

        return new ParsedReservation(reservaId, tourId, userId, pax);
    }

    private static class ParsedReservation {
        final String reservaId;
        final String tourId;
        final String userId;
        final int pax;
        ParsedReservation(String r, String t, String u, int p){
            this.reservaId = r;
            this.tourId = t;
            this.userId = u;
            this.pax = p;
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
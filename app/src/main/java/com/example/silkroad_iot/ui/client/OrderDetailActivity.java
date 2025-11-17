package com.example.silkroad_iot.ui.client;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.ParadaFB;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.data.TourHistorialFB;
import com.example.silkroad_iot.databinding.ActivityOrderDetailBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private ActivityOrderDetailBinding b;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Detalle de Orden");
        }

        String historialId = getIntent().getStringExtra("historialId");
        final String finalHistorialId = historialId;

        TourFB tour = (TourFB) getIntent().getSerializableExtra("tourFB");
        TourHistorialFB historial = (TourHistorialFB) getIntent().getSerializableExtra("historialFB");

        if (tour == null || historial == null) {
            finish();
            return;
        }

        // ====== DATOS BÁSICOS DEL TOUR / RESERVA ======
        b.tvCompany.setText(tour.getDisplayName());
        b.tvTourName.setText(tour.getDisplayName());
        b.tvTourPrice.setText(String.format(Locale.getDefault(), "S/. %.2f", tour.getDisplayPrice()));

        int pax = historial.getPax() > 0 ? historial.getPax() : 1;
        b.tvQuantity.setText("Cantidad de usuarios: " + pax);
        b.tvServices.setText("Servicios adicionales :     S./0.00");
        b.tvTotalPrice.setText(String.format(Locale.getDefault(), "S/. %.2f", tour.getDisplayPrice() * pax));
        b.tvDepartment.setText("Departamento por definir");
        b.tvDuration.setText("Tiempo: " + (tour.getDuration() == null ? "Por definir" : tour.getDuration()));

        if (historial.getFechaRealizado() != null) {
            b.tvTourDate.setText("Fecha: " + dateFormat.format(historial.getFechaRealizado()));
            b.tvHour.setText(hourFormat.format(historial.getFechaRealizado()));
        } else if (historial.getFechaReserva() != null) {
            b.tvTourDate.setText("Fecha: " + dateFormat.format(historial.getFechaReserva()));
            b.tvHour.setText("Por definir");
        } else {
            b.tvTourDate.setText("Fecha: -");
            b.tvHour.setText("Por definir");
        }

        String estado = (historial.getEstado() == null ? "desconocido" : historial.getEstado());
        b.tvStatus.setText("Estado: " + estado);

        // ========= GENERAR / MOSTRAR QR =========
        String qrData = historial.getQrData();
        if ((qrData == null || qrData.isEmpty()) && finalHistorialId != null && !finalHistorialId.isEmpty()) {
            qrData = "RESERVA|" +
                    finalHistorialId + "|" +
                    historial.getIdTour() + "|" +
                    historial.getIdUsuario() + "|PAX:" + pax;

            historial.setQrData(qrData);
            historial.setPax(pax);

            FirebaseFirestore.getInstance()
                    .collection("tours_history")
                    .document(finalHistorialId)
                    .update("qrData", qrData, "pax", pax);
        }

        if (qrData != null && !qrData.isEmpty()) {
            b.imgQrCode.setImageBitmap(makeQr(qrData));
        } else {
            b.imgQrCode.setImageResource(R.drawable.qr_code_24);
        }

        // ========= SECCIÓN DE CALIFICACIÓN =========
        setupRatingSection(historial, finalHistorialId);

        // ========= VER PARADAS =========
        b.btnPlaces.setOnClickListener(v -> {
            if (tour.getId() == null || tour.getId().isEmpty()) {
                Toast.makeText(this, "Tour sin ID", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseFirestore.getInstance()
                    .collection("tours")
                    .document(tour.getId())
                    .collection("paradas")
                    .orderBy("orden")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        List<ParadaFB> paradas = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshot) {
                            ParadaFB p = doc.toObject(ParadaFB.class);
                            p.setId(doc.getId());
                            paradas.add(p);
                        }
                        tour.setParadas(paradas);
                        Intent it = new Intent(this, StopsActivity.class);
                        it.putExtra("tour", tour);
                        startActivity(it);
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(this, "No se pudieron cargar las paradas", Toast.LENGTH_SHORT).show();
                    });
        });

        // ========= CANCELAR RESERVA =========
        b.btnCancelar.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmar cancelación")
                    .setMessage("¿Estás seguro de que quieres cancelar esta reserva?")
                    .setPositiveButton("Sí, cancelar", (dialog, which) -> {
                        b.tvStatus.setText("Estado: CANCELADO");
                        if (finalHistorialId == null || finalHistorialId.isEmpty()) {
                            finish();
                            return;
                        }
                        FirebaseFirestore.getInstance()
                                .collection("tours_history")
                                .document(finalHistorialId)
                                .update("estado", "cancelado")
                                .addOnSuccessListener(aVoid -> {
                                    Intent it = new Intent(this, ClientHomeActivity.class);
                                    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(it);
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        b.tvStatus.setText("Estado: " + (historial.getEstado() == null ? "desconocido" : historial.getEstado())));
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    /**
     * Sección de calificación:
     * - Solo tours finalizados (o check-out)
     * - Si ya tiene rating en Firestore, no se vuelve a mostrar el formulario
     */
    private void setupRatingSection(TourHistorialFB historial, String historialId) {
        // Estado normalizado
        String estado = historial.getEstado() != null
                ? historial.getEstado().toLowerCase(Locale.ROOT)
                : "";

        boolean esFinalizado =
                estado.contains("finalizada") ||
                        estado.contains("finalizado") ||
                        estado.contains("check-out") ||
                        estado.contains("checkout");

        // Ya calificado si rating != null
        boolean yaCalificado = historial.getRating() != null;

        if (!esFinalizado || yaCalificado) {
            b.layoutRating.setVisibility(View.GONE);
            return;
        }

        // Mostrar UI de calificación
        b.layoutRating.setVisibility(View.VISIBLE);
        b.rbRating.setRating(0f);

        b.btnCalificar.setOnClickListener(v -> {
            if (historialId == null || historialId.isEmpty()) {
                Toast.makeText(this, "No se puede calificar: falta ID de reserva.", Toast.LENGTH_SHORT).show();
                return;
            }

            float ratingValue = b.rbRating.getRating();
            if (ratingValue <= 0f) {
                Toast.makeText(this, "Por favor, selecciona una calificación.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore.getInstance()
                    .collection("tours_history")
                    .document(historialId)
                    .update("rating", ratingValue)
                    .addOnSuccessListener(aVoid -> {
                        historial.setRating(ratingValue);
                        Toast.makeText(this, "¡Gracias por tu calificación!", Toast.LENGTH_SHORT).show();
                        // Ocultamos sección para evitar nueva calificación
                        b.layoutRating.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al guardar la calificación: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private Bitmap makeQr(String text) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            int size = 512;
            BitMatrix bit = writer.encode(text, BarcodeFormat.QR_CODE, size, size);
            Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bmp.setPixel(x, y, bit.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bmp;
        } catch (WriterException e) {
            return null;
        }
    }
}
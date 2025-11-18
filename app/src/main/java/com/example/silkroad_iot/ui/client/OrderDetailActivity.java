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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

        // Normalizar estado para lógicas de UI
        String estadoLower = estado.toLowerCase(Locale.ROOT);
        boolean esFinalizado =
                estadoLower.contains("finalizada") ||
                        estadoLower.contains("finalizado") ||
                        estadoLower.contains("check-out") ||
                        estadoLower.contains("checkout");

        // 👉 Si el tour está finalizado, ocultamos el botón de cancelar
        if (esFinalizado) {
            b.btnCancelar.setVisibility(View.GONE);
        } else {
            b.btnCancelar.setVisibility(View.VISIBLE);
        }

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

        // ========= SECCIÓN DE CALIFICACIÓN / RESULTADO =========
        setupRatingSection(historial, finalHistorialId, esFinalizado);

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

        // ========= CANCELAR RESERVA (con devolución de cupos) =========
        b.btnCancelar.setOnClickListener(v -> {
            if (finalHistorialId == null || finalHistorialId.isEmpty() || tour.getId() == null) {
                Toast.makeText(this, "No se puede cancelar: falta información de la reserva.", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Confirmar cancelación")
                    .setMessage("¿Estás seguro de que quieres cancelar esta reserva?")
                    .setPositiveButton("Sí, cancelar", (dialog, which) -> {

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference histRef = db.collection("tours_history").document(finalHistorialId);
                        DocumentReference tourRef = db.collection("tours").document(tour.getId());

                        db.runTransaction(transaction -> {
                            // Leer historial
                            DocumentSnapshot histSnap = transaction.get(histRef);
                            String estadoActual = histSnap.getString("estado");
                            if (estadoActual != null && estadoActual.equalsIgnoreCase("cancelado")) {
                                return null; // ya estaba cancelado
                            }

                            int paxReserva = 1;
                            if (histSnap.getLong("pax") != null) {
                                paxReserva = histSnap.getLong("pax").intValue();
                            } else if (historial.getPax() > 0) {
                                paxReserva = historial.getPax();
                            }

                            // Leer tour para cupos
                            DocumentSnapshot tourSnap = transaction.get(tourRef);
                            Integer cuposDisp = null;

                            if (tourSnap.getLong("cupos_disponibles") != null) {
                                cuposDisp = tourSnap.getLong("cupos_disponibles").intValue();
                            } else if (tourSnap.getLong("cuposDisponibles") != null) {
                                cuposDisp = tourSnap.getLong("cuposDisponibles").intValue();
                            } else if (tourSnap.getLong("capacidadTotal") != null) {
                                cuposDisp = tourSnap.getLong("capacidadTotal").intValue();
                            }

                            if (cuposDisp == null) {
                                cuposDisp = tour.getCuposDisponiblesSafe();
                            }

                            int nuevosCupos = cuposDisp + paxReserva;

                            // Actualizar estado y cupos
                            transaction.update(histRef, "estado", "cancelado");
                            transaction.update(tourRef, "cupos_disponibles", nuevosCupos);

                            return null;
                        }).addOnSuccessListener(aVoid -> {
                            b.tvStatus.setText("Estado: cancelado");
                            Toast.makeText(this, "Reserva cancelada y cupos liberados", Toast.LENGTH_LONG).show();

                            Intent it = new Intent(this, ClientHomeActivity.class);
                            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(it);
                            finish();
                        }).addOnFailureListener(e ->
                                Toast.makeText(this, "Error al cancelar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    /**
     * Sección de calificación:
     *  - Solo tours finalizados
     *  - Si ya tiene rating/comentario en Firestore, NO se muestra el formulario
     *    y se muestra el resultado (rating + comentario) en modo solo lectura.
     */
    private void setupRatingSection(TourHistorialFB historial,
                                    String historialId,
                                    boolean esFinalizado) {

        Float rating = historial.getRating();
        String comentario = historial.getComentario();

        // Si el tour NO está finalizado → no mostramos nada de rating
        if (!esFinalizado) {
            b.layoutRating.setVisibility(View.GONE);
            b.layoutRatingResult.setVisibility(View.GONE);
            return;
        }

        // Si YA está calificado → mostramos solo el resultado
        if (rating != null) {
            b.layoutRating.setVisibility(View.GONE);
            b.layoutRatingResult.setVisibility(View.VISIBLE);

            b.tvRatingResult.setText(String.format(Locale.getDefault(),
                    "Tu calificación: %.1f / 5", rating));

            if (comentario != null && !comentario.trim().isEmpty()) {
                b.tvCommentResult.setText("Tu comentario: " + comentario);
            } else {
                b.tvCommentResult.setText("Sin comentario.");
            }
            return;
        }

        // Si está finalizado y aún NO tiene rating → mostramos solo el formulario
        b.layoutRating.setVisibility(View.VISIBLE);
        b.layoutRatingResult.setVisibility(View.GONE);
        b.rbRating.setRating(0f);
        b.etComment.setText("");

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

            String commentValue = b.etComment.getText().toString().trim();

            FirebaseFirestore.getInstance()
                    .collection("tours_history")
                    .document(historialId)
                    .update("rating", ratingValue, "comentario", commentValue)
                    .addOnSuccessListener(aVoid -> {
                        historial.setRating(ratingValue);
                        historial.setComentario(commentValue);

                        Toast.makeText(this, "¡Gracias por tu calificación!", Toast.LENGTH_SHORT).show();

                        // Ocultamos el formulario y mostramos el resultado en la vista
                        b.layoutRating.setVisibility(View.GONE);
                        b.layoutRatingResult.setVisibility(View.VISIBLE);

                        b.tvRatingResult.setText(String.format(Locale.getDefault(),
                                "Tu calificación: %.1f / 5", ratingValue));

                        if (!commentValue.isEmpty()) {
                            b.tvCommentResult.setText("Tu comentario: " + commentValue);
                        } else {
                            b.tvCommentResult.setText("Sin comentario.");
                        }
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
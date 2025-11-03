package com.example.silkroad_iot.ui.client;

import android.content.Intent;
import android.os.Bundle;
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
        if (getSupportActionBar()!=null) getSupportActionBar().setTitle("Detalle de Orden");

        String historialId = getIntent().getStringExtra("historialId");
        TourFB tour = (TourFB) getIntent().getSerializableExtra("tourFB");
        TourHistorialFB historial = (TourHistorialFB) getIntent().getSerializableExtra("historialFB");

        if (tour == null || historial == null) { finish(); return; }

        b.tvCompany.setText(tour.getDisplayName());
        b.tvTourName.setText(tour.getDisplayName());
        b.tvTourPrice.setText(String.format(Locale.getDefault(), "S/. %.2f", tour.getDisplayPrice()));
        b.tvQuantity.setText("Cantidad de usuarios: 1");
        b.tvServices.setText("Servicios adicionales :     S./0.00");
        b.tvTotalPrice.setText(String.format(Locale.getDefault(), "S/. %.2f", tour.getDisplayPrice()));
        b.tvDepartment.setText("Departamento por definir");
        b.tvDuration.setText("Tiempo: " + (tour.getDuration() == null ? "Por definir" : tour.getDuration()));

        if (historial.getFechaRealizado() != null) {
            b.tvTourDate.setText("Fecha: " + dateFormat.format(historial.getFechaRealizado()));
            b.tvHour.setText(hourFormat.format(historial.getFechaRealizado()));
        } else {
            b.tvTourDate.setText("Fecha: -");
            b.tvHour.setText("Por definir");
        }

        b.tvStatus.setText("Estado: " + (historial.getEstado() == null ? "desconocido" : historial.getEstado()));
        b.imgQrCode.setImageResource(R.drawable.qr_code_24);

        // Ver Paradas
        b.btnPlaces.setOnClickListener(v -> {
            if (tour.getId() == null || tour.getId().isEmpty()) {
                Toast.makeText(this, "Tour sin ID", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseFirestore.getInstance()
                    .collection("tours").document(tour.getId())
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
                        Intent it = new Intent(this, StopsActivity.class); // puedes renombrar luego a ParadasActivity
                        it.putExtra("tour", tour);
                        startActivity(it);
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(this, "No se pudieron cargar las paradas", Toast.LENGTH_SHORT).show();
                    });
        });

        // Cancelar reserva
        b.btnCancelar.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmar cancelación")
                    .setMessage("¿Estás seguro de que quieres cancelar esta reserva?")
                    .setPositiveButton("Sí, cancelar", (dialog, which) -> {
                        b.tvStatus.setText("Estado: CANCELADO");
                        if (historialId == null || historialId.isEmpty()) { finish(); return; }
                        FirebaseFirestore.getInstance()
                                .collection("tours_history").document(historialId)
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
}
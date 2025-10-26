package com.example.silkroad_iot.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.data.TourHistorialFB;
import com.example.silkroad_iot.data.TourOrder;
import com.example.silkroad_iot.databinding.ActivityOrderDetailBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView tvCompany, tvTourName, tvTourPrice, tvQuantity, tvServices, tvTotalPrice,
            tvDepartment, tvTourDate, tvDuration, tvStatus, tvHour;
    private ImageView imgQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String historialId = getIntent().getStringExtra("historialId");

        super.onCreate(savedInstanceState);
        ActivityOrderDetailBinding b = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);
        getSupportActionBar().setTitle("Detalle de Orden");

        bindViews();

        // 👇 Obtener objetos desde el Intent
        TourFB tour = (TourFB) getIntent().getSerializableExtra("tourFB");
        TourHistorialFB historial = (TourHistorialFB) getIntent().getSerializableExtra("historialFB");

        if (tour == null || historial == null) {
            finish();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        // Mostrar datos
        tvCompany.setText(tour.getNombre());
        tvTourName.setText(tour.getNombre());
        tvTourPrice.setText(String.format("S/. %.2f", tour.getPrecio()));
        tvQuantity.setText("Cantidad de usuarios: 1");
        tvServices.setText("Servicios adicionales :     S./0.00");
        tvTotalPrice.setText(String.format("S/. %.2f", tour.getPrecio()));
        tvDepartment.setText("Departamento por definir");
        tvDuration.setText("Tiempo: Por definir");

        // Fecha del tour
        if (historial.getFechaRealizado() != null) {
            tvTourDate.setText("Fecha: " + dateFormat.format(historial.getFechaRealizado()));
            tvHour.setText(hourFormat.format(historial.getFechaRealizado()));
        } else {
            tvTourDate.setText("Fecha: -");
            tvHour.setText("Por definir");
        }

        // Estado
        tvStatus.setText("Estado: " + historial.getEstado());

        // QR
        imgQrCode.setImageResource(R.drawable.qr_code_24);

        // Botones
        findViewById(R.id.btnPlaces).setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailActivity.this, StopsActivity.class);
            intent.putExtra("tour", tour);
            startActivity(intent);
        });

        findViewById(R.id.btnCancelar).setOnClickListener(v -> {
            new AlertDialog.Builder(OrderDetailActivity.this)
                    .setTitle("Confirmar cancelación")
                    .setMessage("¿Estás seguro de que quieres cancelar esta reserva?")
                    .setPositiveButton("Sí, cancelar", (dialog, which) -> {
                        // ✅ Acción cuando el usuario confirma
                        tvStatus.setText("Estado: CANCELADO");

                        if (historialId == null || historialId.isEmpty()) {
                            finish();
                            return;
                        }

                        FirebaseFirestore.getInstance()
                                .collection("tours_history")
                                .document(historialId)
                                .update("estado", "cancelado")
                                .addOnSuccessListener(aVoid -> {
                                    // 🔄 Ir a ClienteHomeActivity
                                    Intent intent = new Intent(OrderDetailActivity.this, ClientHomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    tvStatus.setText("Estado: " + (historial.getEstado() != null ? historial.getEstado() : "desconocido"));
                                });
                    })
                    .setNegativeButton("No", null) // ❌ No hacer nada si cancela
                    .show();
        });


    }


    private void bindViews() {
        tvCompany = findViewById(R.id.tvCompany);
        tvTourName = findViewById(R.id.tvTourName);
        tvTourPrice = findViewById(R.id.tvTourPrice);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvServices = findViewById(R.id.tvServices);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvTourDate = findViewById(R.id.tvTourDate);
        tvDuration = findViewById(R.id.tvDuration);
        tvStatus = findViewById(R.id.tvStatus);
        tvHour = findViewById(R.id.tvHour);
        imgQrCode = findViewById(R.id.imgQrCode);
    }
}

package com.example.silkroad_iot.ui.client;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.TourOrder;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView tvCompany, tvTourName, tvTourPrice, tvQuantity, tvServices, tvTotalPrice,
            tvDepartment, tvTourDate, tvDuration, tvStatus, tvHour;
    private ImageView imgQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Obtener el TourOrder enviado
        TourOrder order = (TourOrder) getIntent().getSerializableExtra("order");
        if (order == null) {
            finish(); // Si no hay datos, cerrar
            return;
        }

        bindViews();

        // Formato para fechas
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        // Mostrar datos
        tvCompany.setText(order.tour.name);
        tvTourName.setText(order.tour.name);
        tvTourPrice.setText(String.format("S/. %.2f", order.tour.price));
        tvQuantity.setText(String.valueOf("Cantidad de usuarios: " + order.quantity));
        tvServices.setText("Servicios adicionales :     S./0.00"); // futuro: servicios extras
        tvTotalPrice.setText(String.format("S/. %.2f", order.quantity * order.tour.price));
        tvDepartment.setText("Departamento por definir");
        tvTourDate.setText("Fecha: " + dateFormat.format(order.date));
        tvDuration.setText("Tiempo: Por definir"); // futuro: duración del tour
        tvStatus.setText("Estado: " + (order.status != null ? order.status.name() : "Sin estado"));

        // Mostrar hora si existe, si no: “Por definir”
        if (order.date != null) {
            tvHour.setText(hourFormat.format(order.date));
        } else {
            tvHour.setText("Por definir");
        }

        // QR por ahora es imagen fija
        imgQrCode.setImageResource(R.drawable.ic_person_24);
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

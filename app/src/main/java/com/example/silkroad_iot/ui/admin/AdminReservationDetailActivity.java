package com.example.silkroad_iot.ui.admin;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.ReservaWithTour;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.data.TourHistorialFB;
import com.example.silkroad_iot.databinding.ActivityAdminReservationDetailBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminReservationDetailActivity extends AppCompatActivity {

    private ActivityAdminReservationDetailBinding b;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        b = ActivityAdminReservationDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detalle de reserva");
        }
        b.toolbar.setNavigationOnClickListener(v -> finish());

        ReservaWithTour item = (ReservaWithTour) getIntent().getSerializableExtra("reserva");
        if (item == null) { finish(); return; }

        TourFB tour = item.getTour();
        TourHistorialFB r = item.getReserva();

        String tourName    = tour != null ? tour.getDisplayName() : "(Sin tour)";
        double total       = tour != null ? tour.getDisplayPrice() : 0.0;
        int pax            = r.getPax() > 0 ? r.getPax() : (tour != null ? tour.getDisplayPeople() : 1);
        if (pax <= 0) pax = 1;

        String clientId    = r.getIdUsuario() == null ? "—" : r.getIdUsuario();
        String status      = r.getEstado();
        if (TextUtils.isEmpty(status)) status = "pendiente";

        Date date = r.getFechaReserva() != null ? r.getFechaReserva() : r.getFechaRealizado();

        b.tTourName.setText(tourName);
        b.tDate.setText(date == null ? "—" : sdf.format(date));
        b.tAmount.setText("S/ " + (total * pax));
        b.tStatus.setText(status);

        b.tUser.setText(clientId);
        b.tEmail.setText(clientId);
        b.tPhone.setText("—");
        b.tDni.setText("—");

        int bg = R.color.pill_gray;
        String st = status.toLowerCase(Locale.getDefault());
        if (st.contains("check-in") || st.contains("check-out") || st.contains("final")) {
            bg = R.color.teal_200;
        } else if (st.contains("cancel") || st.contains("rech")) {
            bg = android.R.color.holo_red_light;
        }
        b.tStatus.setBackgroundResource(bg);

        // QR: usamos qrData si existe
        String qrData = r.getQrData();
        if (qrData == null || qrData.isEmpty()) {
            // Fallback por si alguna reserva antigua no lo tiene
            String reservaId = r.getId() == null ? "-" : r.getId();
            qrData = "RESERVA|" +
                    reservaId + "|" +
                    r.getIdTour() + "|" +
                    r.getIdUsuario() + "|PAX:" + pax;
        }

        b.imgQr.setImageBitmap(makeQr(qrData));
        b.tQrMessage.setText("Muestra este QR en el punto de encuentro para hacer check-in.");

        // Por ahora no usamos rating → oculto
        b.cardRating.setVisibility(View.GONE);
        RatingBar rb = b.tRating;
        if (rb != null) rb.setRating(5f);

        b.btnBack.setOnClickListener(v -> finish());
    }

    private Bitmap makeQr(String text){
        try {
            QRCodeWriter w = new QRCodeWriter();
            int size = 512;
            BitMatrix bit = w.encode(text, BarcodeFormat.QR_CODE, size, size);
            Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            for (int x=0; x<size; x++) {
                for (int y=0; y<size; y++) {
                    bmp.setPixel(x, y, bit.get(x,y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bmp;
        } catch (WriterException e) {
            return null;
        }
    }
}
package com.example.silkroad_iot.ui.admin;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.AdminRepository;
import com.example.silkroad_iot.databinding.ActivityAdminReservationDetailBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminReservationDetailActivity extends AppCompatActivity {

    private ActivityAdminReservationDetailBinding b;
    private final AdminRepository repo = AdminRepository.get();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        b = ActivityAdminReservationDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        b.toolbar.setNavigationOnClickListener(v -> finish());

        int index = getIntent().getIntExtra("index", -1);
        if (index < 0 || index >= repo.getReservations().size()) { finish(); return; }
        Object r = repo.getReservations().get(index);

        String tourName    = str(r, "tourName");
        String clientName  = str(r, "clientName");
        String clientEmail = str(r, "clientEmail");
        String clientPhone = str(r, "clientPhone");
        String clientDni   = str(r, "clientDni");

        Number people      = num(r, "people");
        Number total       = num(r, "total");
        String status      = str(r, "status");

        Date date          = date(r, "date");
        if (date == null) { Number ts = num(r, "date"); if (ts != null) date = new Date(ts.longValue()); }

        // --- Setear UI con IDs del layout nuevo ---
        b.tTourName.setText(TextUtils.isEmpty(tourName) ? "(Sin tour)" : tourName);
        b.tDate.setText(date == null ? "—" : sdf.format(date));
        b.tAmount.setText("S/ " + (total == null ? 0 : total.doubleValue()));
        b.tStatus.setText(TextUtils.isEmpty(status) ? "pendiente" : status);

        b.tUser.setText(clientName.isEmpty()? "—" : clientName);
        b.tEmail.setText(clientEmail.isEmpty()? "—" : clientEmail);
        b.tPhone.setText(clientPhone.isEmpty()? "—" : clientPhone);
        b.tDni.setText(clientDni.isEmpty()? "—" : clientDni);

        // Badge de estado con colorcito simple
        int bg = R.color.pill_gray;
        String st = (status == null ? "" : status).toLowerCase(Locale.getDefault());
        if (st.contains("pend"))          bg = R.color.pill_gray;
        else if (st.contains("check-in")) bg = R.color.teal_200;
        else if (st.contains("check-out"))bg = R.color.teal_200;
        else if (st.contains("final"))    bg = R.color.teal_200;
        else if (st.contains("cancel"))   bg = android.R.color.holo_red_light;
        b.tStatus.setBackgroundResource(bg);

        // QR con mensaje
        String qrText = "RESERVA|" + (clientName.isEmpty()? "-" : clientName) + "|" +
                (tourName.isEmpty()? "(Sin tour)": tourName) + "|" +
                (date==null? "-" : sdf.format(date)) + "|PAX:" + (people==null?1:people.intValue());
        b.imgQr.setImageBitmap(makeQr(qrText));
        b.tQrMessage.setText("Muestra este QR en el punto de encuentro para hacer check-in.");

        // Valoración (visible solo si estado contiene "final")
        boolean isFinalizada = st.contains("final");
        b.cardRating.setVisibility(isFinalizada ? View.VISIBLE : View.GONE);
        if (isFinalizada) {
            Number stars = num(r, "rating"); // 0..5
            String comment = str(r, "comment");
            RatingBar rb = b.tRating;
            if (rb != null) rb.setRating(stars == null ? 5f : stars.floatValue());
            b.tRatingComment.setText(TextUtils.isEmpty(comment) ? "Sin comentarios." : comment);
        }

        b.btnBack.setOnClickListener(v -> finish());
        // Importante: NO hay botones aprobar/rechazar (los quitaste del layout y del flujo)
    }

    private Bitmap makeQr(String text){
        try {
            QRCodeWriter w = new QRCodeWriter();
            int size = 512;
            var bit = w.encode(text, BarcodeFormat.QR_CODE, size, size);
            Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            for (int x=0; x<size; x++)
                for (int y=0; y<size; y++)
                    bmp.setPixel(x, y, bit.get(x,y) ? 0xFF000000 : 0xFFFFFFFF);
            return bmp;
        } catch (WriterException e) { return null; }
    }

    // ------- helpers reflexión -------
    private static Object f(Object o, String n){ if(o==null) return null; try{Field f=o.getClass().getDeclaredField(n); f.setAccessible(true); return f.get(o);} catch(Throwable ignore){return null;} }
    private static String str(Object o, String n){ Object v=f(o,n); return v==null? "": String.valueOf(v); }
    private static Number num(Object o, String n){ Object v=f(o,n); return (v instanceof Number)? (Number)v : null; }
    private static Date date(Object o, String n){ Object v=f(o,n); return (v instanceof Date)? (Date)v : null; }
}
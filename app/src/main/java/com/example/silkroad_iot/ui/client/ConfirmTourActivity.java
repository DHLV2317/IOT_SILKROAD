package com.example.silkroad_iot.ui.client;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.data.TourHistorialFB;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityConfirmTourBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConfirmTourActivity extends AppCompatActivity {

    private TourFB tour;
    private ActivityConfirmTourBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityConfirmTourBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Reservas");
        }

        // Obtener tour desde el intent
        tour = (TourFB) getIntent().getSerializableExtra("tour");
        if (tour == null) {
            finish();
            return;
        }

        // Mostrar nombre del tour
        b.tTourName.setText(tour.getDisplayName());

        // Cantidad de personas usando helper (cae a 0 si no hay)
        int pax = tour.getDisplayPeople();
        if (pax <= 0) pax = 1;
        b.tTourPeople.setText("Cantidad de personas: " + pax);

        // Mostrar fechas del tour (inicio - fin)
        if (tour.getDateFrom() != null && tour.getDateTo() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String inicio = sdf.format(tour.getDateFrom());
            String fin    = sdf.format(tour.getDateTo());
            b.tTourDates.setText("Fechas: " + inicio + " hasta " + fin);
        } else {
            b.tTourDates.setText("Fechas: No definidas");
        }

        // Mostrar precio total por defecto (por ahora: solo precio base)
        updateTotal();

        // Confirmar reserva
        b.btnConfirm.setOnClickListener(v -> {
            User user = UserStore.get().getLogged();
            if (user == null) {
                Toast.makeText(this, "Inicia sesión para reservar", Toast.LENGTH_SHORT).show();
                return;
            }

            // --------- Datos base de la reserva ----------
            Date ahora     = new Date();          // fecha de reserva
            String estado  = "pendiente";        // estado inicial
            int paxReserva = tour.getDisplayPeople();
            if (paxReserva <= 0) paxReserva = 1;

            // Cadena que irá dentro del QR (misma que verá el guía al escanear)
            // Formato: RESERVA|<id_tour>|<id_usuario>|PAX:<pax>
            String qrData = "RESERVA|" +
                    (tour.getId() == null ? "-" : tour.getId()) + "|" +
                    user.getEmail() + "|PAX:" + paxReserva;

            // Crear objeto historial (se guardará en 'tours_history')
            TourHistorialFB historial = new TourHistorialFB(
                    null,                   // id (lo genera Firestore)
                    tour.getId(),           // id_tour
                    user.getEmail(),        // id_usuario
                    ahora,                  // fechaReserva
                    paxReserva,             // pax
                    estado,                 // estado inicial: pendiente
                    qrData                  // datos que se codifican en el QR
            );

            // Fecha de realización (si ya tienes dateFrom la guardamos también)
            if (tour.getDateFrom() != null) {
                historial.setFechaRealizado(tour.getDateFrom());
            }

            // Guardar en tu store / Firestore
            OrderStore.addOrder(historial);

            Toast.makeText(this, "Tour reservado con éxito", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void updateTotal() {
        double total = tour.getDisplayPrice();  // usa helper que combina precio/price
        b.tTotal.setText("Total: S/. " + total);
    }
}
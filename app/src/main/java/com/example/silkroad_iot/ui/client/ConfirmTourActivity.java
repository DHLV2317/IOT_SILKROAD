package com.example.silkroad_iot.ui.client;

import android.os.Bundle;
import android.widget.TextView;
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
        getSupportActionBar().setTitle("Reservas");

        // Obtener tour desde el intent
        tour = (TourFB) getIntent().getSerializableExtra("tour");
        if (tour == null) {
            finish();
            return;
        }

        // Mostrar nombre del tour
        b.tTourName.setText(tour.getNombre());

        // Mostrar cantidad de personas
        b.tTourPeople.setText("Cantidad de personas: " + tour.getCantidad_personas());

        // Mostrar fechas del tour (inicio - fin)
        if (tour.getDateFrom() != null && tour.getDateTo() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String inicio = sdf.format(tour.getDateFrom());
            String fin = sdf.format(tour.getDateTo());
            b.tTourDates.setText("Fechas: " + inicio + " hasta " + fin);
        } else {
            b.tTourDates.setText("Fechas: No definidas");
        }

        // Mostrar precio total por defecto (una persona)
        updateTotal();

        // Confirmar reserva
        b.btnConfirm.setOnClickListener(v -> {
            User user = UserStore.get().getLogged();
            if (user == null) return;

            // Crear objeto historial
            TourHistorialFB historial = new TourHistorialFB(
                    null,                    // ID (null para que lo genere Firestore)
                    tour.getId(),            // ID del tour
                    user.getEmail(),         // ID del usuario (email)
                    tour.getDateFrom(),      // Fecha de inicio del tour
                    new Date(),              // Fecha de reserva (ahora)
                    "solicitado"             // Estado inicial
            );

            OrderStore.addOrder(historial);
            Toast.makeText(this, "Tour reservado con éxito", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void updateTotal() {
        double total = tour.getPrecio();  // precio por persona
        b.tTotal.setText("Total: S/. " + total);
    }
}

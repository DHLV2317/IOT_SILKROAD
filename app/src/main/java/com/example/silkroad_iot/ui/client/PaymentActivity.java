package com.example.silkroad_iot.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silkroad_iot.data.CardFB;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityPaymentBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding b;
    private FirebaseFirestore db;

    private final List<CardFB> cards = new ArrayList<>();
    private CardAdapter cardAdapter;
    private CardFB selectedCard;

    private TourFB tour;
    private int pax;
    private double total;

    private final DecimalFormat moneyFormat = new DecimalFormat("#0.00");
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pago");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        b.toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        tour = (TourFB) getIntent().getSerializableExtra("tour");
        pax  = getIntent().getIntExtra("pax", 1);
        total = getIntent().getDoubleExtra("total", 0.0);

        if (tour == null) {
            Toast.makeText(this, "Tour inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        User u = UserStore.get().getLogged();
        if (u == null) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        userEmail = u.getEmail();

        b.tResumen.setText(
                "Vas a reservar: " + tour.getDisplayName() +
                        "\nPersonas: " + pax
        );
        b.tMonto.setText("Total a pagar: S/. " + moneyFormat.format(total));

        // Recycler de tarjetas
        b.rvTarjetas.setLayoutManager(new LinearLayoutManager(this));
        cardAdapter = new CardAdapter(cards, card -> selectedCard = card);
        b.rvTarjetas.setAdapter(cardAdapter);

        // Cargar tarjetas del usuario
        cargarTarjetas();

        // Ir a agregar tarjeta
        b.btnAddCard.setOnClickListener(v -> {
            Intent i = new Intent(this, AddCardActivity.class);
            startActivity(i);
        });

        // Botón Pagar
        b.btnPagar.setOnClickListener(v -> {
            if (selectedCard == null) {
                Toast.makeText(this, "Selecciona una tarjeta", Toast.LENGTH_SHORT).show();
                return;
            }
            if (total <= 0) {
                Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            realizarPagoConTransaccion(UserStore.get().getLogged(), selectedCard, tour, pax, total);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar tarjetas por si el usuario agregó una nueva
        if (userEmail != null) {
            cargarTarjetas();
        }
    }

    private void cargarTarjetas() {
        db.collection("cards")
                .whereEqualTo("userEmail", userEmail)
                .get()
                .addOnSuccessListener(q -> {
                    cards.clear();
                    for (DocumentSnapshot doc : q.getDocuments()) {
                        CardFB c = doc.toObject(CardFB.class);
                        if (c != null) {
                            c.setId(doc.getId());
                            cards.add(c);
                        }
                    }
                    cardAdapter.notifyDataSetChanged();

                    if (cards.isEmpty()) {
                        Toast.makeText(this,
                                "No tienes tarjetas registradas. Agrega una para continuar.",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error cargando tarjetas", Toast.LENGTH_SHORT).show());
    }

    private void realizarPagoConTransaccion(User user, CardFB card,
                                            TourFB tour, int pax, double total) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference cardRef = db.collection("cards").document(card.getId());
        DocumentReference tourRef = db.collection("tours").document(tour.getId());
        DocumentReference historyRef = db.collection("tours_history").document();

        db.runTransaction(transaction -> {
            // 1) Leer tarjeta
            DocumentSnapshot cardSnap = transaction.get(cardRef);
            CardFB c = cardSnap.toObject(CardFB.class);
            if (c == null) {
                throw new FirebaseFirestoreException("Tarjeta no encontrada",
                        FirebaseFirestoreException.Code.ABORTED);
            }

            double saldoActual = c.getBalance();
            if (saldoActual < total) {
                throw new FirebaseFirestoreException("Saldo insuficiente",
                        FirebaseFirestoreException.Code.ABORTED);
            }

            // 2) Leer tour para cupos (soporta keys legacy)
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

            if (cuposDisp < pax) {
                throw new FirebaseFirestoreException("No hay cupos suficientes",
                        FirebaseFirestoreException.Code.ABORTED);
            }

            // 3) Actualizar saldo y cupos
            double nuevoSaldo = saldoActual - total;
            int nuevosCupos = cuposDisp - pax;

            transaction.update(cardRef, "balance", nuevoSaldo);

            HashMap<String, Object> tourUpdates = new HashMap<>();
            tourUpdates.put("cupos_disponibles", nuevosCupos);
            tourUpdates.put("cupos_totales", tour.getCuposTotalesSafe());
            transaction.update(tourRef, tourUpdates);

            // 4) Crear historial con estado "aceptado"
            Date ahora = new Date();
            String estado = "aceptado";

            String qrData = "RESERVA|" +
                    historyRef.getId() + "|" +
                    tour.getId() + "|" +
                    user.getEmail() + "|PAX:" + pax;

            HashMap<String, Object> histMap = new HashMap<>();
            histMap.put("id_tour", tour.getId());
            histMap.put("id_usuario", user.getEmail());
            histMap.put("fechaReserva", ahora);
            histMap.put("pax", pax);
            histMap.put("estado", estado);
            histMap.put("qrData", qrData);

            if (tour.getDateFrom() != null) {
                histMap.put("fecha_realizado", tour.getDateFrom());
            }

            transaction.set(historyRef, histMap);

            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(this,
                    "Pago realizado y reserva aceptada 🎉",
                    Toast.LENGTH_LONG).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        });
    }
}
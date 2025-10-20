package com.example.silkroad_iot.ui.client;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silkroad_iot.data.TourHistorialFB;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityTourHistoryBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TourHistoryActivity extends AppCompatActivity {
    private ActivityTourHistoryBinding b;
    private ActivityResultLauncher<Intent> detailLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityTourHistoryBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // 🧭 Configurar toolbar
        setSupportActionBar(b.toolbar);
        getSupportActionBar().setTitle("Historial de Tours");

        // 🧑‍💼 Usuario actual
        User u = UserStore.get().getLogged();
        if (u == null) return;

        // 🎯 Inicializar launcher para resultados
        detailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("updatedOrder")) {
                            TourHistorialFB updated = (TourHistorialFB) data.getSerializableExtra("updatedOrder");

                            // Encuentra y actualiza la lista original
                            List<TourHistorialFB> currentOrders =
                                    ((TourHistorialAdapter) b.rvHistory.getAdapter()).getOrders();

                            for (int i = 0; i < currentOrders.size(); i++) {
                                if (currentOrders.get(i).getFechaRealizado().equals(updated.getFechaRealizado())) {
                                    currentOrders.set(i, updated);
                                    b.rvHistory.getAdapter().notifyItemChanged(i);
                                    break;
                                }
                            }
                        }
                    }
                }
        );

        // 🔄 Cargar historial desde Firestore
        cargarHistorialDesdeFirestore(u.getEmail());
    }

    private void cargarHistorialDesdeFirestore(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<TourHistorialFB> historialList = new ArrayList<>();

        db.collection("tours_history")
                .whereEqualTo("id_usuario", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        TourHistorialFB historial = doc.toObject(TourHistorialFB.class);
                        historial.setId(doc.getId());
                        historialList.add(historial);
                    }

                    // ✅ Configurar Adapter
                    TourHistorialAdapter adapter = new TourHistorialAdapter(historialList, order -> {
                        Intent intent = new Intent(TourHistoryActivity.this, OrderDetailActivity.class);
                        intent.putExtra("order", order);
                        detailLauncher.launch(intent);
                    });

                    b.rvHistory.setLayoutManager(new LinearLayoutManager(this));
                    b.rvHistory.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    // Podrías mostrar un Toast si falla
                    e.printStackTrace();
                });
    }
}

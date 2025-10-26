package com.example.silkroad_iot.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;

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

    private final List<TourHistorialFB> historialList = new ArrayList<>();
    private TourHistorialAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityTourHistoryBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // 🧭 Toolbar
        setSupportActionBar(b.toolbar);
        getSupportActionBar().setTitle("Historial de Tours");

        // 🧑 Usuario actual
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
                            for (int i = 0; i < historialList.size(); i++) {
                                if (historialList.get(i).getId().equals(updated.getId())) {
                                    historialList.set(i, updated);
                                    adapter.notifyItemChanged(i);
                                    break;
                                }
                            }
                        }
                    }
                }
        );

        // 🔄 Configurar botón de filtro
        b.btnFiltrar.setOnClickListener(view -> {
            if (historialList.isEmpty()) return;

            PopupMenu popup = new PopupMenu(this, view);
            popup.getMenu().add("Fecha de inicio del tour");
            popup.getMenu().add("Fecha de reserva");

            popup.setOnMenuItemClickListener(item -> {
                String selected = item.getTitle().toString();

                if (selected.equals("Fecha de inicio del tour")) {
                    historialList.sort((a, b) -> a.getFechaRealizado().compareTo(b.getFechaRealizado()));
                } else if (selected.equals("Fecha de reserva")) {
                    historialList.sort((a, b) -> a.getFechaReserva().compareTo(b.getFechaReserva()));
                }

                adapter.notifyDataSetChanged();
                return true;
            });

            popup.show();
        });

        // 🔄 Cargar historial desde Firestore
        cargarHistorialDesdeFirestore(u.getEmail());
    }

    private void cargarHistorialDesdeFirestore(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        historialList.clear(); // ✅ Limpiar la lista global antes de llenarla

        db.collection("tours_history")
                .whereEqualTo("id_usuario", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        TourHistorialFB historial = doc.toObject(TourHistorialFB.class);
                        historial.setId(doc.getId());
                        historialList.add(historial);
                    }

                    // ✅ Configurar adapter solo una vez
                    if (adapter == null) {
                        adapter = new TourHistorialAdapter(historialList, order -> {
                            Intent intent = new Intent(TourHistoryActivity.this, OrderDetailActivity.class);
                            intent.putExtra("historialFB", order);
                            intent.putExtra("historialId", order.getId());
                            detailLauncher.launch(intent);
                        });
                        b.rvHistory.setLayoutManager(new LinearLayoutManager(this));
                        b.rvHistory.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }


                })
                .addOnFailureListener(Throwable::printStackTrace);
    }
}

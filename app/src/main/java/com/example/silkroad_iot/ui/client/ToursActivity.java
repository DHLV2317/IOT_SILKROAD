package com.example.silkroad_iot.ui.client;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.EmpresaFb;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.databinding.ActivityToursBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ToursActivity extends AppCompatActivity {

    private ActivityToursBinding b;
    private FirebaseFirestore db;
    private List<TourFB> tourList;
    private TourAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityToursBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // 🔧 Configuración base
        db = FirebaseFirestore.getInstance();
        tourList = new ArrayList<>();
        adapter = new TourAdapter(tourList);

        // Toolbar
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tours Disponibles");
        }

        // RecyclerView
        b.rvTours.setLayoutManager(new LinearLayoutManager(this));
        b.rvTours.setAdapter(adapter);

        // 📦 Recibir datos de la empresa seleccionada
        EmpresaFb empresa = (EmpresaFb) getIntent().getSerializableExtra("company");
        if (empresa == null) {
            Toast.makeText(this, "No se recibió la empresa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🏢 Mostrar imagen y nombre de empresa
        ImageView imgCompanyLogo = b.imgCompanyLogo;
        TextView tvCompanyName = b.tvCompanyName;

        tvCompanyName.setText(empresa.getNombre());

        Glide.with(this)
                .load(empresa.getImagen())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(imgCompanyLogo);

        Log.d("TOURS_ACTIVITY", "Cargando tours de empresa: " + empresa.getNombre());

        // 🔥 Cargar tours desde Firebase
        cargarToursDesdeFirebase(empresa.getId());
    }

    private void cargarToursDesdeFirebase(String empresaId) {
        db.collection("tours")
                .whereEqualTo("empresaId", empresaId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d("TOURS_FIREBASE", "✅ id de la empresa: " + empresaId);
                    tourList.clear();

                    for (DocumentSnapshot document : querySnapshot) {
                        TourFB tour = new TourFB();

                        // ----- ID -----
                        tour.setId(document.getId());

                        // ----- Nombre / descripción -----
                        tour.setNombre(document.getString("nombre"));
                        tour.setName(document.getString("name")); // por si usas ambos
                        tour.setDescription(document.getString("description"));

                        // ----- Empresa -----
                        tour.setEmpresaId(document.getString("empresaId"));

                        // ----- Imagen (imagen / imageUrl) -----
                        String img1 = document.getString("imagen");
                        String img2 = document.getString("imageUrl");
                        if (img1 != null && !img1.isEmpty()) {
                            tour.setImagen(img1);
                            tour.setImageUrl(img1);
                        } else if (img2 != null && !img2.isEmpty()) {
                            tour.setImagen(img2);
                            tour.setImageUrl(img2);
                        }

                        // ----- Precio seguro (price / precio) -----
                        Double priceField = document.getDouble("price");
                        Double precioField = document.getDouble("precio");

                        double safePrice = 0.0;
                        if (priceField != null) {
                            safePrice = priceField;
                        } else if (precioField != null) {
                            safePrice = precioField;
                        }

                        tour.setPrecio(safePrice); // double
                        tour.setPrice(safePrice);  // Double

                        // ----- Cantidad de personas segura -----
                        Long peopleField = document.getLong("people");
                        Long cantidad = document.getLong("cantidad_personas");
                        int safePeople = 0;
                        if (peopleField != null) {
                            safePeople = peopleField.intValue();
                        } else if (cantidad != null) {
                            safePeople = cantidad.intValue();
                        }
                        tour.setCantidad_personas(safePeople);
                        tour.setPeople(safePeople);

                        // ----- Otros campos opcionales -----
                        tour.setCiudad(document.getString("ciudad"));
                        tour.setLangs(document.getString("langs"));
                        tour.setDuration(document.getString("duration"));

                        // guía asignado (si lo manejas así)
                        tour.setAssignedGuideName(document.getString("assignedGuideName"));
                        tour.setAssignedGuideId(document.getString("assignedGuideId"));

                        // Fechas
                        tour.setDateFrom(document.getDate("dateFrom"));
                        tour.setDateTo(document.getDate("dateTo"));

                        // ✅ Manejo seguro de id_paradas
                        Object idParadasObj = document.get("id_paradas");
                        if (idParadasObj instanceof List) {
                            //noinspection unchecked
                            tour.setId_paradas((List<String>) idParadasObj);
                        } else if (idParadasObj instanceof String) {
                            tour.setId_paradas(
                                    Collections.singletonList((String) idParadasObj)
                            );
                        } else {
                            tour.setId_paradas(new ArrayList<>());
                        }

                        tourList.add(tour);

                        Log.d(
                                "TOURS_FIREBASE",
                                String.format(
                                        Locale.getDefault(),
                                        "✅ Tour cargado: %s / Empresa: %s / Precio: %.2f",
                                        tour.getDisplayName(),
                                        tour.getEmpresaId(),
                                        tour.getDisplayPrice()
                                )
                        );
                    }

                    Log.d("TOURS_FIREBASE", "✅ Total tours encontrados: " + tourList.size());
                    adapter.notifyDataSetChanged();
                    Log.d("TOURS_FIREBASE", "✅ Adapter notificado. tours.size=" + adapter.getItemCount());
                })
                .addOnFailureListener(e -> {
                    Log.e("TOURS_FIREBASE", "❌ Error al cargar tours", e);
                    Toast.makeText(this, "Error al cargar tours", Toast.LENGTH_SHORT).show();
                });
    }
}
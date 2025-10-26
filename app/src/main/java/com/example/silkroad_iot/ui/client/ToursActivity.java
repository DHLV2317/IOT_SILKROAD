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
import java.util.List;

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
        getSupportActionBar().setTitle("Tours Disponibles");

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
                .load(empresa.getImagen()) // Asegúrate que empresa.getLogo() devuelva una URL válida
                .placeholder(R.drawable.ic_launcher_foreground)
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

                    for (DocumentSnapshot doc : querySnapshot) {
                        TourFB tour = doc.toObject(TourFB.class);
                        if (tour != null) {
                            tour.setId(doc.getId());
                            tourList.add(tour);
                            Log.d("TOURS_FIREBASE", "✅ Tour cargado: " + tour.getNombre() + " / Empresa: " + tour.getEmpresaId());
                        }
                    }

                    Log.d("TOURS_FIREBASE", "✅ Total tours encontrados: " + tourList.size());
                    Log.d("TOURS_FIREBASE", "✅ tourList.hash=" + tourList.hashCode());

                    // Aquí ya no pases otra lista, solo notifica el cambio
                    adapter.notifyDataSetChanged();

                    Log.d("TOURS_FIREBASE", "✅ Adapter notificado. tours.size=" + adapter.getItemCount());
                })
                .addOnFailureListener(e -> {
                    Log.e("TOURS_FIREBASE", "❌ Error al cargar tours", e);
                    Toast.makeText(this, "Error al cargar tours", Toast.LENGTH_SHORT).show();
                });
    }


}

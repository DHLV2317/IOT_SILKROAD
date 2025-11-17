package com.example.silkroad_iot.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.Department;
import com.example.silkroad_iot.data.EmpresaFb;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityClientHomeBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientHomeActivity extends AppCompatActivity {
    private ActivityClientHomeBinding b;
    private final UserStore store = UserStore.get();

    private FirebaseFirestore db;
    private List<EmpresaFb> empresasFB = new ArrayList<>();
    private CompanyAdapter companyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityClientHomeBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);

        // Toolbar con el nombre de usuario
        User u = store.getLogged();
        String name = (u != null ? u.getName() : "Usuario");
        b.toolbar.setTitle("Hola " + name);

        // Departamentos
        List<Department> deps = Arrays.asList(
                new Department("Lima"), new Department("Cusco"),
                new Department("Arequipa"), new Department("Piura"),
                new Department("La Libertad")
        );
        b.rvDepartments.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        b.rvDepartments.setAdapter(new DepartmentAdapter(deps, d -> {
            // TODO: Filtrar tours por departamento
        }));

        // 🔥 Configuración Firebase
        db = FirebaseFirestore.getInstance();
        companyAdapter = new CompanyAdapter(empresasFB);

        // Mostrar companies en Staggered Grid (Pinterest style)
        androidx.recyclerview.widget.StaggeredGridLayoutManager sglm = new androidx.recyclerview.widget.StaggeredGridLayoutManager(2, androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL);
        b.rvCompanies.setLayoutManager(sglm);
        b.rvCompanies.setAdapter(companyAdapter);

        // Recommended tours (horizontal carousel)
        List<com.example.silkroad_iot.data.TourFB> recommended = new ArrayList<>();
        com.example.silkroad_iot.ui.client.TourAdapter recommendedAdapter = new com.example.silkroad_iot.ui.client.TourAdapter(recommended);
        b.rvRecommendedTours.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        b.rvRecommendedTours.setAdapter(recommendedAdapter);

        // Cargar recomendaciones simples desde Firestore (colección "tours" con limit 10)
        db.collection("tours").limit(10).get().addOnSuccessListener(qs -> {
            List<com.example.silkroad_iot.data.TourFB> list = new ArrayList<>();
            for (DocumentSnapshot doc : qs) {
                com.example.silkroad_iot.data.TourFB t = doc.toObject(com.example.silkroad_iot.data.TourFB.class);
                if (t != null) { t.setId(doc.getId()); list.add(t); }
            }
            runOnUiThread(() -> {
                recommendedAdapter.updateData(list);
            });
        }).addOnFailureListener(e -> Log.e("RECOMMENDED", "Error loading tours", e));

        // Cargar empresas desde Firestore
        cargarEmpresasDesdeFirebase();

        // Filtro
        b.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                companyAdapter.filterList(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Botón historial
        // Button btnHistory = findViewById(R.id.btnHistory); // No existe en el layout actual
        // btnHistory.setOnClickListener(v -> {
        //     Intent i = new Intent(this, TourHistoryActivity.class);
        //     startActivity(i);
        // });
    }

    private void cargarEmpresasDesdeFirebase() {
        Log.d("EMPRESAS_FIREBASE", "Iniciando carga desde Firestore...");

        db.collection("empresas")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<EmpresaFb> nuevasEmpresas = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot) {
                        EmpresaFb emp = doc.toObject(EmpresaFb.class);
                        emp.setId(doc.getId());  // ✅ guardar el ID del documento
                        if (emp != null) {
                            nuevasEmpresas.add(emp);
                            Log.d("EMPRESAS_FIREBASE", "Empresa cargada: " + emp.getNombre());
                        }
                    }

                    Log.d("EMPRESAS_FIREBASE", "Total empresas cargadas: " + nuevasEmpresas.size());

                    // ✅ Actualizar adapter con los nuevos datos
                    runOnUiThread(() -> {
                        companyAdapter.updateData(nuevasEmpresas);
                        Log.d("EMPRESAS_FIREBASE", "Adapter items después: " + companyAdapter.getItemCount());
                    });
                })
                .addOnFailureListener(e -> Log.e("EMPRESAS_FIREBASE", "Error al cargar empresas", e));
    }
}

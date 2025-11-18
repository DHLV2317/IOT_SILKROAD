package com.example.silkroad_iot.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.Department;
import com.example.silkroad_iot.data.EmpresaFb;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityClientHomeBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ClientHomeActivity extends AppCompatActivity {

    private ActivityClientHomeBinding b;
    private final UserStore store = UserStore.get();

    private FirebaseFirestore db;

    // Drawer
    private ActionBarDrawerToggle drawerToggle;

    // Empresas
    private CompanyAdapter companyAdapter;
    private List<EmpresaFb> empresasFB = new ArrayList<>();

    // Ciudades
    private DepartmentAdapter departmentAdapter;
    private final List<Department> departamentos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityClientHomeBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // Toolbar como ActionBar
        setSupportActionBar(b.toolbar);

        // DrawerToggle (icono hamburguesa)
        drawerToggle = new ActionBarDrawerToggle(
                this,
                b.drawerLayout,
                b.toolbar,
                R.string.nav_open,
                R.string.nav_close
        );
        b.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Usuario actual
        User u = store.getLogged();
        String name = (u != null ? u.getName() : "Usuario");
        String email = (u != null ? u.getEmail() : "");

        b.toolbar.setTitle("Hola " + name);
        b.tvHello.setText("Hola " + name);

        // Header del NavigationView
        View header = b.navViewClient.getHeaderView(0);
        TextView tvHeaderName = header.findViewById(R.id.tvHeaderName);
        TextView tvHeaderEmail = header.findViewById(R.id.tvHeaderEmail);
        ImageView imgAvatarHeader = header.findViewById(R.id.imgAvatarHeader);

        tvHeaderName.setText(name);
        tvHeaderEmail.setText(email);
        // Si tienes URL de foto en User, la puedes cargar con Glide:
        // Glide.with(this).load(u.getPhotoUrl()).into(imgAvatarHeader);

        // Listener del menú lateral
        b.navViewClient.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.m_home) {
                            Log.d("NAV", "Home seleccionado");
                        } else if (id == R.id.m_profile) {
                            startActivity(new Intent(ClientHomeActivity.this, ClientOnboardingActivity.class));
                        } else if (id == R.id.m_history) {
                            startActivity(new Intent(ClientHomeActivity.this, TourHistoryActivity.class));
                        } else if (id == R.id.m_payments) {
                            startActivity(new Intent(ClientHomeActivity.this, PaymentMethodsActivity.class));
                        } else if (id == R.id.m_support_chat) {
                            startActivity(new Intent(ClientHomeActivity.this, SupportChatActivity.class));
                        } else if (id == R.id.m_logout) {
                            FirebaseAuth.getInstance().signOut();
                            finish();
                        }

                        b.drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    }
                }
        );

        // Recycler CIUDADES
        departmentAdapter = new DepartmentAdapter(departamentos, d -> {
            Log.d("CIUDAD_CLICK", "Ciudad seleccionada: " + d.getNombre());
            // Aquí luego podrías filtrar empresas por ciudad
        });
        b.rvDepartments.setLayoutManager(
                new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        );
        b.rvDepartments.setAdapter(departmentAdapter);

        // Firestore
        db = FirebaseFirestore.getInstance();

        // Recycler EMPRESAS
        companyAdapter = new CompanyAdapter(empresasFB);
        b.rvCompanies.setLayoutManager(new LinearLayoutManager(this));
        b.rvCompanies.setAdapter(companyAdapter);

        // Cargar empresas y ciudades
        cargarEmpresasDesdeFirebase();
        cargarCiudadesDesdeToursFirebase();

        // Filtro búsqueda empresas
        b.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                companyAdapter.filterList(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Botón Historial
        b.btnHistory.setOnClickListener(v -> {
            Intent i = new Intent(this, TourHistoryActivity.class);
            startActivity(i);
        });
    }

    @Override
    public void onBackPressed() {
        if (b.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            b.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void cargarEmpresasDesdeFirebase() {
        Log.d("EMPRESAS_FIREBASE", "Cargando empresas...");

        db.collection("empresas")
                .get()
                .addOnSuccessListener(query -> {
                    List<EmpresaFb> nuevas = new ArrayList<>();

                    for (DocumentSnapshot doc : query) {
                        EmpresaFb emp = doc.toObject(EmpresaFb.class);
                        if (emp != null) {
                            emp.setId(doc.getId());
                            nuevas.add(emp);
                            Log.d("EMPRESAS_FIREBASE", "Empresa: " + emp.getNombre());
                        }
                    }

                    empresasFB.clear();
                    empresasFB.addAll(nuevas);
                    companyAdapter.updateData(nuevas);

                    Log.d("EMPRESAS_FIREBASE", "Total empresas cargadas: " + nuevas.size());
                })
                .addOnFailureListener(e -> Log.e("EMPRESAS_FIREBASE", "Error al cargar empresas", e));
    }

    private void cargarCiudadesDesdeToursFirebase() {
        Log.d("CIUDADES_FIREBASE", "Cargando ciudades desde tours...");

        db.collection("tours")
                .get()
                .addOnSuccessListener(query -> {
                    Set<String> ciudadesSet = new LinkedHashSet<>();

                    for (DocumentSnapshot doc : query) {
                        TourFB tour = doc.toObject(TourFB.class);
                        if (tour != null) {
                            String ciudad = tour.getCiudad();
                            if (ciudad != null && !ciudad.trim().isEmpty()) {
                                ciudadesSet.add(ciudad.trim());
                            }
                        }
                    }

                    departamentos.clear();
                    for (String c : ciudadesSet) {
                        departamentos.add(new Department(c));
                    }
                    departmentAdapter.notifyDataSetChanged();

                    Log.d("CIUDADES_FIREBASE", "Ciudades encontradas: " + ciudadesSet.size());
                })
                .addOnFailureListener(e -> Log.e("CIUDADES_FIREBASE", "Error al cargar ciudades", e));
    }
}
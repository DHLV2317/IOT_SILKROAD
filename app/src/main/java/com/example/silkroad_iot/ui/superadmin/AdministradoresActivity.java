package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.silkroad_iot.ui.superadmin.entity.Administrador;
import com.example.silkroad_iot.ui.superadmin.entity.ListaAdministradoresAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.databinding.ActivitySuperadminAdministradoresBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdministradoresActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private FirebaseFirestore db;
    ActivitySuperadminAdministradoresBinding binding;
    private final List<Administrador> administradorList = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ListaAdministradoresAdapter adapter;

    private FirebaseFirestore db;

    private ListaAdministradoresAdapter listaAdministradoresAdapter = new ListaAdministradoresAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminAdministradoresBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
<<<<<<< Updated upstream

        listaAdministradoresAdapter.setListaAdministradores(administradorList);
        listaAdministradoresAdapter.setContext(AdministradoresActivity.this);
        binding.recyclerView.setAdapter(listaAdministradoresAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(AdministradoresActivity.this));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_inicio) {
            Intent intent = new Intent(this, SuperAdminHomeActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_administradores) {
            Intent intent = new Intent(this, AdministradoresActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_solicitudes_guias) {
            Intent intent = new Intent(this, SolicitudesGuiasActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_guias) {
            Intent intent = new Intent(this, GuiasActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_clientes) {
            Intent intent = new Intent(this, ClientesActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_reportes) {
            Intent intent = new Intent(this, ReportesActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_logs) {
            Intent intent = new Intent(this, LogsActivity.class);
            startActivity(intent);
        }
        //else if (itemId == R.id.nav_grupo_iot_item) {}
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        //int itemId = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    /*public void cargarLista() {
        //List<Router> routerList = lista;
        administradorList = Global.listaAdministradores;
        ListaAdministradoresAdapter listaAdministradoresAdapter = new ListaAdministradoresAdapter();
        listaAdministradoresAdapter.setListaAdministradores(administradorList);
        listaAdministradoresAdapter.setContext(AdministradoresActivity.this);
        binding.recyclerView.setAdapter(listaAdministradoresAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(AdministradoresActivity.this));

    }*/
=======

        adapter = new ListaAdministradoresAdapter();
        adapter.setListaAdministradores(administradorList);
        adapter.setContext(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
    }
>>>>>>> Stashed changes

    private void cargarAdministradoresDesdeFirebase() {
        //Log.d("ADMINISTRADORES_FIREBASE", "Iniciando carga desde Firestore...");
        db.collection("administradores")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Administrador> administradorList = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot) {
                        Administrador adm = doc.toObject(Administrador.class);
                        adm.setId(doc.getId());  // ✅ guardar el ID del documento
                        if (adm != null) {
                            administradorList.add(adm);
                            Log.d("EMPRESAS_FIREBASE", "Administrador cargado: " + adm.getNombre());
                        }
                    }


                    // ✅ Actualizar adapter con los nuevos datos
                    runOnUiThread(() -> {
                        listaAdministradoresAdapter.setListaAdministradores(administradorList);
                    });
                })
                .addOnFailureListener(e -> Log.e("EMPRESAS_FIREBASE", "Error al cargar empresas", e));
    }
    @Override
    protected void onStart() {
        super.onStart();
        //cargarLista();
    }

    private void cargarLista() {
        administradorList.clear();
        db.collection("administradores")
                .get()
                .addOnSuccessListener(snap -> {
                    for (QueryDocumentSnapshot d : snap) {
                        Administrador a = d.toObject(Administrador.class);
                        // asegura docId si lo necesitas luego
                        if (a.getCorreo() == null || a.getCorreo().isEmpty()) {
                            a.setCorreo(d.getId());
                        }
                        administradorList.add(a);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    public void crearAdministrador(android.view.View view){
        startActivity(new Intent(this, CrearAdministradorActivity.class));
    }

    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_inicio) {
            startActivity(new Intent(this, SuperAdminHomeActivity.class));
        } else if (id == R.id.nav_administradores) {
            // ya estás aquí
        } else if (id == R.id.nav_solicitudes_guias) {
            startActivity(new Intent(this, SolicitudesGuiasActivity.class));
        } else if (id == R.id.nav_guias) {
            startActivity(new Intent(this, GuiasActivity.class));
        } else if (id == R.id.nav_clientes) {
            startActivity(new Intent(this, ClientesActivity.class));
        } else if (id == R.id.nav_reportes) {
            startActivity(new Intent(this, ReportesActivity.class));
        } else if (id == R.id.nav_logs) {
            startActivity(new Intent(this, LogsActivity.class));
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
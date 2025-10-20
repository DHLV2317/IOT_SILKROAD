package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.databinding.ActivitySuperadminReportesBinding;
import com.example.silkroad_iot.ui.superadmin.entity.ListaAdministradoresAdapter;
<<<<<<< Updated upstream
import com.example.silkroad_iot.ui.superadmin.entity.ListaEmpresasAdapter;
=======
import com.example.silkroad_iot.ui.superadmin.entity.ReporteDTO;
>>>>>>> Stashed changes
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReportesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ActivitySuperadminReportesBinding binding;
    private final List<ReporteDTO> reporteList = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private FirebaseFirestore db;
    private ListaAdministradoresAdapter dummyAdapter; // Si tienes un adapter específico para reportes, úsalo.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminReportesBinding.inflate(getLayoutInflater());
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

        // TODO: reemplaza por tu adapter real de reportes
        dummyAdapter = new ListaAdministradoresAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(dummyAdapter);

        db = FirebaseFirestore.getInstance();
    }

<<<<<<< Updated upstream
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

    public void cargarLista() {
        //List<Router> routerList = lista;
        administradorList = Global.listaAdministradores;
        ListaEmpresasAdapter listaAdministradoresAdapter = new ListaEmpresasAdapter();
        listaAdministradoresAdapter.setListaAdministradores(administradorList);
        listaAdministradoresAdapter.setContext(ReportesActivity.this);
        binding.recyclerView.setAdapter(listaAdministradoresAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(ReportesActivity.this));

    }

    @Override
    protected void onStart() {
=======
    @Override protected void onStart() {
>>>>>>> Stashed changes
        super.onStart();
        cargarLista();
    }

    private void cargarLista() {
        reporteList.clear();
        db.collection("reportes")
                .get()
                .addOnSuccessListener(snap -> {
                    for (QueryDocumentSnapshot d : snap) {
                        ReporteDTO r = d.toObject(ReporteDTO.class);
                        reporteList.add(r);
                    }
                    // TODO: notificar tu adapter real de reportes
                    dummyAdapter.notifyDataSetChanged();
                });
    }

    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_inicio) {
            startActivity(new Intent(this, SuperAdminHomeActivity.class));
        } else if (id == R.id.nav_administradores) {
            startActivity(new Intent(this, AdministradoresActivity.class));
        } else if (id == R.id.nav_solicitudes_guias) {
            startActivity(new Intent(this, SolicitudesGuiasActivity.class));
        } else if (id == R.id.nav_guias) {
            startActivity(new Intent(this, GuiasActivity.class));
        } else if (id == R.id.nav_clientes) {
            startActivity(new Intent(this, ClientesActivity.class));
        } else if (id == R.id.nav_reportes) {
            // aquí
        } else if (id == R.id.nav_logs) {
            startActivity(new Intent(this, LogsActivity.class));
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
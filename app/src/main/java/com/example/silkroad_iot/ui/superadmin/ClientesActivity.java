package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.silkroad_iot.databinding.ActivitySuperadminClientesBinding;
import com.example.silkroad_iot.ui.superadmin.entity.Cliente;
import com.example.silkroad_iot.ui.superadmin.entity.ListaClientesAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silkroad_iot.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClientesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ActivitySuperadminClientesBinding binding;
    private final List<Cliente> clienteList = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ListaClientesAdapter adapter;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminClientesBinding.inflate(getLayoutInflater());
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

        adapter = new ListaClientesAdapter();
        adapter.setListaClientes(clienteList);
        adapter.setContext(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
    }

    @Override protected void onStart() {
        super.onStart();
        cargarLista();
    }

    private void cargarLista() {
        clienteList.clear();
        db.collection("clientes")
                .get()
                .addOnSuccessListener(snap -> {
                    for (QueryDocumentSnapshot d : snap) {
                        Cliente c = d.toObject(Cliente.class);
                        if (c.getCorreo() == null || c.getCorreo().isEmpty()) {
                            c.setCorreo(d.getId());
                        }
                        clienteList.add(c);
                    }
                    adapter.notifyDataSetChanged();
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
            // aquí
        } else if (id == R.id.nav_reportes) {
            startActivity(new Intent(this, ReportesActivity.class));
        } else if (id == R.id.nav_logs) {
            startActivity(new Intent(this, LogsActivity.class));
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
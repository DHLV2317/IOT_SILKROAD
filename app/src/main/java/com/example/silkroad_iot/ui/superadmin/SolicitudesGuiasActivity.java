package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.silkroad_iot.databinding.ActivitySuperadminGuiasBinding;
import com.example.silkroad_iot.databinding.ActivitySuperadminSolicitudesGuiasBinding;
import com.example.silkroad_iot.ui.superadmin.entity.Global;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.ui.superadmin.entity.Guia;
import com.example.silkroad_iot.ui.superadmin.entity.ListaGuiasAdapter;
import com.example.silkroad_iot.ui.superadmin.entity.ListaSolicitudesGuiasAdapter;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class SolicitudesGuiasActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ActivitySuperadminSolicitudesGuiasBinding binding;
    private List<Guia> guiaList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminSolicitudesGuiasBinding.inflate(getLayoutInflater());
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
    public void cargarLista() {
        //List<Router> routerList = lista;
        guiaList = Global.listaGuiasNoAprobados;
        ListaSolicitudesGuiasAdapter listaSolicitudesGuiasAdapter = new ListaSolicitudesGuiasAdapter();
        listaSolicitudesGuiasAdapter.setListaSolicitudesGuias(guiaList);
        listaSolicitudesGuiasAdapter.setContext(SolicitudesGuiasActivity.this);
        binding.recyclerView.setAdapter(listaSolicitudesGuiasAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(SolicitudesGuiasActivity.this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        cargarLista();
    }


}

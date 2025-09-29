package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.silkroad_iot.ui.superadmin.entity.Global;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.databinding.ActivitySuperadminAdministradoresBinding;
import com.example.silkroad_iot.ui.superadmin.entity.Administrador;
import com.example.silkroad_iot.ui.superadmin.entity.ListaAdministradoresAdapter;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class AdministradoresActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ActivitySuperadminAdministradoresBinding binding;
    private List<Administrador> administradorList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

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
        administradorList = Global.listaAdministradores;
        ListaAdministradoresAdapter listaAdministradoresAdapter = new ListaAdministradoresAdapter();
        listaAdministradoresAdapter.setListaAdministradores(administradorList);
        listaAdministradoresAdapter.setContext(AdministradoresActivity.this);
        binding.recyclerView.setAdapter(listaAdministradoresAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(AdministradoresActivity.this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        cargarLista();
    }

    public void crearAdministrador(View view){
        Intent intent = new Intent(this, CrearAdministradorActivity.class);
        startActivity(intent);
    }


}
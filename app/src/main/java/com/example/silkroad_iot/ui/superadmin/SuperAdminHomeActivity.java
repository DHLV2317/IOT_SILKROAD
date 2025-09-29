package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.ui.superadmin.entity.Administrador;
import com.example.silkroad_iot.ui.superadmin.entity.Cliente;
import com.example.silkroad_iot.ui.superadmin.entity.Global;
import com.example.silkroad_iot.ui.superadmin.entity.Guia;
import com.example.silkroad_iot.ui.superadmin.entity.Log;
import com.example.silkroad_iot.ui.superadmin.entity.ReporteDTO;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.util.Date;


public class SuperAdminHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_home);

        if(Global.sc.equals("Init")){
            Administrador admin = new Administrador();
            admin.setNombre("Nombre administrador");
            admin.setNombreEmpresa("Empresa 1");
            admin.setUbicacion("Ubicación 1");
            admin.setCorreo("correo1@correo1");
            admin.setTelefono("987654321");
            admin.setContrasena("contrasenia");
            admin.setActivo(true);
            Global.listaAdministradores.add(admin);
            Guia guia = new Guia();
            guia.setNombres("AAA");
            guia.setApellidos("AAA");
            guia.setCorreo("AAA");
            guia.setTelefono("999999999");
            guia.setDomicilio("AAA");
            guia.setFechaNacimiento(new Date());
            guia.setIdiomas("AAA");
            guia.setNumeroDocumento("AAA");
            guia.setTipoDocumento("AAA");
            guia.setFoto1((byte) 1);
            guia.setActivo(true);
            guia.setContrasena("AAA");
            guia.setAprobado(false);
            Global.listaGuiasNoAprobados.add(guia);
            guia.setAprobado(true);
            Global.listaGuiasAprobados.add(guia);
            Cliente cliente = new Cliente();
            cliente.setNombres("AAA");
            cliente.setApellidos("AAA");
            cliente.setCorreo("AAA");
            cliente.setTelefono("999999999");
            cliente.setDomicilio("AAA");
            cliente.setFechaNacimiento(new Date());
            cliente.setNumeroDocumento("AAA");
            cliente.setTipoDocumento("AAA");
            cliente.setFoto((byte) 1);
            cliente.setActivo(true);
            cliente.setContrasena("AAA");
            Global.listaClientes.add(cliente);
            ReporteDTO reporte = new ReporteDTO();
            reporte.setDescripcion("Reporte 1");
            Global.listaReportes.add(reporte);
            Log log = new Log();
            log.setNombre("Log 1");
            log.setTipo("Tipo 1");
            log.setFecha(new Date());
            log.setHora("Hora 1");
            log.setUsuario("Usuario 1");
            log.setTipoUsuario("Superadmin");
            log.setUsuarioAfectado("Usuario afectado 1");
            log.setTipoUsuarioAfectado("Cliente");
            log.setDescripcion("Log 1 descripcion");
            Global.listaLogs.add(log);
            Global.sc="Done";
        }
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


}
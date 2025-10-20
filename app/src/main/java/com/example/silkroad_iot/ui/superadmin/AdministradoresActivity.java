package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.databinding.ActivitySuperadminAdministradoresBinding;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Lista de administradores usando SOLO com.example.silkroad_iot.data.User
 * Fuente: Firestore /users con role == "ADMIN"
 */
public class AdministradoresActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ADMINISTRADORES";

    private ActivitySuperadminAdministradoresBinding binding;

    // Drawer / Toolbar
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

    // Firestore
    private FirebaseFirestore db;

    // Datos y adapter (User)
    private final List<User> adminList = new ArrayList<>();
    private AdminsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminAdministradoresBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        drawerLayout   = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Recycler
        adapter = new AdminsAdapter(adminList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        // Firestore
        db = FirebaseFirestore.getInstance();

        // Cargar admins
        cargarAdministradoresDesdeFirestore();
    }

    private void cargarAdministradoresDesdeFirestore() {
        adminList.clear();
        db.collection("users")
                .whereEqualTo("role", "ADMIN") // en Firestore el rol está almacenado como string
                .get()
                .addOnSuccessListener(snap -> {
                    for (QueryDocumentSnapshot d : snap) {
                        User u = new User();
                        // Campos mínimos seguros
                        Object name  = d.get("name");
                        Object email = d.get("email");
                        Object role  = d.get("role");

                        u.setName(name == null ? "" : String.valueOf(name));
                        u.setEmail(email == null ? "" : String.valueOf(email));
                        // Opcional: si necesitas más campos
                        if (d.contains("phone"))    u.setPhone(String.valueOf(d.get("phone")));
                        if (d.contains("address"))  u.setAddress(String.valueOf(d.get("address")));
                        if (d.contains("uid"))      u.setUid(String.valueOf(d.get("uid")));

                        // Rol para compatibilidad local (no imprescindible para mostrar)
                        try {
                            if (role != null) {
                                u.setRole(User.Role.valueOf(String.valueOf(role).toUpperCase()));
                            } else {
                                u.setRole(User.Role.ADMIN);
                            }
                        } catch (Exception ignore) {
                            u.setRole(User.Role.ADMIN);
                        }

                        adminList.add(u);
                        Log.d(TAG, "Admin: " + u.getName() + " <" + u.getEmail() + ">");
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error cargando admins", e));
    }

    // ==== Toolbar ====
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    // ==== Drawer ====
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

    // ==== Acción botón "Crear Administrador" (si lo usas en el layout) ====
    public void crearAdministrador(android.view.View view){
        startActivity(new Intent(this, CrearAdministradorActivity.class));
    }

    // ===== Adapter simple usando User =====
    private static class AdminsAdapter extends RecyclerView.Adapter<AdminsAdapter.VH> {
        private final List<User> data;
        AdminsAdapter(List<User> data){ this.data = data; }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            User u = data.get(pos);
            h.txt1.setText(u.getName() == null || u.getName().isEmpty() ? "(Sin nombre)" : u.getName());
            h.txt2.setText(u.getEmail() == null || u.getEmail().isEmpty() ? "(Sin email)" : u.getEmail());
        }

        @Override public int getItemCount(){ return data.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView txt1, txt2;
            VH(@NonNull View itemView) {
                super(itemView);
                txt1 = itemView.findViewById(android.R.id.text1);
                txt2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}
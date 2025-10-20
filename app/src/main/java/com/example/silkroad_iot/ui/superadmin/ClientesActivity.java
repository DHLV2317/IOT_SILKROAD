package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.silkroad_iot.databinding.ActivitySuperadminClientesBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClientesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivitySuperadminClientesBinding binding;
    private final List<User> data = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ClientsAdapter adapter;
    private FirebaseFirestore db;

    @Override protected void onCreate(Bundle savedInstanceState) {
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
        drawerLayout.addDrawerListener(toggle); toggle.syncState();

        adapter = new ClientsAdapter(data);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
    }

    @Override protected void onStart() {
        super.onStart();
        cargarLista();
    }

    private void cargarLista() {
        data.clear();
        db.collection("users")
                .whereEqualTo("role", "CLIENT")
                .get()
                .addOnSuccessListener(snap -> {
                    for (QueryDocumentSnapshot d : snap) {
                        User u = d.toObject(User.class);
                        // fallback mínimos
                        if (u.getEmail() == null) u.setEmail(String.valueOf(d.get("email")));
                        if (u.getName()  == null) u.setName(String.valueOf(d.get("name")));
                        data.add(u);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_inicio) startActivity(new Intent(this, SuperAdminHomeActivity.class));
        else if (id == R.id.nav_administradores) startActivity(new Intent(this, AdministradoresActivity.class));
        else if (id == R.id.nav_solicitudes_guias) startActivity(new Intent(this, SolicitudesGuiasActivity.class));
        else if (id == R.id.nav_guias) startActivity(new Intent(this, GuiasActivity.class));
        else if (id == R.id.nav_clientes) { /* aquí */ }
        else if (id == R.id.nav_reportes) startActivity(new Intent(this, ReportesActivity.class));
        else if (id == R.id.nav_logs) startActivity(new Intent(this, LogsActivity.class));
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Adapter mínimo
    private static class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.VH> {
        private final List<User> items;
        ClientsAdapter(List<User> items){ this.items = items; }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
            View view = LayoutInflater.from(p.getContext())
                    .inflate(android.R.layout.simple_list_item_2, p, false);
            return new VH(view);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            User u = items.get(pos);
            h.t1.setText(u.getName() == null || u.getName().isEmpty() ? "(Sin nombre)" : u.getName());
            h.t2.setText(u.getEmail() == null || u.getEmail().isEmpty() ? "(Sin email)" : u.getEmail());
        }

        @Override public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView t1, t2;
            VH(@NonNull View v){ super(v); t1 = v.findViewById(android.R.id.text1); t2 = v.findViewById(android.R.id.text2); }
        }
    }
}
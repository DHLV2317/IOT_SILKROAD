package com.example.silkroad_iot.ui.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.AdminRepository;
import com.example.silkroad_iot.databinding.ContentAdminReservationsBinding;
import com.example.silkroad_iot.ui.common.BaseDrawerActivity;

public class AdminReservationsActivity extends BaseDrawerActivity {

    private ContentAdminReservationsBinding b;
    private final AdminRepository repo = AdminRepository.get();
    private AdminReservationsAdapter adapter;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inserta el contenido dentro del Drawer y toma la toolbar del Drawer
        setupDrawer(R.layout.content_admin_reservations, R.menu.menu_drawer_admin, "Reservas");

        // Binding al layout de contenido (sin toolbar propia)
        b = ContentAdminReservationsBinding.bind(findViewById(R.id.rootContent));

        // Lista + adapter
        adapter = new AdminReservationsAdapter(repo.getReservations());
        b.list.setLayoutManager(new LinearLayoutManager(this));
        b.list.setAdapter(adapter);

        // Búsqueda por texto
        b.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                adapter.filter(s == null ? "" : s.toString(), adapter.getStatusFilter());
            }
        });

        // Filtro por estado (dropdown)
        String[] estados = new String[]{"Todos", "pendiente", "check-in", "check-out", "finalizada", "cancelada"};
        ArrayAdapter<String> stAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, estados);
        b.inputStatus.setAdapter(stAdapter);
        b.inputStatus.setText("Todos", false);
        b.inputStatus.setOnItemClickListener((parent, view, position, id) -> {
            String sel = estados[position];
            adapter.setStatusFilter(sel);
            String q = b.inputSearch.getText() == null ? "" : b.inputSearch.getText().toString();
            adapter.filter(q, sel);
        });

        // Hook para generar reporte con la lista filtrada actual
        b.btnReport.setOnClickListener(v -> {
            // Ejemplo:
            // List<Object> items = adapter.getCurrentItems();
            // TODO: Generar PDF/Share con 'items'
        });
    }

    @Override protected int defaultMenuId() { return R.id.m_reservations; }
}
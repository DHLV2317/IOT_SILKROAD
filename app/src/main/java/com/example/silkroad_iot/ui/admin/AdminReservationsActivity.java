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

        // Búsqueda
        b.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                adapter.filter(s == null ? "" : s.toString(), adapter.getStatusFilter());
            }
        });

        // Filtro por estado
        String[] estados = new String[]{"Todos", "pendiente", "check-in", "check-out", "finalizada", "cancelada"};
        ArrayAdapter<String> stAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, estados);
        b.inputStatus.setAdapter(stAdapter);
        b.inputStatus.setText("Todos", false);
        b.inputStatus.setOnItemClickListener((parent, view, position, id) -> {
            String sel = estados[position];
            adapter.setStatusFilter(sel);
            adapter.filter(b.inputSearch.getText() == null ? "" : b.inputSearch.getText().toString(), sel);
        });

        // Generar PDF (engancha tu generador aquí)
        b.btnReport.setOnClickListener(v -> {
            // TODO: usa adapter.getCurrentItems() para armar el PDF
        });
    }

    @Override protected int defaultMenuId() { return R.id.m_reservations; }
}
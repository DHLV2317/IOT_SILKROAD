package com.example.silkroad_iot.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.AdminRepository;
import com.example.silkroad_iot.databinding.ContentAdminGuidesBinding;
import com.example.silkroad_iot.ui.common.BaseDrawerActivity;

import java.util.List;

public class AdminGuidesActivity extends BaseDrawerActivity {

    private ContentAdminGuidesBinding b;
    private final AdminRepository repo = AdminRepository.get();
    private AdminGuidesAdapter adapter;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Monta el "content" dentro del drawer. El título lo pone el toolbar del drawer.
        setupDrawer(R.layout.content_admin_guides, R.menu.menu_drawer_admin, "Guías");

        // Binding del content
        b = ContentAdminGuidesBinding.bind(findViewById(R.id.rootContent));

        // Recycler
        adapter = new AdminGuidesAdapter(repo.getGuides());
        b.list.setLayoutManager(new LinearLayoutManager(this));
        b.list.setAdapter(adapter);

        // Buscar
        b.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                adapter.filter(s == null ? "" : s.toString());
            }
        });

        // No hay botón +Asignar guía (el requerimiento fue eliminarlo).
    }

    @Override protected int defaultMenuId() { return R.id.m_guides; }

    /* ============================
     *  Llamadas desde el Adapter
     * ============================ */

    /** Abre un selector de tours y asigna el elegido al guía indicado. */
    public void showAssignTourDialog(int guideIndex) {
        // Guía
        AdminRepository.Guide g = repo.getGuides().get(guideIndex);

        // Lista de tours disponibles
        List<AdminRepository.Tour> tours = repo.getTours();
        if (tours == null || tours.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setMessage("No hay tours disponibles para asignar.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        String[] names = new String[tours.size()];
        for (int i = 0; i < tours.size(); i++) {
            String n = tours.get(i).name;
            names[i] = (n == null || n.trim().isEmpty()) ? "(Sin nombre)" : n;
        }

        new AlertDialog.Builder(this)
                .setTitle("Asignar tour a " + (g.name == null ? "guía" : g.name))
                .setItems(names, (dialog, which) -> {
                    AdminRepository.Tour chosen = tours.get(which);

                    // Actualiza guía
                    g.currentTour = chosen.name;
                    g.state = "Ocupado";
                    if (g.history != null) g.history.add("Asignado a: " + chosen.name);

                    // Marca en el tour opcionalmente
                    chosen.assignedGuideName = g.name;

                    // Refresca solo ese ítem
                    if (adapter != null) adapter.notifyItemChanged(guideIndex);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /** Navega a detalle del guía (si lo vuelves a usar en el adapter). */
    public void showGuideDetail(int guideIndex) {
        Intent it = new Intent(this, AdminGuideDetailActivity.class);
        it.putExtra("index", guideIndex);
        startActivity(it);
    }
}
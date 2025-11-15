package com.example.silkroad_iot.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.GuideFb;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.databinding.ContentAdminGuidesBinding;
import com.example.silkroad_iot.ui.common.BaseDrawerActivity;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminGuidesActivity extends BaseDrawerActivity {

    private ContentAdminGuidesBinding b;

    private FirebaseFirestore db;
    private ListenerRegistration guidesReg;

    private final List<GuideFb> guides = new ArrayList<>();
    private final List<TourFB>  toursCache = new ArrayList<>();

    private AdminGuidesAdapter adapter;

    // si luego quieres filtrar por empresa, podemos re-usar esto
    private static final String PREFS = "app_prefs";
    private static final String KEY_EMPRESA_ID = "empresa_id";
    private String empresaId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupDrawer(R.layout.content_admin_guides, R.menu.menu_drawer_admin, "Guías");
        b = ContentAdminGuidesBinding.bind(findViewById(R.id.rootContent));

        db = FirebaseFirestore.getInstance();
        empresaId = getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_EMPRESA_ID, null);

        adapter = new AdminGuidesAdapter(guides, new AdminGuidesAdapter.Callbacks() {
            @Override public void onAssignClicked(int position) { showAssignTourDialog(position); }
            @Override public void onDetailClicked(int position) { showGuideDetail(position); }
        });

        b.list.setLayoutManager(new LinearLayoutManager(this));
        b.list.setAdapter(adapter);

        // búsqueda local
        b.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                adapter.filter(s == null ? "" : s.toString());
            }
        });

        attachGuidesListener();
        preloadTours();
    }

    @Override
    protected int defaultMenuId() { return R.id.m_guides; }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (guidesReg != null) guidesReg.remove();
    }

    // ============================================================
    //   ESCUCHA DIRECTAMENTE LA COLECCIÓN "guias"
    //   SOLO LOS QUE ESTÁN APROBADOS
    // ============================================================

    private void attachGuidesListener() {
        showLoading(true);

        if (guidesReg != null) guidesReg.remove();

        // Colección correcta: guias
        Query q = db.collection("guias")
                .whereEqualTo("guideApproved", true)
                .whereEqualTo("guideApprovalStatus", "APPROVED");

        // ⚠️ De momento NO filtramos por empresaId porque en tus docs de "guias"
        // no se ve ese campo. Si luego lo agregas, aquí se puede activar:
        /*
        if (empresaId != null && !empresaId.trim().isEmpty()) {
            q = q.whereEqualTo("empresaId", empresaId);
        }
        */

        guidesReg = q.addSnapshotListener(guidesListener);
    }

    private final EventListener<QuerySnapshot> guidesListener = (snap, err) -> {
        if (err != null) {
            showLoading(false);
            showEmpty("Error al cargar guías");
            Toast.makeText(this, "Error: " + err.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        if (snap == null || snap.isEmpty()) {
            guides.clear();
            adapter.updateData(guides);
            showLoading(false);
            showEmpty("No hay guías aprobados.");
            return;
        }

        List<GuideFb> list = new ArrayList<>();
        for (DocumentSnapshot d : snap.getDocuments()) {
            GuideFb g = d.toObject(GuideFb.class);
            if (g != null) {
                g.setId(d.getId());   // id del doc en "guias"
                list.add(g);
            }
        }

        guides.clear();
        guides.addAll(list);
        adapter.updateData(list);

        if (list.isEmpty()) {
            showEmpty("No hay guías aprobados.");
        } else {
            hideEmpty();
        }
        showLoading(false);
    };

    // ============================================================
    //                  PRELOAD DE TOURS
    // ============================================================

    private void preloadTours() {
        Query q = db.collection("tours");
        if (empresaId != null && !empresaId.trim().isEmpty()) {
            q = q.whereEqualTo("empresaId", empresaId);
        }

        q.get().addOnSuccessListener(snap -> {
            toursCache.clear();
            for (DocumentSnapshot d : snap) {
                try {
                    Map<String, Object> data = d.getData();
                    if (data != null) data.remove("id_paradas");

                    TourFB t = d.toObject(TourFB.class);
                    if (t != null) {
                        if (t.getId() == null || t.getId().trim().isEmpty()) {
                            t.setId(d.getId());
                        }
                        t.setId_paradas(new ArrayList<>());
                        toursCache.add(t);
                    }
                } catch (Exception ex) {
                    Log.e("PRELOAD_TOURS", "Error parseando tour: " + ex);
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this,
                        "No se pudieron precargar tours: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    // ============================================================
    //             ACCIONES DEL ADAPTER
    // ============================================================

    public void showAssignTourDialog(int idx) {
        if (idx < 0 || idx >= guides.size()) return;
        GuideFb g = guides.get(idx);

        if (toursCache.isEmpty()) {
            preloadTours();
            Toast.makeText(this, "Cargando tours disponibles…", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] names = new String[toursCache.size()];
        for (int i = 0; i < toursCache.size(); i++) {
            String n = toursCache.get(i).getDisplayName();
            names[i] = (n == null || n.trim().isEmpty()) ? "(Sin nombre)" : n;
        }

        new AlertDialog.Builder(this)
                .setTitle("Asignar tour a " + (g.getNombre() == null ? "guía" : g.getNombre()))
                .setItems(names, (dialog, which) -> {
                    TourFB chosen = toursCache.get(which);
                    assignTourToGuide(g, chosen, idx);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void assignTourToGuide(GuideFb guide, TourFB tour, int idx) {
        String newState = "Ocupado";
        String newCurrentTour = tour.getDisplayName();

        DocumentReference guideRef = db.collection("guias").document(guide.getId());
        DocumentReference tourRef  = db.collection("tours").document(tour.getId());

        Tasks.whenAll(
                guideRef.update(
                        "estado", newState,
                        "tourActual", newCurrentTour
                ),
                tourRef.update(
                        "assignedGuideName",
                        guide.getNombre() == null ? "" : guide.getNombre()
                )
        ).addOnSuccessListener(unused -> {
            guide.setEstado(newState);
            guide.setTourActual(newCurrentTour);

            if (guide.getHistorial() != null) {
                guide.getHistorial().add("Asignado a: " + newCurrentTour);
            }

            b.list.post(() -> {
                adapter.notifyItemChanged(idx);
                Toast.makeText(this,
                        "Tour asignado correctamente",
                        Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e ->
                Toast.makeText(this,
                        "Error al asignar tour: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
    }

    public void showGuideDetail(int idx) {
        if (idx < 0 || idx >= guides.size()) return;
        GuideFb g = guides.get(idx);
        Intent i = new Intent(this, AdminGuideDetailActivity.class);
        i.putExtra("guideId", g.getId());
        startActivity(i);
    }

    // ============================================================
    //                     UI HELPERS
    // ============================================================

    private void showLoading(boolean show) {
        b.progress.setVisibility(show ? View.VISIBLE : View.GONE);
        b.list.setAlpha(show ? 0.4f : 1f);
        b.list.setEnabled(!show);
    }

    private void showEmpty(String msg) {
        b.tEmpty.setText(msg);
        b.tEmpty.setVisibility(View.VISIBLE);
    }

    private void hideEmpty() {
        b.tEmpty.setVisibility(View.GONE);
    }
}
package com.example.silkroad_iot.ui.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.data.GuideFb;
import com.example.silkroad_iot.databinding.ContentAdminGuidesBinding;
import com.example.silkroad_iot.ui.common.BaseDrawerActivity;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Lista de guías (Firestore) con filtro y asignación de tour.
 * - Colección "guias" -> GuideFb
 * - Colección "tours" -> TourFB
 */
public class AdminGuidesActivity extends BaseDrawerActivity {

    private ContentAdminGuidesBinding b;

    // Firestore
    private FirebaseFirestore db;
    private final List<GuideFb> guides = new ArrayList<>();
    private final List<TourFB> toursCache = new ArrayList<>();

    private AdminGuidesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Monta el "content" dentro del drawer
        setupDrawer(R.layout.content_admin_guides, R.menu.menu_drawer_admin, "Guías");
        b = ContentAdminGuidesBinding.bind(findViewById(R.id.rootContent));

        db = FirebaseFirestore.getInstance();

        // RecyclerView
        adapter = new AdminGuidesAdapter(guides, new AdminGuidesAdapter.Callbacks() {
            @Override public void onAssignClicked(int position) { showAssignTourDialog(position); }
            @Override public void onDetailClicked(int position) { showGuideDetail(position); }
        });

        b.list.setLayoutManager(new LinearLayoutManager(this));
        b.list.setAdapter(adapter);

        // Filtro de búsqueda
        b.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                adapter.filter(s == null ? "" : s.toString());
            }
        });

        // Cargar datos iniciales
        loadGuides();
        preloadTours();
    }

    @Override protected int defaultMenuId() { return R.id.m_guides; }

    /* ============================
     *        CARGAS FIRESTORE
     * ============================ */

    private void loadGuides() {
        db.collection("guias").get()
                .addOnSuccessListener(snap -> {
                    List<GuideFb> list = new ArrayList<>();
                    for (DocumentSnapshot d : snap) {
                        GuideFb g = d.toObject(GuideFb.class);
                        if (g != null) {
                            g.setId(d.getId());
                            list.add(g);
                        }
                    }
                    guides.clear();
                    guides.addAll(list);
                    adapter.updateData(list);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar guías: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void preloadTours() {
        db.collection("tours").get()
                .addOnSuccessListener(snap -> {
                    toursCache.clear();
                    for (DocumentSnapshot d : snap) {
                        TourFB t = d.toObject(TourFB.class);
                        if (t != null) {
                            if (t.getId() == null || t.getId().trim().isEmpty()) t.setId(d.getId());
                            toursCache.add(t);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "No se pudieron precargar tours: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /* ============================
     *      ACCIONES DEL ADAPTER
     * ============================ */

    /** Abre selector y asigna tour actualizando Firestore. */
    public void showAssignTourDialog(int guideIndex) {
        if (guideIndex < 0 || guideIndex >= guides.size()) return;
        GuideFb g = guides.get(guideIndex);

        if (toursCache.isEmpty()) {
            preloadTours();
            Toast.makeText(this, "Cargando tours disponibles...", Toast.LENGTH_SHORT).show();
            return;
        }

        openAssignDialogInternal(g, guideIndex);
    }

    private void openAssignDialogInternal(GuideFb g, int guideIndex) {
        if (toursCache.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setMessage("No hay tours disponibles para asignar.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        String[] names = new String[toursCache.size()];
        for (int i = 0; i < toursCache.size(); i++) {
            String n = toursCache.get(i).getNombre();
            names[i] = (n == null || n.trim().isEmpty()) ? "(Sin nombre)" : n;
        }

        new AlertDialog.Builder(this)
                .setTitle("Asignar tour a " + (g.getNombre() == null ? "guía" : g.getNombre()))
                .setItems(names, (dialog, which) -> {
                    TourFB chosen = toursCache.get(which);
                    assignTourToGuide(g, chosen, guideIndex);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void assignTourToGuide(GuideFb guide, TourFB tour, int guideIndex) {
        String newState = "Ocupado";
        String newCurrentTour = tour.getNombre() == null ? "" : tour.getNombre();

        DocumentReference guideRef = db.collection("guias").document(guide.getId());
        DocumentReference tourRef  = db.collection("tours").document(tour.getId());

        Tasks.whenAll(
                guideRef.update(
                        "estado", newState,
                        "tourActual", newCurrentTour
                ),
                tourRef.update(
                        "assignedGuideName", guide.getNombre() == null ? "" : guide.getNombre()
                )
        ).addOnSuccessListener(unused -> {
            guide.setEstado(newState);
            guide.setTourActual(newCurrentTour);
            if (guide.getHistorial() != null) {
                guide.getHistorial().add("Asignado a: " + newCurrentTour);
            }
            adapter.notifyItemChanged(guideIndex);
            Toast.makeText(this, "Tour asignado correctamente", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error al asignar tour: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    /** Abre detalle del guía (si lo usas). */
    public void showGuideDetail(int guideIndex) {
        Toast.makeText(this, "Detalle guía idx=" + guideIndex, Toast.LENGTH_SHORT).show();
    }
}
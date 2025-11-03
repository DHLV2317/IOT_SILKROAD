package com.example.silkroad_iot.ui.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Lista de guías en tiempo real desde Firestore con filtro y asignación.
 * Colecciones:
 *  - "guias" (GuideFb)
 *  - "tours" (TourFB)
 *
 * Filtra por empresa si guardas empresaId en SharedPrefs ("empresa_id").
 */
public class AdminGuidesActivity extends BaseDrawerActivity {

    private ContentAdminGuidesBinding b;

    // Firestore
    private FirebaseFirestore db;
    private ListenerRegistration guidesReg;

    private final List<GuideFb> guides = new ArrayList<>();
    private final List<TourFB> toursCache = new ArrayList<>();

    private AdminGuidesAdapter adapter;

    // Prefs
    private static final String PREFS = "app_prefs";
    private static final String KEY_EMPRESA_ID = "empresa_id";
    private String empresaId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Monta el "content" dentro del drawer
        setupDrawer(R.layout.content_admin_guides, R.menu.menu_drawer_admin, "Guías");
        b = ContentAdminGuidesBinding.bind(findViewById(R.id.rootContent));

        db = FirebaseFirestore.getInstance();
        empresaId = getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_EMPRESA_ID, null);

        // RecyclerView
        adapter = new AdminGuidesAdapter(guides, new AdminGuidesAdapter.Callbacks() {
            @Override public void onAssignClicked(int position) { showAssignTourDialog(position); }
            @Override public void onDetailClicked(int position) { showGuideDetail(position); }
        });

        b.list.setLayoutManager(new LinearLayoutManager(this));
        b.list.setAdapter(adapter);

        // Búsqueda local
        b.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                adapter.filter(s == null ? "" : s.toString());
            }
        });

        // Cargas
        attachGuidesListener();
        preloadTours(); // para el diálogo de asignación
    }

    @Override protected int defaultMenuId() { return R.id.m_guides; }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (guidesReg != null) {
            guidesReg.remove();
            guidesReg = null;
        }
    }

    /* ============================
     *        CARGAS FIRESTORE
     * ============================ */

    private void attachGuidesListener() {
        showLoading(true);

        if (guidesReg != null) {
            guidesReg.remove();
            guidesReg = null;
        }

        // Consulta: si tienes empresaId, filtra por esa empresa; si no, trae todo
        if (empresaId != null && !empresaId.trim().isEmpty()) {
            guidesReg = db.collection("guias")
                    .whereEqualTo("empresaId", empresaId)
                    .addSnapshotListener(guidesListener);
        } else {
            guidesReg = db.collection("guias")
                    .addSnapshotListener(guidesListener);
        }
    }

    private final EventListener<QuerySnapshot> guidesListener = (snap, err) -> {
        if (err != null) {
            showLoading(false);
            showEmpty("Error al cargar guías");
            Toast.makeText(this, "Error: " + err.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        if (snap == null) {
            showLoading(false);
            showEmpty("Sin resultados");
            return;
        }

        List<GuideFb> list = new ArrayList<>();
        for (DocumentSnapshot d : snap.getDocuments()) {
            GuideFb g = d.toObject(GuideFb.class);
            if (g != null) {
                g.setId(d.getId());
                list.add(g);
            }
        }

        guides.clear();
        guides.addAll(list);
        adapter.updateData(list);

        showLoading(false);
        if (list.isEmpty()) {
            showEmpty(getString(R.string.empty_guides));
        } else {
            hideEmpty();
        }
    };

    private void preloadTours() {
        if (empresaId != null && !empresaId.trim().isEmpty()) {
            db.collection("tours")
                    .whereEqualTo("empresaId", empresaId)
                    .get()
                    .addOnSuccessListener(snap -> {
                        toursCache.clear();
                        for (DocumentSnapshot d : snap) {
                            try {
                                // Convertimos a objeto TourFB ignorando id_paradas del documento
                                Map<String, Object> data = d.getData();
                                if (data != null) data.remove("id_paradas");

                                TourFB t = d.toObject(TourFB.class);

                                if (t != null) {
                                    if (t.getId() == null || t.getId().trim().isEmpty())
                                        t.setId(d.getId());

                                    // Asignamos lista vacía para evitar deserialización fallida
                                    t.setId_paradas(new ArrayList<>());

                                    toursCache.add(t);
                                }
                            } catch (Exception ex) {
                                Log.e("PRELOAD_TOURS", "Error parseando tour: " + ex);
                            }
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "No se pudieron precargar tours: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            db.collection("tours")
                    .get()
                    .addOnSuccessListener(snap -> {
                        toursCache.clear();
                        for (DocumentSnapshot d : snap) {
                            try {
                                Map<String, Object> data = d.getData();
                                if (data != null) data.remove("id_paradas");

                                TourFB t = d.toObject(TourFB.class);

                                if (t != null) {
                                    if (t.getId() == null || t.getId().trim().isEmpty())
                                        t.setId(d.getId());

                                    t.setId_paradas(new ArrayList<>());

                                    toursCache.add(t);
                                }
                            } catch (Exception ex) {
                                Log.e("PRELOAD_TOURS", "Error parseando tour: " + ex);
                            }
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "No se pudieron precargar tours: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
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
            String n = toursCache.get(i).getDisplayName();
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
        String newCurrentTour = tour.getDisplayName();

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
            // refresco optimista
            b.list.post(() -> {
                adapter.notifyItemChanged(guideIndex);
                Toast.makeText(this, "Tour asignado correctamente", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error al asignar tour: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    /** Abre detalle del guía (placeholder). */
    public void showGuideDetail(int guideIndex) {
        Toast.makeText(this, "Detalle guía idx=" + guideIndex, Toast.LENGTH_SHORT).show();
    }

    /* ============================
     *   UI helpers: loader/empty
     * ============================ */
    private void showLoading(boolean show) {
        b.progress.setVisibility(show ? View.VISIBLE : View.GONE);
        b.list.setAlpha(show ? 0.5f : 1f);
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
package com.example.silkroad_iot.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.GuideFb;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.databinding.ContentAdminGuidesBinding;
import com.example.silkroad_iot.ui.common.BaseDrawerActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.*;

import java.util.*;

public class AdminGuidesActivity extends BaseDrawerActivity implements OnMapReadyCallback {

    private ContentAdminGuidesBinding b;

    private FirebaseFirestore db;
    private ListenerRegistration guidesReg;

    private final List<GuideFb> guides = new ArrayList<>();
    private final List<TourFB> toursCache = new ArrayList<>();

    private AdminGuidesAdapter adapter;

    private GoogleMap mMap;
    private boolean isMapReady = false;

    private final Map<String, Marker> markers = new HashMap<>();
    private final Map<String, Polyline> guidePolylines = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupDrawer(R.layout.content_admin_guides, R.menu.menu_drawer_admin, "Guías");
        b = ContentAdminGuidesBinding.bind(findViewById(R.id.rootContent));

        db = FirebaseFirestore.getInstance();

        adapter = new AdminGuidesAdapter(guides, new AdminGuidesAdapter.Callbacks() {
            @Override public void onAssignClicked(int pos) { showAssignTourDialog(pos); }
            @Override public void onDetailClicked(int pos) { showGuideDetail(pos); }
        });

        b.list.setLayoutManager(new LinearLayoutManager(this));
        b.list.setAdapter(adapter);

        b.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                adapter.filter(s == null ? "" : s.toString());
            }
        });

        b.progress.setVisibility(View.VISIBLE);
        b.tEmpty.setVisibility(View.GONE);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAdmin);
        if (mapFragment != null) mapFragment.getMapAsync(this);

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
    //   MAPA
    // ============================================================

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        isMapReady = true;

        LatLng lima = new LatLng(-12.0464, -77.0428);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lima, 12f));

        mMap.setOnMarkerClickListener(marker -> {
            Object tag = marker.getTag();
            if (tag instanceof String) {
                String guideId = (String) tag;
                loadGuidePath(guideId, marker);
            }
            return false; // para que igual muestre la info window
        });

        refreshMarkers();
    }

    private void refreshMarkers() {
        if (!isMapReady || mMap == null) return;

        for (GuideFb g : guides) {
            if (g.getLatActual() == null || g.getLngActual() == null) continue;

            LatLng pos = new LatLng(g.getLatActual(), g.getLngActual());
            Marker mk = markers.get(g.getId());

            if (mk == null) {
                mk = mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(g.getNombre())
                        .snippet("Estado: " + g.getEstado()));
                if (mk != null) mk.setTag(g.getId());
                markers.put(g.getId(), mk);
            } else {
                mk.setPosition(pos);
                mk.setTitle(g.getNombre());
                mk.setSnippet("Estado: " + g.getEstado());
            }
        }
    }

    // Carga las paradas (ubicaciones) del guía y dibuja Polyline
    private void loadGuidePath(String guideId, Marker marker) {
        db.collection("guias")
                .document(guideId)
                .collection("ubicaciones")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    if (!isMapReady || mMap == null) return;

                    // Eliminar polyline anterior del mismo guía
                    Polyline old = guidePolylines.get(guideId);
                    if (old != null) {
                        old.remove();
                    }

                    List<LatLng> points = new ArrayList<>();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        Double lat = d.getDouble("lat");
                        Double lng = d.getDouble("lng");
                        if (lat != null && lng != null) {
                            points.add(new LatLng(lat, lng));
                        }
                    }

                    if (points.isEmpty()) {
                        Toast.makeText(this,
                                "Este guía aún no tiene paradas registradas.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    PolylineOptions opts = new PolylineOptions()
                            .addAll(points)
                            .width(8f);

                    Polyline poly = mMap.addPolyline(opts);
                    guidePolylines.put(guideId, poly);

                    // Centrar cámara en la última parada
                    LatLng last = points.get(points.size() - 1);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(last, 16f));

                    Toast.makeText(this,
                            "Mostrando recorrido (" + points.size() + " paradas).",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Error al cargar recorrido: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    // ============================================================
    //   FIRESTORE LISTENER (guias)
    // ============================================================

    private void attachGuidesListener() {
        Query q = db.collection("guias")
                .whereEqualTo("guideApproved", true)
                .whereEqualTo("guideApprovalStatus", "APPROVED");

        guidesReg = q.addSnapshotListener((snap, err) -> {
            if (err != null || snap == null) {
                b.progress.setVisibility(View.GONE);
                Toast.makeText(this, "Error cargando guías.", Toast.LENGTH_LONG).show();
                return;
            }

            guides.clear();
            markers.clear();
            for (DocumentSnapshot d : snap.getDocuments()) {
                GuideFb g = d.toObject(GuideFb.class);
                if (g != null) {
                    g.setId(d.getId());
                    guides.add(g);
                }
            }

            adapter.updateData(guides);

            b.progress.setVisibility(View.GONE);
            b.tEmpty.setVisibility(guides.isEmpty() ? View.VISIBLE : View.GONE);

            refreshMarkers();
        });
    }

    // ============================================================
    //   TOURS
    // ============================================================

    private void preloadTours() {
        db.collection("tours").get().addOnSuccessListener(snap -> {
            toursCache.clear();
            for (DocumentSnapshot d : snap) {
                TourFB t = d.toObject(TourFB.class);
                if (t != null) {
                    if (t.getId() == null) t.setId(d.getId());
                    toursCache.add(t);
                }
            }
        });
    }

    public void showAssignTourDialog(int idx) {
        // Dejas tu implementación actual aquí
    }

    public void showGuideDetail(int idx) {
        GuideFb g = guides.get(idx);
        Intent i = new Intent(this, AdminGuideDetailActivity.class);
        i.putExtra("guideId", g.getId());
        startActivity(i);
    }
}
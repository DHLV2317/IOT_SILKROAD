package com.example.silkroad_iot.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.databinding.ActivityAdminTourDetailBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminTourDetailActivity extends AppCompatActivity {
    private ActivityAdminTourDetailBinding b;

    // Firestore
    private FirebaseFirestore db;
    private DocumentReference docRef; // null si es nuevo

    // Modelo TourFB
    private TourFB model;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        b = ActivityAdminTourDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        b.toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        // Si viene un id => editar; si no => crear
        String tourId = getIntent().getStringExtra("id"); // pásalo cuando navegues a editar
        boolean editing = !TextUtils.isEmpty(tourId);
        setTitle(editing ? "Editar Tour" : "Nuevo Tour");
        b.btnDelete.setVisibility(editing ? android.view.View.VISIBLE : android.view.View.GONE);

        if (editing) {
            docRef = db.collection("tours").document(tourId);
            docRef.get().addOnSuccessListener(d -> {
                model = d.toObject(TourFB.class);
                if (model == null) model = new TourFB();
                if (TextUtils.isEmpty(model.getId())) model.setId(d.getId());
                bindModelToUi();
            }).addOnFailureListener(e ->
                    Snackbar.make(b.getRoot(), "Error cargando tour: " + e.getMessage(), Snackbar.LENGTH_LONG).show());
        } else {
            model = new TourFB(); // vacío
            bindModelToUi();
        }

        // Guardar
        b.btnSave.setOnClickListener(v -> saveTour());

        // Eliminar (solo si edita)
        b.btnDelete.setOnClickListener(v -> {
            if (docRef == null) return;
            docRef.delete()
                    .addOnSuccessListener(unused -> {
                        Snackbar.make(b.getRoot(), "Tour eliminado", Snackbar.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Snackbar.make(b.getRoot(), "Error al eliminar: " + e.getMessage(), Snackbar.LENGTH_LONG).show());
        });
    }

    private void bindModelToUi() {
        // Campos de TourFB
        b.inputName.setText(nz(model.getNombre()));
        // Reutilizamos inputDesc para id_paradas (si tu layout no tiene un campo propio)
        b.inputDesc.setHint("ID de paradas (id_paradas)");
        b.inputDesc.setText(nz(model.getId_paradas()));

        double precio = model.getPrecio();
        b.inputPrice.setText(precio == 0 ? "" : String.valueOf(precio));

        int cant = model.getCantidad_personas();
        b.inputPeople.setText(cant == 0 ? "" : String.valueOf(cant));

        // Imagen (url) si existiera
        String url = nz(model.getImagen());
        if (url.isEmpty()) {
            Glide.with(this).load(R.drawable.ic_menu_24).into(b.img);
        } else {
            Glide.with(this).load(url)
                    .placeholder(R.drawable.ic_menu_24)
                    .error(R.drawable.ic_menu_24)
                    .into(b.img);
        }
    }

    private void saveTour() {
        String nombre = b.inputName.getText().toString().trim();
        String sPrecio = b.inputPrice.getText().toString().trim();
        String sPeople = b.inputPeople.getText().toString().trim();
        String idParadas = b.inputDesc.getText().toString().trim(); // reutilizado
        String imagenUrl = ""; // si tienes un input para imagen, léelo aquí.

        if (TextUtils.isEmpty(nombre)) { b.inputName.setError("Requerido"); return; }
        if (TextUtils.isEmpty(sPrecio)) { b.inputPrice.setError("Requerido"); return; }
        if (TextUtils.isEmpty(sPeople)) { b.inputPeople.setError("Requerido"); return; }

        double precio = safeDouble(sPrecio, 0);
        int cantidad = safeInt(sPeople, 1);

        model.setNombre(nombre);
        model.setPrecio(precio);
        model.setCantidad_personas(cantidad);
        model.setId_paradas(idParadas);
        if (!TextUtils.isEmpty(imagenUrl)) model.setImagen(imagenUrl);
        // model.setEmpresaId(...); // si corresponde, setéalo aquí.

        if (docRef == null) {
            docRef = db.collection("tours").document();
            model.setId(docRef.getId());
        }
        docRef.set(model)
                .addOnSuccessListener(unused -> {
                    Snackbar.make(b.getRoot(), "Tour guardado", Snackbar.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Snackbar.make(b.getRoot(), "Error al guardar: " + e.getMessage(), Snackbar.LENGTH_LONG).show());
    }

    private static String nz(String s){ return s == null ? "" : s; }
    private static double safeDouble(String s, double def){
        try { return Double.parseDouble(s); } catch (Exception e){ return def; }
    }
    private static int safeInt(String s, int def){
        try { return Integer.parseInt(s); } catch (Exception e){ return def; }
    }
}
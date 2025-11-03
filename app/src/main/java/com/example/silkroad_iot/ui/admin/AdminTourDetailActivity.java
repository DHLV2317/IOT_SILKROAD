package com.example.silkroad_iot.ui.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.databinding.ActivityAdminTourDetailBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdminTourDetailActivity extends AppCompatActivity {

    private ActivityAdminTourDetailBinding b;

    // Firestore
    private FirebaseFirestore db;
    private DocumentReference docRef; // null si es nuevo

    // Modelo
    private TourFB model;

    // Prefs / claves
    private static final String PREFS = "app_prefs";
    private static final String KEY_EMPRESA_ID = "empresa_id";

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final Calendar calFrom = Calendar.getInstance();
    private final Calendar calTo = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        b = ActivityAdminTourDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        b.toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        String tourId = getIntent().getStringExtra("id");
        boolean editing = !TextUtils.isEmpty(tourId);
        setTitle(editing ? "Editar Tour" : "Nuevo Tour");
        b.btnDelete.setVisibility(editing ? View.VISIBLE : View.GONE);

        if (editing) {
            docRef = db.collection("tours").document(tourId);
            docRef.get()
                    .addOnSuccessListener(d -> {
                        model = d.toObject(TourFB.class);
                        if (model == null) model = new TourFB();
                        if (TextUtils.isEmpty(model.getId())) model.setId(d.getId());
                        bindModelToUi();
                    })
                    .addOnFailureListener(e ->
                            Snackbar.make(b.getRoot(), "Error cargando tour: " + e.getMessage(), Snackbar.LENGTH_LONG).show());
        } else {
            model = new TourFB(); // nuevo
            bindModelToUi();
        }

        // Cargar imagen desde URL pegada
        b.btnLoadImage.setOnClickListener(v -> {
            String url = nz(b.inputImageUrl.getText().toString());
            if (!url.isEmpty()) {
                model.setImagen(url); // usa alias interno
                loadImage(url);
            } else {
                Snackbar.make(b.getRoot(), "Pega una URL de imagen válida", Snackbar.LENGTH_SHORT).show();
            }
        });

        // Date pickers
        b.inputDateFrom.setOnClickListener(v -> showDateDialog(calFrom, (y,m,d)->{
            calFrom.set(y,m,d);
            b.inputDateFrom.setText(sdf.format(calFrom.getTime()));
            model.setDateFrom(calFrom.getTime());
        }));
        b.inputDateTo.setOnClickListener(v -> showDateDialog(calTo, (y,m,d)->{
            calTo.set(y,m,d);
            b.inputDateTo.setText(sdf.format(calTo.getTime()));
            model.setDateTo(calTo.getTime());
        }));

        // Guardar
        b.btnSave.setOnClickListener(v -> saveTour());

        // Eliminar
        b.btnDelete.setOnClickListener(v -> {
            if (docRef == null) return;
            b.btnDelete.setEnabled(false);
            docRef.delete()
                    .addOnSuccessListener(unused -> {
                        Snackbar.make(b.getRoot(), "Tour eliminado", Snackbar.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        b.btnDelete.setEnabled(true);
                        Snackbar.make(b.getRoot(), "Error al eliminar: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    });
        });
    }

    private void bindModelToUi() {
        // Imagen
        String url = nz(model.getDisplayImageUrl());
        loadImage(url);
        b.inputImageUrl.setText(url);

        // Campos
        b.inputName.setText(nz(model.getDisplayName()));
        b.inputDesc.setText(nz(model.getDescription()));
        b.inputLangs.setText(nz(model.getLangs()));

        // id_paradas (como lista -> input separado por comas)
        List<String> ids = model.getId_paradas();
        b.inputStopsId.setText(ids != null && !ids.isEmpty() ? TextUtils.join(", ", ids) : "");

        // Precio / personas
        if (model.getDisplayPrice() > 0) b.inputPrice.setText(String.valueOf(model.getDisplayPrice()));
        if (model.getDisplayPeople() > 0) b.inputPeople.setText(String.valueOf(model.getDisplayPeople()));

        // Fechas
        if (model.getDateFrom() != null) {
            calFrom.setTime(model.getDateFrom());
            b.inputDateFrom.setText(sdf.format(model.getDateFrom()));
        } else {
            b.inputDateFrom.setText("");
        }
        if (model.getDateTo() != null) {
            calTo.setTime(model.getDateTo());
            b.inputDateTo.setText(sdf.format(model.getDateTo()));
        } else {
            b.inputDateTo.setText("");
        }
    }

    private void loadImage(String url) {
        if (TextUtils.isEmpty(url)) {
            Glide.with(this).load(R.drawable.ic_menu_24).into(b.imgHeader);
        } else {
            Glide.with(this).load(url)
                    .placeholder(R.drawable.ic_menu_24)
                    .error(R.drawable.ic_menu_24)
                    .into(b.imgHeader);
        }
    }

    private void saveTour() {
        // Validaciones mínimas
        String nombre  = b.inputName.getText().toString().trim();
        String sPrecio = b.inputPrice.getText().toString().trim();
        String sPeople = b.inputPeople.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) { b.inputName.setError("Requerido"); return; }
        if (TextUtils.isEmpty(sPrecio)) { b.inputPrice.setError("Requerido"); return; }
        if (TextUtils.isEmpty(sPeople)) { b.inputPeople.setError("Requerido"); return; }

        // Empresa del usuario logueado
        String empresaId = getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_EMPRESA_ID, null);
        if (TextUtils.isEmpty(empresaId)) {
            Snackbar.make(b.getRoot(), "No se encontró empresa asignada. Vuelve a ingresar por Empresa.", Snackbar.LENGTH_LONG).show();
            return;
        }

        // Owner UID (útil para filtros alternativos)
        String ownerUid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        // Parseo id_paradas desde input (separadas por coma)
        List<String> idParadas = parseIds(nz(b.inputStopsId.getText().toString()));

        // Setear modelo
        model.setNombre(nombre);
        model.setDescription(nz(b.inputDesc.getText().toString()));
        model.setLangs(nz(b.inputLangs.getText().toString()));
        model.setId_paradas(idParadas);
        model.setPrecio(safeDouble(sPrecio, 0));
        model.setCantidad_personas(safeInt(sPeople, 1));
        model.setEmpresaId(empresaId);
        if (!TextUtils.isEmpty(ownerUid)) model.setOwnerUid(ownerUid);

        String url = nz(b.inputImageUrl.getText().toString());
        if (!url.isEmpty()) model.setImagen(url);

        if (docRef == null) {
            docRef = db.collection("tours").document();
            model.setId(docRef.getId());
        }

        b.btnSave.setEnabled(false);
        docRef.set(model)
                .addOnSuccessListener(unused -> {
                    Snackbar.make(b.getRoot(), "Tour guardado", Snackbar.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    b.btnSave.setEnabled(true);
                    Snackbar.make(b.getRoot(), "Error al guardar: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                });
    }

    private List<String> parseIds(String raw) {
        List<String> out = new ArrayList<>();
        if (TextUtils.isEmpty(raw)) return out;
        String[] parts = raw.split(",");
        for (String p : parts) {
            String s = p.trim();
            if (!s.isEmpty()) out.add(s);
        }
        return out;
    }

    private void showDateDialog(Calendar cal, OnPick cb) {
        new DatePickerDialog(
                this,
                (view, y, m, d) -> cb.onPick(y, m, d),
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private interface OnPick { void onPick(int y, int m, int d); }

    private static String nz(String s) { return s == null ? "" : s; }
    private static double safeDouble(String s, double def){
        try { return Double.parseDouble(s); } catch (Exception e){ return def; }
    }
    private static int safeInt(String s, int def){
        try { return Integer.parseInt(s); } catch (Exception e){ return def; }
    }
}
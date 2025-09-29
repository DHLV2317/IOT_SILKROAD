package com.example.silkroad_iot.ui.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.AdminRepository;
import com.example.silkroad_iot.data.AdminRepository.Tour;
import com.example.silkroad_iot.databinding.ActivityAdminTourDetailBinding;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdminTourDetailActivity extends AppCompatActivity {
    private ActivityAdminTourDetailBinding b;
    private final AdminRepository repo = AdminRepository.get();

    private int index = -1;      // -1 => crear
    private Tour model;          // objeto en edición (si aplica)

    private final Calendar cal = Calendar.getInstance();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        b = ActivityAdminTourDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // Toolbar
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        b.toolbar.setNavigationOnClickListener(v -> finish());

        // ¿Editar o Crear?
        index = getIntent().getIntExtra("index", -1);
        if (index >= 0) {
            model = repo.getTourAt(index);  // ✅ sin getTours().get(index)
        } else {
            model = null;
        }

        boolean editing = (model != null);
        setTitle(editing ? "Editar Tour" : "Nuevo Tour");
        b.btnDelete.setVisibility(editing ? android.view.View.VISIBLE : android.view.View.GONE);

        // Precarga si edita
        if (editing) {
            b.inputName.setText(nz(model.name));
            b.inputDesc.setText(nz(model.description));
            b.inputPrice.setText(String.valueOf(model.price));
            b.inputPeople.setText(String.valueOf(model.people));
            if (model.FechaTour != null) {
                cal.setTime(model.FechaTour);
                b.inputDate.setText(sdf.format(model.FechaTour));
            }
            if (!TextUtils.isEmpty(model.imageUrl)) {
                Glide.with(this).load(model.imageUrl)
                        .placeholder(R.drawable.ic_menu_24)
                        .error(R.drawable.ic_menu_24)
                        .into(b.img);
            } else {
                Glide.with(this).load(R.drawable.ic_menu_24).into(b.img);
            }
        } else {
            Glide.with(this).load(R.drawable.ic_menu_24).into(b.img);
        }

        // Selector de fecha
        b.inputDate.setOnClickListener(v -> {
            int y = cal.get(Calendar.YEAR);
            int m = cal.get(Calendar.MONTH);
            int d = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dp = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        b.inputDate.setText(sdf.format(cal.getTime()));
                    },
                    y, m, d
            );
            dp.show();
        });

        // Guardar
        b.btnSave.setOnClickListener(v -> {
            String name = b.inputName.getText().toString().trim();
            String desc = b.inputDesc.getText().toString().trim();
            String p    = b.inputPrice.getText().toString().trim();
            String ppl  = b.inputPeople.getText().toString().trim();

            if (TextUtils.isEmpty(name)) { b.inputName.setError("Requerido"); return; }
            if (TextUtils.isEmpty(p))    { b.inputPrice.setError("Requerido"); return; }
            if (TextUtils.isEmpty(ppl))  { b.inputPeople.setError("Requerido"); return; }

            double price = safeDouble(p, 0);
            int people   = safeInt(ppl, 1);
            Date fecha   = (b.inputDate.getText().length() > 0) ? cal.getTime() : null;

            if (model == null) {
                model = new Tour(name, price, people, desc, null, 0, fecha);
                repo.addTour(model);  // ✅ sin getTours().add(...)
            } else {
                model.name = name;
                model.description = desc;
                model.price = price;
                model.people = people;
                model.FechaTour = fecha;
            }

            Snackbar.make(b.getRoot(), "Tour guardado", Snackbar.LENGTH_SHORT).show();
            finish();
        });

        // Eliminar
        b.btnDelete.setOnClickListener(v -> {
            if (model != null) {
                repo.getTours().remove(model);  // eliminar directo de la lista
                Snackbar.make(b.getRoot(), "Tour eliminado", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private static String nz(String s){ return s == null ? "" : s; }
    private static double safeDouble(String s, double def){
        try { return Double.parseDouble(s); } catch (Exception e){ return def; }
    }
    private static int safeInt(String s, int def){
        try { return Integer.parseInt(s); } catch (Exception e){ return def; }
    }
}
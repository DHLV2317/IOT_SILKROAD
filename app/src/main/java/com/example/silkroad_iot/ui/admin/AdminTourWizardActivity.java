package com.example.silkroad_iot.ui.admin;

import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.TourFB;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AdminTourWizardActivity extends AppCompatActivity {

    // Firestore
    private FirebaseFirestore db;

    // Vistas comunes
    private View group1, group2, group3, group4;
    private MaterialButton btnPrev, btnNext;
    private MaterialToolbar toolbar;

    // Paso 1
    private TextInputEditText inName, inDesc, inDuration, inDate, inPrice, inPeople;
    private AutoCompleteTextView inLangs;
    private android.widget.ImageView img;

    // Paso 2
    private TextInputEditText inStopAddr, inStopMin;
    private LinearLayout boxStops;

    // Paso 3
    private TextInputEditText inSrvName, inSrvPrice;
    private LinearLayout boxServices;

    // Paso 4
    private LinearLayout boxGuides;
    private TextInputEditText inPayment;

    // Estado
    private int step = 1;
    private final Set<String> langsSel = new LinkedHashSet<>();
    private Long dateRangeStart = null, dateRangeEnd = null;
    private Uri pickedImage;

    // Datos reunidos
    private final List<String> stops = new ArrayList<>();
    private final List<String> services = new ArrayList<>();

    // Guías
    private static class GuideItem {
        String id;
        String name;
        String langs;
        String email;
    }
    private final List<GuideItem> guideItems = new ArrayList<>();
    private final List<String> selectedGuideIds = new ArrayList<>();
    private final List<String> selectedGuideNames = new ArrayList<>();

    // Modo edición
    private String editingDocId = null;
    private String defaultEmpresaId = null;

    // Picker de imagen
    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    uri -> { if (uri != null) { pickedImage = uri; Glide.with(this).load(uri).into(img); }});

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_admin_tour_wizard);

        db = FirebaseFirestore.getInstance();

        editingDocId = getIntent().getStringExtra("docId");
        defaultEmpresaId = getIntent().getStringExtra("empresaId");

        bindViews();
        setupStep1();
        setupStep2();
        setupStep3();
        setupStep4();
        updateUiForStep();

        btnPrev.setOnClickListener(v -> {
            if (step > 1) {
                step--;
                updateUiForStep();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (step < 4) {
                if (!validateStep(step)) return;
                step++;
                updateUiForStep();
            } else {
                if (!validateStep(4)) return;
                saveTourToFirestore();
            }
        });
    }

    /* ================== Bind ================== */
    private void bindViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        group1 = findViewById(R.id.groupStep1);
        group2 = findViewById(R.id.groupStep2);
        group3 = findViewById(R.id.groupStep3);
        group4 = findViewById(R.id.groupStep4);

        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);

        img = findViewById(R.id.img);
        inName = findViewById(R.id.inputName);
        inDesc = findViewById(R.id.inputDesc);
        inDuration = findViewById(R.id.inputDuration);
        inLangs = findViewById(R.id.inputLangs);
        inDate = findViewById(R.id.inputDate);
        inPrice = findViewById(R.id.inputPrice);
        inPeople = findViewById(R.id.inputPeople);

        inStopAddr = findViewById(R.id.inputStopAddress);
        inStopMin = findViewById(R.id.inputStopMinutes);
        boxStops = findViewById(R.id.boxStops);

        inSrvName = findViewById(R.id.inputServiceName);
        inSrvPrice = findViewById(R.id.inputServicePrice);
        boxServices = findViewById(R.id.boxServices);

        boxGuides = findViewById(R.id.boxGuides);
        inPayment = findViewById(R.id.inputPayment);
    }

    /* ========= Botón Outlined compatible M3/MDC ========= */
    private MaterialButton createOutlinedButton(String text) {
        int styleM3 = R.style.M3_OutlinedButton;
        int styleMDC = R.style.MDC_OutlinedButton;

        ContextThemeWrapper ctw;
        try {
            ctw = new ContextThemeWrapper(this, styleM3);
        } catch (Throwable ignore) {
            ctw = new ContextThemeWrapper(this, styleMDC);
        }

        MaterialButton b = new MaterialButton(ctw, null, 0);
        b.setText(text);
        return b;
    }

    /* ================== Paso 1 ================== */
    private void setupStep1() {
        img.setOnClickListener(v -> pickImage.launch("image/*"));

        String[] langs = {"Español", "Inglés", "Francés", "Alemán", "Portugués"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, langs);
        inLangs.setAdapter(adapter);
        inLangs.setInputType(InputType.TYPE_NULL);
        inLangs.setOnClickListener(v -> inLangs.showDropDown());
        inLangs.setOnFocusChangeListener((v, has) -> { if (has) inLangs.showDropDown(); });
        inLangs.setOnItemClickListener((p, v, pos, id) -> {
            String pick = langs[pos];
            if (langsSel.add(normalizeLang(pick))) {
                inLangs.setText(String.join("/", langsSel), false);
                inLangs.dismissDropDown();
            }
        });

        inDate.setOnClickListener(v -> {
            MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> b = MaterialDatePicker.Builder.dateRangePicker();
            MaterialDatePicker<androidx.core.util.Pair<Long, Long>> picker = b.build();
            picker.addOnPositiveButtonClickListener(pair -> {
                if (pair != null) {
                    dateRangeStart = pair.first;
                    dateRangeEnd = pair.second;
                    inDate.setText(fmtDate(pair.first) + " - " + fmtDate(pair.second));
                }
            });
            picker.show(getSupportFragmentManager(), "range");
        });
    }

    /* ================== Paso 2 ================== */
    private void setupStep2() {
        MaterialButton btnAddStop = findViewById(R.id.btnAddStop);
        btnAddStop.setOnClickListener(v -> {
            String addr = safeText(inStopAddr);
            String mins = safeText(inStopMin);
            if (addr.isEmpty()) { inStopAddr.setError("Requerido"); return; }
            if (mins.isEmpty()) { inStopMin.setError("Requerido"); return; }
            addStopRow(addr, mins);
            inStopAddr.setText(""); inStopMin.setText("");
        });
    }

    private void addStopRow(String addr, String mins) {
        String label = addr + " · " + mins + " min";
        stops.add(label);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dp(8), dp(8), dp(8), dp(8));

        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        row.addView(tv);

        MaterialButton rm = createOutlinedButton("Quitar");
        rm.setOnClickListener(v -> { boxStops.removeView(row); stops.remove(label); });
        row.addView(rm);

        boxStops.addView(row);
    }

    /* ================== Paso 3 ================== */
    private void setupStep3() {
        addServiceRow("Desayuno", "Incluido");
        addServiceRow("Almuerzo", "Incluido");
        addServiceRow("Cena", "Incluido");

        MaterialButton btnAddService = findViewById(R.id.btnAddService);
        btnAddService.setOnClickListener(v -> {
            String name = safeText(inSrvName);
            String price = safeText(inSrvPrice);
            if (name.isEmpty()) { inSrvName.setError("Requerido"); return; }
            if (price.isEmpty()) price = "0";
            addServiceRow(name, "S/ " + price);
            inSrvName.setText(""); inSrvPrice.setText("");
        });
    }

    private void addServiceRow(String name, String value) {
        String label = name + " - " + value;
        services.add(label);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dp(8), dp(8), dp(8), dp(8));

        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        row.addView(tv);

        MaterialButton rm = createOutlinedButton("Quitar");
        rm.setOnClickListener(v -> { boxServices.removeView(row); services.remove(label); });
        row.addView(rm);

        boxServices.addView(row);
    }

    /* ================== Paso 4 ================== */
    private void setupStep4() {
        loadGuidesFromFirestore();
    }

    private void loadGuidesFromFirestore() {
        boxGuides.removeAllViews();
        guideItems.clear();
        selectedGuideIds.clear();
        selectedGuideNames.clear();

        db.collection("guias")
                .whereEqualTo("aprobado", true)
                .whereEqualTo("activo", true)
                .get()
                .addOnSuccessListener(snap -> {
                    for (DocumentSnapshot d : snap) {
                        GuideItem gi = new GuideItem();
                        gi.id = d.getId();
                        String n = d.getString("nombres");
                        String a = d.getString("apellidos");
                        gi.name = ((n == null ? "" : n) + " " + (a == null ? "" : a)).trim();
                        if (gi.name.isEmpty()) gi.name = d.getString("nombre");
                        gi.langs = d.getString("idiomas");
                        gi.email = d.getString("email") != null ? d.getString("email") : d.getString("correo");
                        guideItems.add(gi);

                        CheckBox cb = new CheckBox(this);
                        String label = (gi.name == null ? "Guía" : gi.name)
                                + "  ·  " + (gi.langs == null ? "—" : gi.langs);
                        cb.setText(label);
                        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            if (isChecked) {
                                if (!selectedGuideIds.contains(gi.id)) selectedGuideIds.add(gi.id);
                                if (!selectedGuideNames.contains(gi.name)) selectedGuideNames.add(gi.name);
                            } else {
                                selectedGuideIds.remove(gi.id);
                                selectedGuideNames.remove(gi.name);
                            }
                            updateLangsFromSelectedGuides();
                        });
                        boxGuides.addView(cb);
                    }
                });
    }

    /** Actualiza idiomas a partir de los guías seleccionados. */
    private void updateLangsFromSelectedGuides() {
        langsSel.clear();
        for (GuideItem gi : guideItems) {
            if (selectedGuideIds.contains(gi.id) && gi.langs != null) {
                for (String token : splitLangs(gi.langs)) {
                    if (!TextUtils.isEmpty(token)) langsSel.add(normalizeLang(token));
                }
            }
        }
        if (!langsSel.isEmpty()) inLangs.setText(String.join("/", langsSel), false);
    }

    /* ================== Guardado Firestore ================== */
    private void saveTourToFirestore() {
        String nombre = safeText(inName);
        String desc = safeText(inDesc);
        String duration = safeText(inDuration);

        String langsFromGuides = (langsSel.isEmpty() ? "" : String.join("/", langsSel));
        String langsManual = (inLangs.getText() == null) ? "" : inLangs.getText().toString().trim();
        String langsFinal = !TextUtils.isEmpty(langsFromGuides) ? langsFromGuides : langsManual;

        double precio = parseDouble(safeText(inPrice), 0);
        int cantidad = parseInt(safeText(inPeople), 1);
        Double payProp = parseDouble(safeText(inPayment), 0);

        String imagen = (pickedImage != null) ? pickedImage.toString() : null;
        String empresaId = (defaultEmpresaId != null) ? defaultEmpresaId : "1";
        String id_paradas = stops.isEmpty() ? "" : TextUtils.join(" | ", stops);

        StringBuilder full = new StringBuilder();
        if (!desc.isEmpty()) full.append(desc).append("\n");
        if (!duration.isEmpty()) full.append("Duración: ").append(duration).append("\n");
        if (!langsFinal.isEmpty()) full.append("Idiomas: ").append(langsFinal).append("\n");
        if (!services.isEmpty()) {
            full.append("Servicios:\n");
            for (String s : services) full.append("• ").append(s).append("\n");
        }

        String assignedGuideName = selectedGuideNames.isEmpty() ? null : selectedGuideNames.get(0);
        String assignedGuideId = selectedGuideIds.isEmpty() ? null : selectedGuideIds.get(0);

        // POJO principal
        TourFB t = new TourFB();
        t.setNombre(nombre);
        t.setImagen(imagen);
        t.setPrecio(precio);
        t.setCantidad_personas(cantidad);
        t.setId_paradas(Collections.singletonList(id_paradas));
        t.setEmpresaId(empresaId);
        t.setLangs(langsFinal);
        t.setDuration(duration);
        t.setAssignedGuideName(assignedGuideName);
        t.setPaymentProposal(payProp);
        if (t.getImagen() == null) {
            t.setImagen("https://llerena.org/wp-content/uploads/2017/11/imagen-no-disponible-1.jpg");
        }

        // Extras
        java.util.Map<String, Object> extra = new java.util.HashMap<>();
        if (full.length() > 0) extra.put("description", full.toString());
        if (dateRangeStart != null) extra.put("dateFrom", new Date(dateRangeStart));
        if (dateRangeEnd != null) extra.put("dateTo", new Date(dateRangeEnd));
        if (!selectedGuideIds.isEmpty()) extra.put("invitedGuideIds", new ArrayList<>(selectedGuideIds));
        if (!selectedGuideNames.isEmpty()) extra.put("invitedGuideNames", new ArrayList<>(selectedGuideNames));
        if (assignedGuideId != null) extra.put("assignedGuideId", assignedGuideId);
        if (!TextUtils.isEmpty(langsFinal)) extra.put("idiomas_array", new ArrayList<>(splitLangs(langsFinal)));

        if (editingDocId == null) {
            db.collection("tours")
                    .add(t)
                    .addOnSuccessListener(ref -> {
                        if (!extra.isEmpty()) {
                            ref.set(extra, com.google.firebase.firestore.SetOptions.merge())
                                    .addOnSuccessListener(unused -> finish())
                                    .addOnFailureListener(e -> showToast("Error guardando extras: " + e.getMessage()));
                        } else {
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> showToast("Error al publicar: " + e.getMessage()));
        } else {
            DocumentReference ref = db.collection("tours").document(editingDocId);
            ref.set(t, com.google.firebase.firestore.SetOptions.merge())
                    .addOnSuccessListener(unused -> {
                        if (!extra.isEmpty()) {
                            ref.set(extra, com.google.firebase.firestore.SetOptions.merge())
                                    .addOnSuccessListener(u2 -> finish())
                                    .addOnFailureListener(e -> showToast("Error guardando extras: " + e.getMessage()));
                        } else {
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> showToast("Error al guardar cambios: " + e.getMessage()));
        }
    }

    /* ================== Helpers ================== */
    private List<String> splitLangs(String raw) { return Arrays.asList(raw.split("[/,;]")); }

    private String normalizeLang(String s) {
        s = s.trim();
        if (s.equalsIgnoreCase("es") || s.equalsIgnoreCase("español")) return "Español";
        if (s.equalsIgnoreCase("en") || s.equalsIgnoreCase("ingles") || s.equalsIgnoreCase("inglés")) return "Inglés";
        if (s.equalsIgnoreCase("fr") || s.equalsIgnoreCase("frances") || s.equalsIgnoreCase("francés")) return "Francés";
        if (s.equalsIgnoreCase("de") || s.equalsIgnoreCase("alemán") || s.equalsIgnoreCase("aleman")) return "Alemán";
        if (s.equalsIgnoreCase("pt") || s.equalsIgnoreCase("portugues") || s.equalsIgnoreCase("portugués")) return "Portugués";
        if (s.length() > 1)
            return s.substring(0,1).toUpperCase(Locale.getDefault()) + s.substring(1).toLowerCase(Locale.getDefault());
        return s.toUpperCase(Locale.getDefault());
    }

    private boolean validateStep(int s) {
        if (s == 1) {
            if (isEmpty(inName))  { inName.setError("Requerido"); return false; }
            if (isEmpty(inPrice)) { inPrice.setError("Requerido"); return false; }
            if (isEmpty(inPeople)){ inPeople.setError("Requerido"); return false; }
        }
        return true;
    }

    private void updateUiForStep() {
        setTitle((editingDocId == null ? "Nuevo Tour" : "Editar Tour") + " (" + step + "/4)");

        group1.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        group2.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        group3.setVisibility(step == 3 ? View.VISIBLE : View.GONE);
        group4.setVisibility(step == 4 ? View.VISIBLE : View.GONE);

        btnPrev.setEnabled(step > 1);
        btnNext.setText(step == 4
                ? (editingDocId == null ? "Publicar Tour" : "Guardar cambios")
                : "Siguiente  ➜");
    }

    private String fmtDate(long millis){
        SimpleDateFormat sdfUi = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdfUi.format(new Date(millis));
    }

    private String safeText(EditText e) {
        return e.getText() == null ? "" : e.getText().toString().trim();
    }

    private boolean isEmpty(EditText e) { return TextUtils.isEmpty(safeText(e)); }

    private int dp(int v) { return Math.round(getResources().getDisplayMetrics().density * v); }

    private double parseDouble(String s, double def){
        try { return Double.parseDouble(s); } catch(Exception e){ return def; }
    }

    private int parseInt(String s, int def){
        try { return Integer.parseInt(s); } catch(Exception e){ return def; }
    }

    private void showToast(String msg){
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_LONG).show();
    }
}
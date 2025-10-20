package com.example.silkroad_iot.ui.admin;

import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.AdminRepository;
import com.example.silkroad_iot.data.TourFB;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AdminTourWizardActivity extends AppCompatActivity {

    // ===== Repos/mock para poblar guías visibles en Step4 (no se persisten desde aquí)
    private final AdminRepository repo = AdminRepository.get();

    // ===== Firestore
    private FirebaseFirestore db;

    // ===== Views comunes
    private View group1, group2, group3, group4;
    private Button btnPrev, btnNext;
    private MaterialToolbar toolbar;

    // Step 1
    private TextInputEditText inName, inDesc, inDuration, inDate, inPrice, inPeople;
    private AutoCompleteTextView inLangs;        // <<--- IMPORTANTE: ESTE ES EL inLangs
    private android.widget.ImageView img;

    // Step 2
    private TextInputEditText inStopAddr, inStopMin;
    private LinearLayout boxStops;

    // Step 3
    private TextInputEditText inSrvName, inSrvPrice;
    private LinearLayout boxServices;

    // Step 4
    private LinearLayout boxGuides;
    private TextInputEditText inPayment;

    // Estado
    private int step = 1;
    private final Set<String> langsSel = new LinkedHashSet<>();
    private Long dateRangeStart = null, dateRangeEnd = null;
    private Uri pickedImage;

    // Datos “coleccionados” para serializar en TourFB
    private final List<String> stops = new ArrayList<>();       // se guardan concatenados en id_paradas
    private final List<String> services = new ArrayList<>();    // guardados en description extendida
    private final List<String> invitedGuideNames = new ArrayList<>(); // solo para mostrar/guardar assignedGuideName

    // Modo edición Firestore
    private String editingDocId = null;        // si viene por Intent, hacemos update()
    private String defaultEmpresaId = null;    // si quieres atar a una empresa: putExtra("empresaId", "...")

    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    uri -> { if (uri != null) { pickedImage = uri; Glide.with(this).load(uri).into(img); }});

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_admin_tour_wizard);

        db = FirebaseFirestore.getInstance();

        // Lee extras (opcional)
        editingDocId = getIntent().getStringExtra("docId");
        defaultEmpresaId = getIntent().getStringExtra("empresaId");

        bindViews();
        setupStep1();
        setupStep2();
        setupStep3();
        setupStep4();

        updateUiForStep();

        btnPrev.setOnClickListener(v -> { if (step > 1) { step--; updateUiForStep(); } });
        btnNext.setOnClickListener(v -> {
            if (step < 4) {
                if (!validateStep(step)) return;
                step++; updateUiForStep();
            } else {
                if (!validateStep(4)) return;
                saveTourToFirestore();
            }
        });

        // Si estás en edición (docId), podrías precargar aquí (opcional):
        // preloadFromFirestore(editingDocId);
    }

    private void bindViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        group1 = findViewById(R.id.groupStep1);
        group2 = findViewById(R.id.groupStep2);
        group3 = findViewById(R.id.groupStep3);
        group4 = findViewById(R.id.groupStep4);

        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);

        img        = findViewById(R.id.img);
        inName     = findViewById(R.id.inputName);
        inDesc     = findViewById(R.id.inputDesc);
        inDuration = findViewById(R.id.inputDuration);
        inLangs    = findViewById(R.id.inputLangs);  // <<--- AQUÍ SE RESUELVE EL ROJO (AutoCompleteTextView)
        inDate     = findViewById(R.id.inputDate);
        inPrice    = findViewById(R.id.inputPrice);
        inPeople   = findViewById(R.id.inputPeople);

        inStopAddr = findViewById(R.id.inputStopAddress);
        inStopMin  = findViewById(R.id.inputStopMinutes);
        boxStops   = findViewById(R.id.boxStops);

        inSrvName  = findViewById(R.id.inputServiceName);
        inSrvPrice = findViewById(R.id.inputServicePrice);
        boxServices= findViewById(R.id.boxServices);

        boxGuides  = findViewById(R.id.boxGuides);
        inPayment  = findViewById(R.id.inputPayment);
    }

    // ===== STEP 1 =====
    private void setupStep1() {
        // Imagen
        img.setOnClickListener(v -> pickImage.launch("image/*"));

        // Idiomas (dropdown múltiple acumulando en langsSel)
        String[] langs = {"Español", "Inglés", "Francés", "Alemán", "Portugués"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, langs);
        inLangs.setAdapter(adapter);
        inLangs.setInputType(InputType.TYPE_NULL);
        inLangs.setOnClickListener(v -> inLangs.showDropDown());
        inLangs.setOnFocusChangeListener((v, has) -> { if (has) inLangs.showDropDown(); });
        inLangs.setOnItemClickListener((p, v, pos, id) -> {
            String pick = langs[pos];
            if (langsSel.add(pick)) {
                inLangs.setText(String.join("/", langsSel), false); // formato "ES/EN"
                inLangs.dismissDropDown();
            }
        });

        // Rango de fechas (opcional: lo usamos para vista; TourFB no lo necesita estrictamente)
        inDate.setOnClickListener(v -> {
            MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> b =
                    MaterialDatePicker.Builder.dateRangePicker();
            MaterialDatePicker<androidx.core.util.Pair<Long, Long>> picker = b.build();
            picker.addOnPositiveButtonClickListener(pair -> {
                if (pair != null) {
                    dateRangeStart = pair.first;
                    dateRangeEnd   = pair.second;
                    inDate.setText(fmtDate(pair.first) + " - " + fmtDate(pair.second));
                }
            });
            picker.show(getSupportFragmentManager(), "range");
        });
    }

    // ===== STEP 2 =====
    private void setupStep2() {
        Button btnAddStop = findViewById(R.id.btnAddStop);
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

        Button rm = new Button(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        rm.setText("Quitar");
        rm.setOnClickListener(v -> { boxStops.removeView(row); stops.remove(label); });
        row.addView(rm);

        boxStops.addView(row);
    }

    // ===== STEP 3 =====
    private void setupStep3() {
        // Placeholders iniciales (puedes quitarlos)
        addServiceRow("Desayuno", "Incluido");
        addServiceRow("Almuerzo", "Incluido");
        addServiceRow("Cena", "Incluido");

        Button btnAddService = findViewById(R.id.btnAddService);
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

        Button rm = new Button(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        rm.setText("Quitar");
        rm.setOnClickListener(v -> { boxServices.removeView(row); services.remove(label); });
        row.addView(rm);

        boxServices.addView(row);
    }

    // ===== STEP 4 =====
    private void setupStep4() {
        invitedGuideNames.clear();
        // Mostramos los guías “disponibles” del repo local (solo nombres)
        for (AdminRepository.Guide g : repo.getGuides()) {
            CheckBox cb = new CheckBox(this);
            String label = (g.name == null ? "Guía" : g.name) + "  ·  " + (g.langs == null ? "—" : g.langs);
            cb.setText(label);
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!invitedGuideNames.contains(g.name)) invitedGuideNames.add(g.name);
                } else {
                    invitedGuideNames.remove(g.name);
                }
            });
            boxGuides.addView(cb);
        }
    }

    private boolean validateStep(int s) {
        if (s == 1) {
            if (isEmpty(inName))  { inName.setError("Requerido"); return false; }
            if (isEmpty(inPrice)) { inPrice.setError("Requerido"); return false; }
            if (isEmpty(inPeople)){ inPeople.setError("Requerido"); return false; }
            if (langsSel.isEmpty() && TextUtils.isEmpty(inLangs.getText())) {
                inLangs.setError("Selecciona idiomas"); return false;
            }
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
        btnNext.setText(step == 4 ? (editingDocId == null ? "Publicar Tour" : "Guardar cambios") : "Siguiente  ➜");
    }

    /* =========================================================
       ==============  PERSISTENCIA EN FIRESTORE  ==============
       ========================================================= */
    private void saveTourToFirestore() {
        // Campos base
        String nombre   = safeText(inName);
        String desc     = safeText(inDesc);
        String duration = safeText(inDuration);
        String langs    = (inLangs.getText() == null) ? "" : inLangs.getText().toString().trim();
        if (langs.isEmpty() && !langsSel.isEmpty()) langs = String.join("/", langsSel);

        double precio   = parseDouble(safeText(inPrice), 0);
        int cantidad    = parseInt(safeText(inPeople), 1);
        Double payProp  = parseDouble(safeText(inPayment), 0);

        // “imagen”: solo guardamos el URI string si se escogió
        String imagen = (pickedImage != null) ? pickedImage.toString() : null;

        // Empresa (si te llega por Intent). Si no, null.
        String empresaId = defaultEmpresaId;

        // Paradas: las guardamos concatenadas en id_paradas (para cumplir con tu esquema original)
        String id_paradas = stops.isEmpty() ? "" : TextUtils.join(" | ", stops);

        // description extendida (metemos services + desc + duration + langs)
        StringBuilder full = new StringBuilder();
        if (!desc.isEmpty()) full.append(desc).append("\n");
        if (!duration.isEmpty()) full.append("Duración: ").append(duration).append("\n");
        if (!langs.isEmpty()) full.append("Idiomas: ").append(langs).append("\n");
        if (!services.isEmpty()) { full.append("Servicios:\n"); for (String s : services) full.append("• ").append(s).append("\n"); }

        // guide asignado (si seleccionaste alguno, tomamos el primero como ejemplo)
        String assignedGuideName = invitedGuideNames.isEmpty() ? null : invitedGuideNames.get(0);

        // Mapeo a TourFB
        TourFB t = new TourFB();
        t.setNombre(nombre);
        t.setImagen(imagen);
        t.setPrecio(precio);
        t.setCantidad_personas(cantidad);
        t.setId_paradas(id_paradas);
        t.setEmpresaId(empresaId);

        // NUEVO en tu modelo
        t.setLangs(langs);
        t.setDuration(duration);
        t.setAssignedGuideName(assignedGuideName);
        t.setPaymentProposal(payProp);

        // Nota: TourFB no tiene "description" como tal; si lo necesitas en Firestore, puedes guardarlo
        // en un campo adicional “description” junto al documento:
        // haremos un Map para enviar extra junto a los @PropertyName
        java.util.Map<String, Object> extra = new java.util.HashMap<>();
        if (full.length() > 0) extra.put("description", full.toString());

        // Fecha: no está en TourFB. Si quieres guardar una fecha “start”, añadimos un campo suelto:
        if (dateRangeStart != null) extra.put("dateFrom", new Date(dateRangeStart));
        if (dateRangeEnd   != null) extra.put("dateTo",   new Date(dateRangeEnd));

        // Persistencia: create o update
        if (editingDocId == null) {
            // CREATE
            db.collection("tours")
                    .add(t) // primero el POJO con @PropertyName
                    .addOnSuccessListener(ref -> {
                        // si hay extras, las mergeamos
                        if (!extra.isEmpty()) {
                            ref.set(extra, com.google.firebase.firestore.SetOptions.merge())
                                    .addOnSuccessListener(unused -> finish())
                                    .addOnFailureListener(e -> { showToast("Error guardando extras: " + e.getMessage()); finish(); });
                        } else {
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> showToast("Error al publicar: " + e.getMessage()));
        } else {
            // UPDATE
            DocumentReference ref = db.collection("tours").document(editingDocId);
            ref.set(t, com.google.firebase.firestore.SetOptions.merge())
                    .addOnSuccessListener(unused -> {
                        if (!extra.isEmpty()) {
                            ref.set(extra, com.google.firebase.firestore.SetOptions.merge())
                                    .addOnSuccessListener(u2 -> finish())
                                    .addOnFailureListener(e -> { showToast("Error guardando extras: " + e.getMessage()); finish(); });
                        } else {
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> showToast("Error al guardar cambios: " + e.getMessage()));
        }
    }

    /* =========================================================
       ====================== HELPERS UI =======================
       ========================================================= */
    private String fmtDate(long millis){
        java.text.SimpleDateFormat sdfUi = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdfUi.format(new Date(millis));
    }
    private String safeText(EditText e) { return e.getText() == null ? "" : e.getText().toString().trim(); }
    private boolean isEmpty(EditText e) { return TextUtils.isEmpty(safeText(e)); }
    private int dp(int v) { return Math.round(getResources().getDisplayMetrics().density * v); }
    private double parseDouble(String s, double def){ try { return Double.parseDouble(s);} catch(Exception e){ return def; } }
    private int parseInt(String s, int def){ try { return Integer.parseInt(s);} catch(Exception e){ return def; } }

    private void showToast(String msg){
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_LONG).show();
    }
}
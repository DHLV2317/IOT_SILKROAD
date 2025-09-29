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
import com.example.silkroad_iot.data.AdminRepository.Tour;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AdminTourWizardActivity extends AppCompatActivity {

    private final AdminRepository repo = AdminRepository.get();

    private View group1, group2, group3, group4;
    private Button btnPrev, btnNext;
    private MaterialToolbar toolbar;

    private TextInputEditText inName, inDesc, inDuration, inDate, inPrice, inPeople;
    private AutoCompleteTextView inLangs;
    private android.widget.ImageView img;

    private TextInputEditText inStopAddr, inStopMin;
    private LinearLayout boxStops;

    private TextInputEditText inSrvName, inSrvPrice;
    private LinearLayout boxServices;

    private LinearLayout boxGuides;
    private TextInputEditText inPayment;

    private int step = 1;
    private final Set<String> langsSel = new LinkedHashSet<>();
    private Long dateRangeStart = null, dateRangeEnd = null;
    private Uri pickedImage;

    private final List<String> stops = new ArrayList<>();
    private final List<String> services = new ArrayList<>();
    private final List<String> invitedGuideIds = new ArrayList<>();

    private final SimpleDateFormat sdfUi = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private int editIndex = -1;
    private Tour editingTour = null;

    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) { pickedImage = uri; Glide.with(this).load(uri).into(img); }
            });

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_admin_tour_wizard);

        bindViews();
        setupStep1();
        setupStep2();
        setupStep3();
        setupStep4();

        // ¿Modo edición?
        editIndex = getIntent().getIntExtra("editIndex", -1);
        if (editIndex >= 0) {
            editingTour = repo.getTourAt(editIndex);
            if (editingTour != null) prefillFromTour(editingTour);
        }

        updateUiForStep();

        btnPrev.setOnClickListener(v -> { if (step > 1) { step--; updateUiForStep(); } });
        btnNext.setOnClickListener(v -> {
            if (step < 4) {
                if (!validateStep(step)) return;
                step++; updateUiForStep();
            } else {
                if (!validateStep(4)) return;
                publishTour();
            }
        });
    }

    private void bindViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        inLangs    = findViewById(R.id.inputLangs);
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
        img.setOnClickListener(v -> pickImage.launch("image/*"));

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
                inLangs.setText(String.join("/", langsSel), false);
                inLangs.dismissDropDown();
            }
        });

        inDate.setOnClickListener(v -> {
            MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> b =
                    MaterialDatePicker.Builder.dateRangePicker();
            MaterialDatePicker<androidx.core.util.Pair<Long, Long>> picker = b.build();
            picker.addOnPositiveButtonClickListener(pair -> {
                if (pair != null) {
                    dateRangeStart = pair.first;
                    dateRangeEnd = pair.second;
                    inDate.setText(sdfUi.format(pair.first) + " - " + sdfUi.format(pair.second));
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
            addStopRow(addr, mins); inStopAddr.setText(""); inStopMin.setText("");
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
        for (AdminRepository.Guide g : repo.getGuides()) {
            CheckBox cb = new CheckBox(this);
            cb.setText(g.name + "  ·  " + g.langs);
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) { if (!invitedGuideIds.contains(g.id)) invitedGuideIds.add(g.id); }
                else { invitedGuideIds.remove(g.id); }
            });
            boxGuides.addView(cb);
        }
    }

    // ===== Precarga en edición =====
    private void prefillFromTour(Tour t) {
        if (!TextUtils.isEmpty(t.imageUrl)) {
            Glide.with(this).load(t.imageUrl).placeholder(R.drawable.ic_menu_24).into(img);
            try { pickedImage = Uri.parse(t.imageUrl); } catch (Exception ignore) {}
        } else {
            Glide.with(this).load(R.drawable.ic_menu_24).into(img);
        }

        inName.setText(nz(t.name));
        inDesc.setText(nz(t.description));
        inPrice.setText(String.valueOf(t.price));
        inPeople.setText(String.valueOf(t.people));
        inDuration.setText(nz(t.duration));
        inLangs.setText(nz(t.langs), false);

        if (t.FechaTour != null) {
            dateRangeStart = t.FechaTour.getTime();
            dateRangeEnd   = t.FechaTour.getTime();
            inDate.setText(sdfUi.format(t.FechaTour));
        }
    }

    private boolean validateStep(int s) {
        if (s == 1) {
            if (isEmpty(inName))  { inName.setError("Requerido"); return false; }
            if (isEmpty(inPrice)) { inPrice.setError("Requerido"); return false; }
            if (isEmpty(inPeople)){ inPeople.setError("Requerido"); return false; }
            if (dateRangeStart == null || dateRangeEnd == null) {
                inDate.setError("Selecciona un rango"); return false;
            }
        }
        return true;
    }

    private void updateUiForStep() {
        String prefix = (editingTour != null) ? "Editar Tour" : "Nuevo Tour";
        setTitle(prefix + " (" + step + "/4)");
        group1.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        group2.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        group3.setVisibility(step == 3 ? View.VISIBLE : View.GONE);
        group4.setVisibility(step == 4 ? View.VISIBLE : View.GONE);

        btnPrev.setEnabled(step > 1);
        btnNext.setText(step == 4 ? ((editingTour != null) ? "Guardar cambios" : "Publicar Tour")
                : "Siguiente  ➜");
    }

    private void publishTour() {
        String name = safeText(inName);
        String desc = safeText(inDesc);
        String duration = safeText(inDuration);
        String langs = inLangs.getText() == null ? "" : inLangs.getText().toString().trim();
        double price = parseDouble(safeText(inPrice), 0);
        int people = parseInt(safeText(inPeople), 1);

        StringBuilder full = new StringBuilder();
        if (!desc.isEmpty()) full.append(desc).append("\n");
        if (!duration.isEmpty()) full.append("Duración: ").append(duration).append("\n");
        if (!langs.isEmpty()) full.append("Idiomas: ").append(langs).append("\n");
        if (!stops.isEmpty()) { full.append("Paradas:\n"); for (String s : stops) full.append("• ").append(s).append("\n"); }
        if (!services.isEmpty()) { full.append("Servicios:\n"); for (String s : services) full.append("• ").append(s).append("\n"); }
        if (!invitedGuideIds.isEmpty()) full.append("Guías invitados: ").append(invitedGuideIds.size()).append("\n");

        Date start = new Date(dateRangeStart);
        String image = pickedImage != null ? pickedImage.toString() : (editingTour != null ? editingTour.imageUrl : null);

        if (editingTour == null) {
            Tour t = new Tour(name, price, people, full.toString(), image, 0, start);
            // set extra visibles por reflexión si quieres guardarlos
            t.duration = duration;
            t.langs = langs;
            repo.addTour(t);
        } else {
            editingTour.name = name;
            editingTour.description = full.toString();
            editingTour.price = price;
            editingTour.people = people;
            editingTour.imageUrl = image;
            editingTour.FechaTour = start;
            editingTour.duration = duration;
            editingTour.langs = langs;
        }
        finish();
    }

    // Helpers
    private String safeText(EditText e) { return e.getText() == null ? "" : e.getText().toString().trim(); }
    private boolean isEmpty(EditText e) { return TextUtils.isEmpty(safeText(e)); }
    private int dp(int v) { return Math.round(getResources().getDisplayMetrics().density * v); }
    private double parseDouble(String s, double def){ try { return Double.parseDouble(s);} catch(Exception e){ return def; } }
    private int parseInt(String s, int def){ try { return Integer.parseInt(s);} catch(Exception e){ return def; } }
    private static String nz(String s){ return s==null? "" : s; }
}
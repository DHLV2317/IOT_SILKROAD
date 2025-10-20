package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.databinding.ActivitySuperadminDetallesGuiaBinding;
import com.example.silkroad_iot.ui.superadmin.entity.Guia;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetallesGuiaActivity extends AppCompatActivity {

    private ActivitySuperadminDetallesGuiaBinding binding;
    private FirebaseFirestore db;
    private String correoDoc;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminDetallesGuiaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        Guia guia = (Guia) intent.getSerializableExtra("guia");
        if (guia == null) {
            Toast.makeText(this, "Error: guía no encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        correoDoc = guia.getCorreo();

        // Cargar datos en campos
        binding.inputName.setText(guia.getNombres());
        binding.inputLastName.setText(guia.getApellidos());
        binding.inputDocumentType.setText(guia.getTipoDocumento());
        binding.inputDocumentNumber.setText(guia.getNumeroDocumento());

        // ✅ Formatear Date a String
        String fechaNac = (guia.getFechaNacimiento() != null) ? SDF.format(guia.getFechaNacimiento()) : "";
        binding.inputBirthDate.setText(fechaNac);

        binding.inputEmail.setText(guia.getCorreo());
        binding.inputPhone.setText(guia.getTelefono());
        binding.inputAddress.setText(guia.getDomicilio());
        binding.inputLanguages.setText(guia.getIdiomas());

        // ⛳ Tu layout no necesita un "estadoAprobacion" separado;
        //    si quieres mostrarlo, muéstralo como texto derivado de boolean:
        //    (solo si tienes un TextInput para esto)
        // binding.inputGuideApprovalStatus.setText(guia.isAprobado() ? "APPROVED" : "PENDING");

        binding.inputPassword.setText(guia.getContrasena());

        updateEstadoUI(guia.isActivo());

        binding.en.setOnClickListener(v -> setActivo(true));
        binding.di.setOnClickListener(v -> setActivo(false));
        binding.button.setOnClickListener(this::guardar);
    }

    private void updateEstadoUI(boolean activo) {
        int verde = ContextCompat.getColor(this, R.color.brand_verde);
        int rojo  = ContextCompat.getColor(this, R.color.red);
        int base  = ContextCompat.getColor(this, R.color.brand_celeste);

        if (activo) {
            binding.en.setBackgroundColor(verde);
            binding.di.setBackgroundColor(base);
        } else {
            binding.en.setBackgroundColor(base);
            binding.di.setBackgroundColor(rojo);
        }
    }

    private void setActivo(boolean activo) {
        db.collection("guias").document(correoDoc)
                .update("activo", activo)
                .addOnSuccessListener(v -> {
                    updateEstadoUI(activo);
                    Toast.makeText(this, "Estado actualizado correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void guardar(View view) {
        String nombres         = binding.inputName.getText().toString().trim();
        String apellidos       = binding.inputLastName.getText().toString().trim();
        String tipoDocumento   = binding.inputDocumentType.getText().toString().trim();
        String numeroDocumento = binding.inputDocumentNumber.getText().toString().trim();
        String fechaNacimiento = binding.inputBirthDate.getText().toString().trim(); // guardamos como string yyyy-MM-dd
        String correo          = binding.inputEmail.getText().toString().trim();
        String telefono        = binding.inputPhone.getText().toString().trim();
        String domicilio       = binding.inputAddress.getText().toString().trim();
        String idiomas         = binding.inputLanguages.getText().toString().trim();
        String contrasena      = binding.inputPassword.getText().toString().trim();

        if (nombres.isEmpty() || apellidos.isEmpty() || tipoDocumento.isEmpty() || numeroDocumento.isEmpty()
                || correo.isEmpty() || telefono.isEmpty() || domicilio.isEmpty() || idiomas.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> update = new HashMap<>();
        update.put("nombres", nombres);
        update.put("apellidos", apellidos);
        update.put("tipoDocumento", tipoDocumento);
        update.put("numeroDocumento", numeroDocumento);
        update.put("fechaNacimiento", fechaNacimiento); // ✅ string (o cámbialo a Timestamp si prefieres)
        update.put("correo", correo);
        update.put("telefono", telefono);
        update.put("domicilio", domicilio);
        update.put("idiomas", idiomas);
        update.put("contrasena", contrasena);

        boolean correoCambio = !correo.equals(correoDoc);
        if (correoCambio) {
            db.collection("guias").document(correoDoc).delete()
                    .addOnSuccessListener(v1 -> db.collection("guias").document(correo).set(update)
                            .addOnSuccessListener(v2 -> { Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show(); finish(); })
                            .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()))
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            db.collection("guias").document(correoDoc).update(update)
                    .addOnSuccessListener(v2 -> { Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show(); finish(); })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}
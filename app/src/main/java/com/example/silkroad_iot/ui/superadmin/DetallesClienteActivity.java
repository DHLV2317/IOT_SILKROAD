package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.databinding.ActivitySuperadminDetallesClienteBinding;
import com.example.silkroad_iot.ui.superadmin.entity.Cliente;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetallesClienteActivity extends AppCompatActivity {

    private ActivitySuperadminDetallesClienteBinding binding;
    private FirebaseFirestore db;
    private String correoDoc;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminDetallesClienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
<<<<<<< Updated upstream

        posicion = intent.getIntExtra("posicion", -1);
        Cliente cliente= (Cliente) intent.getSerializableExtra("cliente");

        binding.textInputLayout.getEditText().setText(cliente.getNombres());
        binding.textInputLayout2.getEditText().setText(cliente.getApellidos());
        binding.textInputLayout3.getEditText().setText(cliente.getTipoDocumento());
        binding.textInputLayout4.getEditText().setText(cliente.getNumeroDocumento());
        binding.textInputLayout5.getEditText().setText(cliente.getFechaNacimiento().toString());
        binding.textInputLayout6.getEditText().setText(cliente.getCorreo());
        binding.textInputLayout7.getEditText().setText(cliente.getTelefono());
        binding.textInputLayout8.getEditText().setText(cliente.getDomicilio());
        binding.textInputLayout10.getEditText().setText(cliente.getContrasena());
        binding.textInputLayout11.getEditText().setText(cliente.getContrasena());

        if(cliente.isActivo()){
            binding.en.setBackgroundColor(getResources().getColor(R.color.green, null));
            binding.di.setBackgroundColor(getResources().getColor(R.color.base, null));
        }else{
            binding.en.setBackgroundColor(getResources().getColor(R.color.base, null));
            binding.di.setBackgroundColor(getResources().getColor(R.color.red, null));
=======
        Cliente cliente = (Cliente) intent.getSerializableExtra("cliente");
        if (cliente == null) {
            Toast.makeText(this, "Error: cliente no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
>>>>>>> Stashed changes
        }

        correoDoc = cliente.getCorreo();

        // Llenar campos
        binding.inputNames.setText(cliente.getNombres());
        binding.inputLastNames.setText(cliente.getApellidos());
        binding.inputDocType.setText(cliente.getTipoDocumento());
        binding.inputDocNumber.setText(cliente.getNumeroDocumento());

        // ✅ Formatear Date a String
        String fechaNac = (cliente.getFechaNacimiento() != null) ? SDF.format(cliente.getFechaNacimiento()) : "";
        binding.inputBirthDate.setText(fechaNac);

        binding.inputEmail.setText(cliente.getCorreo());
        binding.inputPhone.setText(cliente.getTelefono());
        binding.inputAddress.setText(cliente.getDomicilio());
        binding.inputPassword.setText(cliente.getContrasena());
        binding.inputPasswordRepeat.setText(cliente.getContrasena());

        updateEstadoUI(cliente.isActivo());

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
        db.collection("clientes").document(correoDoc)
                .update("activo", activo)
                .addOnSuccessListener(v -> {
                    updateEstadoUI(activo);
                    Toast.makeText(this, "Estado actualizado correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void guardar(View view) {
        String nombres         = binding.inputNames.getText().toString().trim();
        String apellidos       = binding.inputLastNames.getText().toString().trim();
        String tipoDocumento   = binding.inputDocType.getText().toString().trim();
        String numeroDocumento = binding.inputDocNumber.getText().toString().trim();
        String fechaNacimiento = binding.inputBirthDate.getText().toString().trim(); // guardado como string
        String correo          = binding.inputEmail.getText().toString().trim();
        String telefono        = binding.inputPhone.getText().toString().trim();
        String domicilio       = binding.inputAddress.getText().toString().trim();
        String contrasena      = binding.inputPassword.getText().toString().trim();
        String contrasenaRep   = binding.inputPasswordRepeat.getText().toString().trim();

        if (nombres.isEmpty() || apellidos.isEmpty() || tipoDocumento.isEmpty() || numeroDocumento.isEmpty()
                || correo.isEmpty() || telefono.isEmpty() || domicilio.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!contrasena.equals(contrasenaRep)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> update = new HashMap<>();
        update.put("nombres", nombres);
        update.put("apellidos", apellidos);
        update.put("tipoDocumento", tipoDocumento);
        update.put("numeroDocumento", numeroDocumento);
        update.put("fechaNacimiento", fechaNacimiento);
        update.put("correo", correo);
        update.put("telefono", telefono);
        update.put("domicilio", domicilio);
        update.put("contrasena", contrasena);

        boolean correoCambio = !correo.equals(correoDoc);
        if (correoCambio) {
            db.collection("clientes").document(correoDoc).delete()
                    .addOnSuccessListener(v1 -> db.collection("clientes").document(correo).set(update)
                            .addOnSuccessListener(v2 -> { Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show(); finish(); })
                            .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()))
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            db.collection("clientes").document(correoDoc).update(update)
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
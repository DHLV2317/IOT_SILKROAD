package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.databinding.ActivitySuperadminDetallesClienteBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DetallesClienteActivity extends AppCompatActivity {

    private ActivitySuperadminDetallesClienteBinding binding;
    private FirebaseFirestore db;
    private String docId;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminDetallesClienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        Intent i = getIntent();
        User cliente = (User) i.getSerializableExtra("cliente");
        if (cliente == null) { Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show(); finish(); return; }

        docId = cliente.getEmail();

        binding.inputNames.setText(cliente.getName());
        binding.inputLastNames.setText(cliente.getLastName());
        binding.inputDocType.setText(cliente.getDocumentType());
        binding.inputDocNumber.setText(cliente.getDocumentNumber());
        binding.inputBirthDate.setText(cliente.getBirthDate() == null ? "" : cliente.getBirthDate()); // guardas string
        binding.inputEmail.setText(cliente.getEmail());
        binding.inputPhone.setText(cliente.getPhone());
        binding.inputAddress.setText(cliente.getAddress());
        binding.inputPassword.setText(cliente.getPassword());
        binding.inputPasswordRepeat.setText(cliente.getPassword());

        updateEstadoUI(true); // si usas un campo 'active' puedes leerlo y reflejarlo

        binding.en.setOnClickListener(v -> setActive(true));
        binding.di.setOnClickListener(v -> setActive(false));
        binding.button.setOnClickListener(this::guardar);
    }

    private void updateEstadoUI(boolean activo) {
        int verde = ContextCompat.getColor(this, R.color.brand_verde);
        int rojo  = ContextCompat.getColor(this, R.color.red);
        int base  = ContextCompat.getColor(this, R.color.brand_celeste);
        binding.en.setBackgroundColor(activo ? verde : base);
        binding.di.setBackgroundColor(activo ? base  : rojo);
    }

    private void setActive(boolean active){
        db.collection("users").document(docId)
                .update("active", active)
                .addOnSuccessListener(v -> { updateEstadoUI(active); Toast.makeText(this, "Estado actualizado", Toast.LENGTH_SHORT).show(); })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void guardar(View view) {
        String nombres         = binding.inputNames.getText().toString().trim();
        String apellidos       = binding.inputLastNames.getText().toString().trim();
        String tipoDocumento   = binding.inputDocType.getText().toString().trim();
        String numeroDocumento = binding.inputDocNumber.getText().toString().trim();
        String fechaNacimiento = binding.inputBirthDate.getText().toString().trim();
        String correo          = binding.inputEmail.getText().toString().trim();
        String telefono        = binding.inputPhone.getText().toString().trim();
        String domicilio       = binding.inputAddress.getText().toString().trim();
        String pass            = binding.inputPassword.getText().toString().trim();
        String passRep         = binding.inputPasswordRepeat.getText().toString().trim();

        if (nombres.isEmpty() || apellidos.isEmpty() || tipoDocumento.isEmpty() || numeroDocumento.isEmpty()
                || correo.isEmpty() || telefono.isEmpty() || domicilio.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(passRep)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> up = new HashMap<>();
        up.put("name", nombres);
        up.put("lastName", apellidos);
        up.put("documentType", tipoDocumento);
        up.put("documentNumber", numeroDocumento);
        up.put("birthDate", fechaNacimiento);
        up.put("email", correo);
        up.put("phone", telefono);
        up.put("address", domicilio);
        up.put("password", pass);
        up.put("role", User.Role.CLIENT.name());

        boolean emailChange = !correo.equals(docId);
        if (emailChange) {
            db.collection("users").document(docId).delete()
                    .addOnSuccessListener(v1 -> db.collection("users").document(correo).set(up)
                            .addOnSuccessListener(v2 -> { Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show(); finish(); })
                            .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()))
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            db.collection("users").document(docId).update(up)
                    .addOnSuccessListener(v -> { Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show(); finish(); })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}
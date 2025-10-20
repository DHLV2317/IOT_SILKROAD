package com.example.silkroad_iot.ui.superadmin;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.databinding.ActivitySuperadminCrearAdministradorBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CrearAdministradorActivity extends AppCompatActivity {

    private ActivitySuperadminCrearAdministradorBinding binding;
    private FirebaseFirestore db;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminCrearAdministradorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();

        binding.btnCrear.setOnClickListener(v -> crearAdmin());
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private void crearAdmin() {
        String nombre     = binding.textInputLayout.getEditText().getText().toString().trim();
        String empresa    = binding.textInputLayout2.getEditText().getText().toString().trim();
        String correo     = binding.textInputLayout3.getEditText().getText().toString().trim();
        String telefono   = binding.textInputLayout4.getEditText().getText().toString().trim();
        String ubicacion  = binding.textInputLayout5.getEditText().getText().toString().trim();
        String pass       = binding.textInputLayout6.getEditText().getText().toString().trim();
        String passRepeat = binding.textInputLayout7.getEditText().getText().toString().trim();

        if (nombre.isEmpty() || empresa.isEmpty() || correo.isEmpty() || telefono.isEmpty() ||
                ubicacion.isEmpty() || pass.isEmpty() || passRepeat.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(passRepeat)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardamos como documento en /users con docId = correo
        Map<String, Object> data = new HashMap<>();
        data.put("name", nombre);
        data.put("email", correo);
        data.put("password", pass);         // (ojo: en producción no se guarda en claro)
        data.put("role", User.Role.ADMIN.name());
        data.put("phone", telefono);
        data.put("address", ubicacion);
        data.put("companyId", empresa);     // usamos companyId para guardar el nombre/ID de la empresa
        data.put("active", true);           // campo opcional de estado

        db.collection("users").document(correo)
                .set(data)
                .addOnSuccessListener(v -> { Toast.makeText(this, "Administrador creado", Toast.LENGTH_SHORT).show(); finish(); })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
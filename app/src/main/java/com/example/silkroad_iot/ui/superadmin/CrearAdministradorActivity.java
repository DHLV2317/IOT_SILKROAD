package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.MainActivity;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.databinding.ActivitySuperadminCrearAdministradorBinding;
import com.google.common.hash.Hashing;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.appcompat.widget.Toolbar;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CrearAdministradorActivity extends AppCompatActivity {

    private ActivitySuperadminCrearAdministradorBinding binding;
    private FirebaseFirestore db;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminCrearAdministradorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

        //String hashedPass = Hashing.sha256().hashString(pass, StandardCharsets.UTF_8).toString();

        // Guardamos como documento en /users con docId = correo
        Map<String, Object> data = new HashMap<>();
        data.put("name", nombre);
        data.put("email", correo);
        data.put("password", pass);         // (ojo: en producción no se guarda en claro)
        data.put("role", User.Role.ADMIN.name());
        data.put("phone", telefono);
        data.put("address", ubicacion);  //latitud-longitud
        data.put("companyId", empresa);     // usamos companyId para guardar el nombre/ID de la empresa
        data.put("active", true);           // campo opcional de estado

        //db.collection("users").document(correo)
        db.collection("usuarios").document(correo)//con correo
                .set(data)
                .addOnSuccessListener(v -> {
                    Map<String, Object> logData = new HashMap<>();
                    logData.put("tipo", "Creación");
                    logData.put("tipoUsuario", "Administrador");
                    logData.put("nombre", "De SuperAdministrador");
                    logData.put("usuario", nombre);
                    logData.put("descripcion", "Se ha creado el administrador de nombre " + nombre + " con el correo " + correo + " asignado a la empresa " + empresa);
                    logData.put("fecha", System.currentTimeMillis());
                    //Toast.makeText(this, "Administrador creado", Toast.LENGTH_SHORT).show(); finish(); })
                    db.collection("logs").document()
                            .set(logData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(CrearAdministradorActivity.this, "Administrador creado", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, AdministradoresActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(CrearAdministradorActivity.this, "Admin creado, pero falló el registro del log: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                finish();
                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CrearAdministradorActivity.this, "Error al crear administrador: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
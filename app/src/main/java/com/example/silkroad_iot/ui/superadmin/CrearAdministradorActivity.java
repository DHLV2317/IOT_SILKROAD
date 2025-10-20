package com.example.silkroad_iot.ui.superadmin;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.databinding.ActivitySuperadminCrearAdministradorBinding;
import com.example.silkroad_iot.ui.superadmin.entity.Administrador;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CrearAdministradorActivity extends AppCompatActivity {

    ActivitySuperadminCrearAdministradorBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminCrearAdministradorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();

        binding.btnCrear.setOnClickListener(v -> crearSuperAdminEmpresa());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    public void crearSuperAdminEmpresa() {

        String nombre=binding.textInputLayout.getEditText().getText().toString().trim();
        String empresa=binding.textInputLayout2.getEditText().getText().toString().trim();
        String correo=binding.textInputLayout3.getEditText().getText().toString().trim();
        String telefono=binding.textInputLayout4.getEditText().getText().toString().trim();
        String ubicacion=binding.textInputLayout5.getEditText().getText().toString().trim();
        String contrasena=binding.textInputLayout6.getEditText().getText().toString().trim();
        String contrasenaRepetida=binding.textInputLayout7.getEditText().getText().toString().trim();

        if(nombre.isEmpty() || empresa.isEmpty() || correo.isEmpty() || telefono.isEmpty()
                || ubicacion.isEmpty() || contrasena.isEmpty() || contrasenaRepetida.isEmpty()){
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!contrasena.equals(contrasenaRepetida)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("nombre", nombre);
        data.put("nombreEmpresa", empresa);
        data.put("correo", correo);
        data.put("telefono", telefono);
        data.put("ubicacion", ubicacion);
        data.put("contrasena", contrasena);
        data.put("activo", true);

        // docId = correo
        db.collection("administradores").document(correo)
                .set(data)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Administrador creado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
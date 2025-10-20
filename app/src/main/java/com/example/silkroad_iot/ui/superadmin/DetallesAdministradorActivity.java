
package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.databinding.ActivitySuperadminDetallesAdministradorBinding;
import com.example.silkroad_iot.ui.superadmin.entity.Administrador;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DetallesAdministradorActivity extends AppCompatActivity {

    ActivitySuperadminDetallesAdministradorBinding binding;
    private String correoDoc; // docId
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminDetallesAdministradorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        Administrador a = (Administrador) intent.getSerializableExtra("administrador");
        // Usamos correo como docId
        correoDoc = a.getCorreo();

        binding.textInputLayout.getEditText().setText(a.getNombre());
        binding.textInputLayout2.getEditText().setText(a.getNombreEmpresa());
        binding.textInputLayout3.getEditText().setText(a.getCorreo());
        binding.textInputLayout4.getEditText().setText(a.getTelefono());
        binding.textInputLayout5.getEditText().setText(a.getUbicacion());
        binding.textInputLayout6.getEditText().setText(a.getContrasena());
        binding.textInputLayout7.getEditText().setText(a.getContrasena());

        updateEstadoUI(a.isActivo());

        binding.en.setOnClickListener(v -> setActivo(true));
        binding.di.setOnClickListener(v -> setActivo(false));
        binding.btnGuardar.setOnClickListener(this::guardarCambios);
    }

    private void updateEstadoUI(boolean activo){
        if(activo){
            binding.en.setBackgroundColor(getResources().getColor(R.color.green, null));
            binding.di.setBackgroundColor(getResources().getColor(R.color.base, null));
        }else{
            binding.en.setBackgroundColor(getResources().getColor(R.color.base, null));
            binding.di.setBackgroundColor(getResources().getColor(R.color.red, null));
        }
    }

    private void setActivo(boolean activo){
        db.collection("administradores").document(correoDoc)
                .update("activo", activo)
                .addOnSuccessListener(v -> {
                    updateEstadoUI(activo);
                    Toast.makeText(this, "Estado actualizado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void guardarCambios(View view){
        String nombre=binding.textInputLayout.getEditText().getText().toString().trim();
        String empresa=binding.textInputLayout2.getEditText().getText().toString().trim();
        String correo=binding.textInputLayout3.getEditText().getText().toString().trim();
        String telefono=binding.textInputLayout4.getEditText().getText().toString().trim();
        String ubicacion=binding.textInputLayout5.getEditText().getText().toString().trim();
        String contrasena=binding.textInputLayout6.getEditText().getText().toString().trim();
        String contrasenaRepetida=binding.textInputLayout7.getEditText().getText().toString().trim();

        if(nombre.isEmpty() || empresa.isEmpty() || correo.isEmpty() || telefono.isEmpty() || ubicacion.isEmpty()){
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (contrasena.isEmpty()||contrasenaRepetida.isEmpty()) {
            Toast.makeText(this, "Rellena ambos campos de contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!contrasena.equals(contrasenaRepetida)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> up = new HashMap<>();
        up.put("nombre", nombre);
        up.put("nombreEmpresa", empresa);
        up.put("correo", correo);
        up.put("telefono", telefono);
        up.put("ubicacion", ubicacion);
        up.put("contrasena", contrasena);

        // Si cambian el correo, queremos que el docId sea el nuevo correo:
        boolean correoCambio = !correo.equals(correoDoc);
        if (correoCambio) {
            db.collection("administradores").document(correoDoc).delete()
                    .addOnSuccessListener(v1 -> db.collection("administradores").document(correo).set(up)
                            .addOnSuccessListener(v2 -> {
                                Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()))
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            db.collection("administradores").document(correoDoc).update(up)
                    .addOnSuccessListener(v -> {
                        Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
<<<<<<< Updated upstream

    public void speditarAdmin(View view)  {
            String nombre=binding.textInputLayout.getEditText().getText().toString();
            String empresa=binding.textInputLayout2.getEditText().getText().toString();
            String correo=binding.textInputLayout3.getEditText().getText().toString();
            String telefono=binding.textInputLayout4.getEditText().getText().toString();
            String ubicacion=binding.textInputLayout5.getEditText().getText().toString();
            String contrasena=binding.textInputLayout6.getEditText().getText().toString();
            String contrasenaRepetida=binding.textInputLayout7.getEditText().getText().toString();

            if(nombre.isEmpty() || empresa.isEmpty() || correo.isEmpty() || telefono.isEmpty() || ubicacion.isEmpty()){
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            }else {
                if (contrasena.isEmpty()||contrasenaRepetida.isEmpty()) {
                    Toast.makeText(this, "Rellena ambos campos de contraseña", Toast.LENGTH_SHORT).show();
                }
                else if (!contrasena.equals(contrasenaRepetida)) {
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }
                else {
                    Administrador administrador = new Administrador();
                    administrador.setNombre(nombre);
                    administrador.setNombreEmpresa(empresa);
                    administrador.setCorreo(correo);
                    administrador.setTelefono(telefono);
                    administrador.setUbicacion(ubicacion);
                    administrador.setContrasena(contrasena);
                    administrador.setActivo(true);
                    Global.listaAdministradores.set(posicion, administrador);
                    Intent intent = new Intent(this, AdministradoresActivity.class);
                    startActivity(intent);

                }
            }
    }

    public void sphabilitarAdmin(View view){
        Global.listaAdministradores.get(posicion).setActivo(true);
        Intent intent = new Intent(this, DetallesAdministradorActivity.class);
        intent.putExtra("posicion", posicion);
        intent.putExtra("administrador", Global.listaAdministradores.get(posicion));
        startActivity(intent);
    }

    public void spdeshabilitarAdmin(View view){
        Global.listaAdministradores.get(posicion).setActivo(false);
        Intent intent = new Intent(this, DetallesAdministradorActivity.class);
        intent.putExtra("posicion", posicion);
        intent.putExtra("administrador", Global.listaAdministradores.get(posicion));
        startActivity(intent);
    }

}
=======
}
>>>>>>> Stashed changes

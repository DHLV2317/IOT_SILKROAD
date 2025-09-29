package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.silkroad_iot.R;
import com.example.silkroad_iot.databinding.ActivitySuperadminCrearAdministradorBinding;
import com.example.silkroad_iot.ui.superadmin.entity.Administrador;
import com.example.silkroad_iot.ui.superadmin.entity.Global;


public class CrearAdministradorActivity extends AppCompatActivity {

    ActivitySuperadminCrearAdministradorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminCrearAdministradorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


    public void crearSuperAdminEmpresa(View view) {

            String nombre=binding.textInputLayout.getEditText().getText().toString();
            String empresa=binding.textInputLayout2.getEditText().getText().toString();
            String correo=binding.textInputLayout3.getEditText().getText().toString();
            String telefono=binding.textInputLayout4.getEditText().getText().toString();
            String ubicacion=binding.textInputLayout5.getEditText().getText().toString();
            String contrasena=binding.textInputLayout6.getEditText().getText().toString();
            String contrasenaRepetida=binding.textInputLayout7.getEditText().getText().toString();

            if(nombre.isEmpty() || empresa.isEmpty() || correo.isEmpty() || telefono.isEmpty() || ubicacion.isEmpty() || contrasena.isEmpty() || contrasenaRepetida.isEmpty()){
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            }else{
                Administrador administrador= new Administrador();
                administrador.setNombre(nombre);
                administrador.setNombreEmpresa(empresa);
                administrador.setCorreo(correo);
                administrador.setTelefono(telefono);
                administrador.setUbicacion(ubicacion);
                administrador.setContrasena(contrasena);
                administrador.setActivo(true);
                Global.listaAdministradores.add(administrador);
                Intent intent = new Intent(this, AdministradoresActivity.class);
                startActivity(intent);
            }

        }
}






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
import com.example.silkroad_iot.ui.superadmin.entity.Global;


public class DetallesAdministradorActivity extends AppCompatActivity {

    ActivitySuperadminDetallesAdministradorBinding binding;
    private int posicion;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminDetallesAdministradorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();

        posicion = intent.getIntExtra("posicion", -1);
        Administrador administrador = (Administrador) intent.getSerializableExtra("administrador");

        binding.textInputLayout.getEditText().setText(administrador.getNombre());
        binding.textInputLayout2.getEditText().setText(administrador.getNombreEmpresa());
        binding.textInputLayout3.getEditText().setText(administrador.getCorreo());
        binding.textInputLayout4.getEditText().setText(administrador.getTelefono());
        binding.textInputLayout5.getEditText().setText(administrador.getUbicacion());
        binding.textInputLayout6.getEditText().setText(administrador.getContrasena());
        binding.textInputLayout7.getEditText().setText(administrador.getContrasena());

        if(administrador.isActivo()){
            binding.en.setBackgroundColor(getResources().getColor(R.color.green, null));
            binding.di.setBackgroundColor(getResources().getColor(R.color.base, null));
        }else{
            binding.en.setBackgroundColor(getResources().getColor(R.color.base, null));
            binding.di.setBackgroundColor(getResources().getColor(R.color.red, null));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

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

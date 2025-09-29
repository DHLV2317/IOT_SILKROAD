package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.databinding.ActivitySuperadminDetallesGuiaBinding;
import com.example.silkroad_iot.ui.superadmin.entity.Global;
import com.example.silkroad_iot.ui.superadmin.entity.Guia;

import java.sql.Date;


public class DetallesGuiaActivity extends AppCompatActivity {

    ActivitySuperadminDetallesGuiaBinding binding;
    private int posicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminDetallesGuiaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();

        posicion = intent.getIntExtra("posicion", -1);
        Guia guia = (Guia) intent.getSerializableExtra("guia");

        binding.textInputLayout.getEditText().setText(guia.getNombres());
        binding.textInputLayout2.getEditText().setText(guia.getApellidos());
        binding.textInputLayout3.getEditText().setText(guia.getTipoDocumento());
        binding.textInputLayout4.getEditText().setText(guia.getNumeroDocumento());
        binding.textInputLayout5.getEditText().setText(guia.getFechaNacimiento().toString());
        binding.textInputLayout6.getEditText().setText(guia.getCorreo());
        binding.textInputLayout7.getEditText().setText(guia.getTelefono());
        binding.textInputLayout8.getEditText().setText(guia.getDomicilio());
        binding.textInputLayout9.getEditText().setText(guia.getIdiomas());
        binding.textInputLayout10.getEditText().setText(guia.getContrasena());
        binding.textInputLayout11.getEditText().setText(guia.getContrasena());

        if(guia.isActivo()){
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

    public void speditarGuia(View view)  {
        String nombres=binding.textInputLayout.getEditText().getText().toString();
        String apellidos=binding.textInputLayout2.getEditText().getText().toString();
        String tipoDocumento=binding.textInputLayout3.getEditText().getText().toString();
        String numeroDocumento=binding.textInputLayout4.getEditText().getText().toString();
        String fechaNacimiento=binding.textInputLayout5.getEditText().getText().toString();
        String correo=binding.textInputLayout6.getEditText().getText().toString();
        String telefono=binding.textInputLayout7.getEditText().getText().toString();
        String domicilio=binding.textInputLayout8.getEditText().getText().toString();
        String idiomas=binding.textInputLayout9.getEditText().getText().toString();
        String contrasena=binding.textInputLayout10.getEditText().getText().toString();
        String contrasenaRepetida=binding.textInputLayout11.getEditText().getText().toString();

        if(nombres.isEmpty()||apellidos.isEmpty()||tipoDocumento.isEmpty()||numeroDocumento.isEmpty()||fechaNacimiento.isEmpty()||correo.isEmpty()||telefono.isEmpty()||domicilio.isEmpty()||idiomas.isEmpty()){
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
        }else {
            if (contrasena.isEmpty()||contrasenaRepetida.isEmpty()) {
                Toast.makeText(this, "Rellena ambos campos de contraseña", Toast.LENGTH_SHORT).show();
            }
            else if (!contrasena.equals(contrasenaRepetida)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            }
            else {
                Guia guia = new Guia();
                guia.setNombres(nombres);
                guia.setApellidos(apellidos);
                guia.setTipoDocumento(tipoDocumento);
                guia.setNumeroDocumento(numeroDocumento);
                guia.setFechaNacimiento(Date.valueOf(fechaNacimiento));
                guia.setCorreo(correo);
                guia.setTelefono(telefono);
                guia.setDomicilio(domicilio);
                guia.setIdiomas(idiomas);
                guia.setContrasena(contrasena);
                Global.listaGuiasAprobados.set(posicion, guia);
                Intent intent = new Intent(this, GuiasActivity.class);
                startActivity(intent);

            }
        }
    }

    public void sphabilitarGuia(View view){
        Global.listaGuiasAprobados.get(posicion).setActivo(true);
        Intent intent = new Intent(this, DetallesGuiaActivity.class);
        intent.putExtra("posicion", posicion);
        intent.putExtra("guia", Global.listaGuiasAprobados.get(posicion));
        startActivity(intent);
    }

    public void spdeshabilitarGuia(View view){
        Global.listaGuiasAprobados.get(posicion).setActivo(false);
        Intent intent = new Intent(this, DetallesGuiaActivity.class);
        intent.putExtra("posicion", posicion);
        intent.putExtra("guia", Global.listaGuiasAprobados.get(posicion));
        startActivity(intent);
    }

}

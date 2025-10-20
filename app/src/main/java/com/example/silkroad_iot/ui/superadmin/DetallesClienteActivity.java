package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.databinding.ActivitySuperadminDetallesClienteBinding;
import com.example.silkroad_iot.databinding.ActivitySuperadminDetallesGuiaBinding;
import com.example.silkroad_iot.ui.superadmin.entity.Cliente;
import com.example.silkroad_iot.ui.superadmin.entity.Global;
import com.example.silkroad_iot.ui.superadmin.entity.Guia;

import java.sql.Date;


public class DetallesClienteActivity extends AppCompatActivity {

    ActivitySuperadminDetallesClienteBinding binding;
    private int posicion;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminDetallesClienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();

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
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    public void speditarCliente(View view)  {
        String nombres=binding.textInputLayout.getEditText().getText().toString();
        String apellidos=binding.textInputLayout2.getEditText().getText().toString();
        String tipoDocumento=binding.textInputLayout3.getEditText().getText().toString();
        String numeroDocumento=binding.textInputLayout4.getEditText().getText().toString();
        String fechaNacimiento=binding.textInputLayout5.getEditText().getText().toString();
        String correo=binding.textInputLayout6.getEditText().getText().toString();
        String telefono=binding.textInputLayout7.getEditText().getText().toString();
        String domicilio=binding.textInputLayout8.getEditText().getText().toString();
        String contrasena=binding.textInputLayout10.getEditText().getText().toString();
        String contrasenaRepetida=binding.textInputLayout11.getEditText().getText().toString();

        if(nombres.isEmpty()||apellidos.isEmpty()||tipoDocumento.isEmpty()||numeroDocumento.isEmpty()||fechaNacimiento.isEmpty()||correo.isEmpty()||telefono.isEmpty()||domicilio.isEmpty()){
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
        }else {
            if (contrasena.isEmpty()||contrasenaRepetida.isEmpty()) {
                Toast.makeText(this, "Rellena ambos campos de contraseña", Toast.LENGTH_SHORT).show();
            }
            else if (!contrasena.equals(contrasenaRepetida)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            }
            else {
                Cliente cliente= new Cliente();
                cliente.setNombres(nombres);
                cliente.setApellidos(apellidos);
                cliente.setTipoDocumento(tipoDocumento);
                cliente.setNumeroDocumento(numeroDocumento);
                cliente.setFechaNacimiento(Date.valueOf(fechaNacimiento));
                cliente.setCorreo(correo);
                cliente.setTelefono(telefono);
                cliente.setDomicilio(domicilio);
                cliente.setContrasena(contrasena);
                Global.listaClientes.set(posicion, cliente);
                Intent intent = new Intent(this, ClientesActivity.class);
                startActivity(intent);

            }
        }
    }

    public void sphabilitarCliente(View view){
        Global.listaClientes.get(posicion).setActivo(true);
        Intent intent = new Intent(this, DetallesClienteActivity.class);
        intent.putExtra("posicion", posicion);
        intent.putExtra("cliente", Global.listaClientes.get(posicion));
        startActivity(intent);
    }

    public void spdeshabilitarCliente(View view){
        Global.listaClientes.get(posicion).setActivo(false);
        Intent intent = new Intent(this, DetallesClienteActivity.class);
        intent.putExtra("posicion", posicion);
        intent.putExtra("cliente", Global.listaClientes.get(posicion));
        startActivity(intent);
    }

}

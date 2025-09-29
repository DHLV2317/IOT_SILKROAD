package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.databinding.ActivitySuperadminDetallesGuiaBinding;
import com.example.silkroad_iot.databinding.ActivitySuperadminDetallesSolicitudGuiaBinding;
import com.example.silkroad_iot.ui.superadmin.entity.Global;
import com.example.silkroad_iot.ui.superadmin.entity.Guia;

import java.sql.Date;


public class DetallesSolicitudGuiaActivity extends AppCompatActivity {

    ActivitySuperadminDetallesSolicitudGuiaBinding binding;
    private int posicion;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminDetallesSolicitudGuiaBinding.inflate(getLayoutInflater());
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

        if(guia.isAprobado()){
            binding.en.setBackgroundColor(getResources().getColor(R.color.green, null));
            binding.di.setBackgroundColor(getResources().getColor(R.color.base, null));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    public void spaceptarGuia(View view){
        Global.listaGuiasAprobados.get(posicion).setAprobado(true);
        Intent intent = new Intent(this, SolicitudesGuiasActivity.class);
        intent.putExtra("posicion", posicion);
        intent.putExtra("guia", Global.listaGuiasAprobados.get(posicion));
        startActivity(intent);
    }

    public void spdenegarGuia(View view){
        Global.listaGuiasAprobados.get(posicion).setAprobado(false);
        Intent intent = new Intent(this, SolicitudesGuiasActivity.class);
        intent.putExtra("posicion", posicion);
        intent.putExtra("guia", Global.listaGuiasAprobados.get(posicion));
        startActivity(intent);
    }

}

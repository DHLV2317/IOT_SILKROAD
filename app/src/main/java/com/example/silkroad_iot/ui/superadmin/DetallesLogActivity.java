package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.databinding.ActivitySuperadminDetallesAdministradorBinding;
import com.example.silkroad_iot.databinding.ActivitySuperadminDetallesLogBinding;
import com.example.silkroad_iot.ui.superadmin.entity.Administrador;
import com.example.silkroad_iot.ui.superadmin.entity.Global;
import com.example.silkroad_iot.ui.superadmin.entity.Log;

public class DetallesLogActivity extends AppCompatActivity {

    ActivitySuperadminDetallesLogBinding binding;
    private int posicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminDetallesLogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();

        posicion = intent.getIntExtra("posicion", -1);
        Log log = (Log) intent.getSerializableExtra("log");

        binding.textView.setText("Evento " +log.getTipo()+" de "+log.getTipoUsuario());
        binding.textView1.setText(log.getNombre());
        binding.textView12.setText(log.getFecha());
        binding.textView13.setText(log.getHora());
        binding.textView14.setText(log.getUsuario());
        binding.textView15.setText(log.getDescripcion());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


}
package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.databinding.ActivitySuperadminDetallesSolicitudGuiaBinding;
import com.example.silkroad_iot.ui.superadmin.entity.Guia;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetallesSolicitudGuiaActivity extends AppCompatActivity {

    ActivitySuperadminDetallesSolicitudGuiaBinding binding;
    private String correoDoc;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminDetallesSolicitudGuiaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        Guia guia = (Guia) intent.getSerializableExtra("guia");
        correoDoc = guia.getCorreo();

        binding.textInputLayout.getEditText().setText(guia.getNombres());
        binding.textInputLayout2.getEditText().setText(guia.getApellidos());
        binding.textInputLayout3.getEditText().setText(guia.getTipoDocumento());
        binding.textInputLayout4.getEditText().setText(guia.getNumeroDocumento());
        binding.textInputLayout5.getEditText().setText(guia.getFechaNacimiento() != null ? guia.getFechaNacimiento().toString() : "");
        binding.textInputLayout6.getEditText().setText(guia.getCorreo());
        binding.textInputLayout7.getEditText().setText(guia.getTelefono());
        binding.textInputLayout8.getEditText().setText(guia.getDomicilio());
        binding.textInputLayout9.getEditText().setText(guia.getIdiomas());

        binding.en.setOnClickListener(v -> setAprobado(true));
        binding.di.setOnClickListener(v -> setAprobado(false));
    }

    private void setAprobado(boolean aprobado){
        db.collection("guias").document(correoDoc)
                .update("aprobado", aprobado)
                .addOnSuccessListener(v -> finish())
                .addOnFailureListener(e -> {});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}
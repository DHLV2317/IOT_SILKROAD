package com.example.silkroad_iot.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.databinding.ActivityRegisterBinding;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registro Cliente"); // Changed title

        // Continuar registro Cliente
        b.btnContinue.setOnClickListener(v -> {
            String name = Objects.requireNonNull(b.inputName.getText()).toString().trim();
            String mail = Objects.requireNonNull(b.inputEmail.getText()).toString().trim();
            String pas1 = Objects.requireNonNull(b.inputPass1.getText()).toString().trim();
            String pas2 = Objects.requireNonNull(b.inputPass2.getText()).toString().trim();
            boolean term = b.ckTerms.isChecked();

            if (TextUtils.isEmpty(name)) { b.inputName.setError("Requerido"); return; }
            if (TextUtils.isEmpty(mail)) { b.inputEmail.setError("Requerido"); return; }
            if (TextUtils.isEmpty(pas1)) { b.inputPass1.setError("Requerido"); return; }
            if (TextUtils.isEmpty(pas2)) { b.inputPass2.setError("Requerido"); return; }
            if (!term) { Toast.makeText(this, "Debes aceptar los términos", Toast.LENGTH_SHORT).show(); return; }

            if (!pas1.equals(pas2)) {
                b.inputPass2.setError("Las contraseñas no coinciden");
            } else {
                // Asumiendo que este flujo es para CLIENTES
                // Puedes diferenciar o generalizar el UserStore si es necesario
                Intent i = new Intent(this, RegisterVerifyActivity.class);
                i.putExtra("NAME", name);
                i.putExtra("MAIL", mail);
                i.putExtra("PASS", pas1);
                // Podrías añadir un extra para indicar el tipo de usuario si RegisterVerifyActivity lo necesita
                // i.putExtra("USER_ROLE", "CLIENT");
                startActivity(i);
            }
        });

        // Ir a Registro Guía
        b.btnRegisterGuide.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuideRegisterActivity.class);
            startActivity(intent);
        });
    }

    // It's good practice to handle the Up button navigation
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
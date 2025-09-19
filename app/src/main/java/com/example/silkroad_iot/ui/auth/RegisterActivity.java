package com.example.silkroad_iot.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.appcompat.app.AppCompatActivity;
import com.example.silkroad_iot.databinding.ActivityRegisterBinding;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.google.android.material.snackbar.Snackbar;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding b;
    private final UserStore store = UserStore.get();

    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        b = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        b.toolbar.setNavigationOnClickListener(v -> finish());

        b.btnContinue.setOnClickListener(v -> {
            String name  = b.inputName.getText().toString().trim();
            String email = b.inputEmail.getText().toString().trim();
            String p1    = b.inputPass1.getText().toString().trim();
            String p2    = b.inputPass2.getText().toString().trim();

            if (TextUtils.isEmpty(name)){ b.inputName.setError("Requerido"); return; }
            if (TextUtils.isEmpty(email)){ b.inputEmail.setError("Requerido"); return; }
            if (TextUtils.isEmpty(p1)){ b.inputPass1.setError("Requerido"); return; }
            if (!p1.equals(p2)){ b.inputPass2.setError("No coincide"); return; }
            if (!b.ckTerms.isChecked()){ Snackbar.make(b.getRoot(),"Acepta los términos",Snackbar.LENGTH_SHORT).show(); return; }
            if (store.exists(email)){ b.inputEmail.setError("Correo ya registrado"); return; }

            // Inicia registro y genera código (lo mostramos por demo)
            String code = store.startRegistration(new User(name, email, p1));
            Snackbar.make(b.getRoot(), "Código enviado: " + code, Snackbar.LENGTH_LONG).show();

            Intent i = new Intent(this, RegisterVerifyActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });
    }
}
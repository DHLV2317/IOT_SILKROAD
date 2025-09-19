package com.example.silkroad_iot.ui.client;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.silkroad_iot.databinding.ActivityClientOnboardingBinding;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.google.android.material.snackbar.Snackbar;

public class ClientOnboardingActivity extends AppCompatActivity {
    private ActivityClientOnboardingBinding b;
    private final UserStore store = UserStore.get();
    private Uri photoUri;

    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    uri -> { if (uri!=null){ photoUri=uri; b.imgAvatar.setImageURI(uri); }});

    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        b = ActivityClientOnboardingBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);

        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // flecha vuelve al login
        b.toolbar.setNavigationOnClickListener(v -> finish());

        User u = store.getLogged();
        if (u==null){ finish(); return; }

        // precargar
        b.inputEmail.setText(u.getEmail());
        b.inputName.setText(u.getName());

        b.btnPickPhoto.setOnClickListener(v -> pickImage.launch("image/*"));

        b.btnConfirm.setOnClickListener(v -> {
            String name = b.inputName.getText().toString().trim();
            String last = b.inputLastName.getText().toString().trim();
            String phone= b.inputPhone.getText().toString().trim();
            String addr = b.inputAddress.getText().toString().trim();

            if (TextUtils.isEmpty(name)){ b.inputName.setError("Requerido"); return; }
            if (TextUtils.isEmpty(last)){ b.inputLastName.setError("Requerido"); return; }
            if (TextUtils.isEmpty(phone)){ b.inputPhone.setError("Requerido"); return; }
            if (TextUtils.isEmpty(addr)){ b.inputAddress.setError("Requerido"); return; }

            u.setName(name);
            u.setLastName(last);
            u.setPhone(phone);
            u.setAddress(addr);
            if (photoUri!=null) u.setPhotoUri(photoUri.toString());
            u.setClientProfileCompleted(true);

            store.updateLogged(u);

            Snackbar.make(b.getRoot(),"Datos confirmados", Snackbar.LENGTH_SHORT).show();
            startActivity(new Intent(this, ClientHomeActivity.class));
            finish();
        });
    }
}
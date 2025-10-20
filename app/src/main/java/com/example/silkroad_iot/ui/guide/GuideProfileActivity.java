package com.example.silkroad_iot.ui.guide;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.MainActivity;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityGuideProfileBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GuideProfileActivity extends AppCompatActivity {

    private ActivityGuideProfileBinding b;
    private Uri newImageUri;
    private FirebaseFirestore db;
    private String guideDocId; // doc id en "guias"

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    newImageUri = result.getData().getData();
                    b.imgProfilePhoto.setImageURI(newImageUri);
                    b.btnSaveChanges.setEnabled(true);
                }
            });

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityGuideProfileBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mi Perfil");
        }

        db = FirebaseFirestore.getInstance();
        setupClickListeners();
        fetchGuide();
    }

    private void fetchGuide() {
        User u = UserStore.get().getLogged();
        String email = (u!=null ? u.getEmail() : null);
        if (email == null || email.isEmpty()) return;

        db.collection("guias")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(snap -> {
                    if (snap.isEmpty()) return;
                    DocumentSnapshot d = snap.getDocuments().get(0);
                    guideDocId = d.getId();

                    String nombre   = d.getString("nombre");
                    String apellidos= d.getString("apellidos"); // si lo tienes
                    String telefono = d.getString("telefono");
                    String direccion= d.getString("direccion");
                    String langs    = d.getString("langs");
                    String foto     = d.getString("fotoUrl");

                    b.txtProfileName.setText((nombre==null?"":nombre) + (apellidos==null?"":" "+apellidos));
                    b.inputNames.setText(nombre==null?"":nombre);
                    b.inputLastNames.setText(apellidos==null?"":apellidos);
                    b.inputEmail.setText(email);
                    b.inputPhone.setText(telefono==null?"":telefono);
                    b.inputAddress.setText(direccion==null?"":direccion);
                    b.inputLanguages.setText(langs==null?"":langs);

                    if (foto != null && !foto.isEmpty()) {
                        try { b.imgProfilePhoto.setImageURI(Uri.parse(foto)); } catch (Exception ignore){}
                    }
                });
    }

    private void setupClickListeners() {
        b.btnChangePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        b.btnSaveChanges.setOnClickListener(v -> saveProfileChanges());

        b.btnViewFullHistory.setOnClickListener(v ->
                startActivity(new Intent(this, GuideHistoryActivity.class)));

        b.btnLogout.setOnClickListener(v -> {
            UserStore.get().logout();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void saveProfileChanges() {
        if (guideDocId == null) {
            Snackbar.make(b.getRoot(), "No se encontró el documento del guía", Snackbar.LENGTH_LONG).show();
            return;
        }

        db.collection("guias").document(guideDocId)
                .update(
                        "nombre", b.inputNames.getText()==null?"":b.inputNames.getText().toString().trim(),
                        "apellidos", b.inputLastNames.getText()==null?"":b.inputLastNames.getText().toString().trim(),
                        "telefono", b.inputPhone.getText()==null?"":b.inputPhone.getText().toString().trim(),
                        "direccion", b.inputAddress.getText()==null?"":b.inputAddress.getText().toString().trim(),
                        "langs", b.inputLanguages.getText()==null?"":b.inputLanguages.getText().toString().trim(),
                        "fotoUrl", (newImageUri!=null? newImageUri.toString() : null)
                )
                .addOnSuccessListener(unused -> {
                    Snackbar.make(b.getRoot(), "✅ Perfil actualizado", Snackbar.LENGTH_LONG).show();
                    b.btnSaveChanges.setEnabled(false);
                })
                .addOnFailureListener(e ->
                        Snackbar.make(b.getRoot(), "Error: " + e.getMessage(), Snackbar.LENGTH_LONG).show());
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
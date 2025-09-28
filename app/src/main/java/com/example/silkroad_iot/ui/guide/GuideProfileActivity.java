package com.example.silkroad_iot.ui.guide;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.MainActivity;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityGuideProfileBinding;
import com.google.android.material.snackbar.Snackbar;

public class GuideProfileActivity extends AppCompatActivity {

    private ActivityGuideProfileBinding binding;
    private Uri newImageUri;
    
    // Para seleccionar nueva foto
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    newImageUri = result.getData().getData();
                    binding.imgProfilePhoto.setImageURI(newImageUri);
                    binding.btnSaveChanges.setEnabled(true);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuideProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mi Perfil");
        }

        loadUserData();
        setupClickListeners();
        generateFakeHistory();
    }

    private void loadUserData() {
        User guide = UserStore.get().getLogged();
        if (guide == null) return;

        // Datos personales
        binding.txtProfileName.setText(guide.getName() + " " + (guide.getLastName() != null ? guide.getLastName() : ""));
        binding.inputNames.setText(guide.getName());
        binding.inputLastNames.setText(guide.getLastName() != null ? guide.getLastName() : "");
        binding.inputEmail.setText(guide.getEmail());
        binding.inputPhone.setText(guide.getPhone() != null ? guide.getPhone() : "");
        binding.inputAddress.setText(guide.getAddress() != null ? guide.getAddress() : "");
        binding.inputLanguages.setText(guide.getLanguages() != null ? guide.getLanguages() : "");

        // Información de documento
        if (guide.getDocumentType() != null && guide.getDocumentNumber() != null) {
            binding.txtDocumentInfo.setText(guide.getDocumentType() + ": " + guide.getDocumentNumber());
        }
        
        if (guide.getBirthDate() != null) {
            binding.txtBirthDate.setText("Nacimiento: " + guide.getBirthDate());
        }

        // Estado de aprobación
        String status = "⏳ Pendiente";
        if (guide.isGuideApproved()) {
            status = "✅ Aprobado";
        } else if ("REJECTED".equals(guide.getGuideApprovalStatus())) {
            status = "❌ Rechazado";
        }
        binding.txtApprovalStatus.setText("Estado: " + status);

        // Estadísticas simuladas
        binding.txtTotalTours.setText("Tours realizados: 23");
        binding.txtTotalEarnings.setText("Ingresos totales: S/ 4,850");
        binding.txtAverageRating.setText("Calificación: ⭐ 4.8/5.0");
    }

    private void setupClickListeners() {
        // Cambiar foto de perfil
        binding.btnChangePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        // Guardar cambios
        binding.btnSaveChanges.setOnClickListener(v -> saveProfileChanges());

        // Ver historial completo
        binding.btnViewFullHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, GuideHistoryActivity.class));
        });

        // Cerrar sesión
        binding.btnLogout.setOnClickListener(v -> {
            UserStore.get().logout();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void saveProfileChanges() {
        User guide = UserStore.get().getLogged();
        if (guide == null) return;

        // Actualizar datos básicos (solo los editables)
        guide.setName(binding.inputNames.getText().toString().trim());
        guide.setLastName(binding.inputLastNames.getText().toString().trim());
        guide.setPhone(binding.inputPhone.getText().toString().trim());
        guide.setAddress(binding.inputAddress.getText().toString().trim());
        guide.setLanguages(binding.inputLanguages.getText().toString().trim());

        // Actualizar foto si hay una nueva
        if (newImageUri != null) {
            guide.setPhotoUri(newImageUri.toString());
        }

        // Guardar cambios
        UserStore.get().updateLogged(guide);
        
        Snackbar.make(binding.getRoot(), "✅ Perfil actualizado correctamente", Snackbar.LENGTH_LONG).show();
        binding.btnSaveChanges.setEnabled(false);
        
        // Recargar datos para mostrar cambios
        loadUserData();
    }

    private void generateFakeHistory() {
        // Historial simulado de tours recientes
        String recentHistory = 
            "📅 25 Sep 2025 - City Tour Lima Colonial - ⭐ 5.0\n" +
            "📅 23 Sep 2025 - Tour Gastronómico Barranco - ⭐ 4.5\n" +
            "📅 20 Sep 2025 - Machu Picchu Full Day - ⭐ 5.0\n" +
            "📅 18 Sep 2025 - Valle Sagrado - ⭐ 4.8\n" +
            "📅 15 Sep 2025 - Líneas de Nazca - ⭐ 4.9\n\n" +
            "Toca 'Ver Historial Completo' para más detalles...";
            
        binding.txtRecentHistory.setText(recentHistory);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
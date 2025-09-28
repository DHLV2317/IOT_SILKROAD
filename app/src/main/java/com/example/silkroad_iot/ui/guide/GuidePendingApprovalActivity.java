package com.example.silkroad_iot.ui.guide;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.MainActivity;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityGuidePendingApprovalBinding;

public class GuidePendingApprovalActivity extends AppCompatActivity {
    
    private ActivityGuidePendingApprovalBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuidePendingApprovalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Cuenta Pendiente");
        }
        
        setupUserInfo();
        setupClickListeners();
    }
    
    private void setupUserInfo() {
        User guide = UserStore.get().getLogged();
        if (guide != null) {
            binding.txtGuideName.setText(guide.getName() + " " + (guide.getLastName() != null ? guide.getLastName() : ""));
            binding.txtGuideEmail.setText(guide.getEmail());
            
            // Mostrar información de registro
            if (guide.getDocumentNumber() != null) {
                binding.txtDocumentInfo.setText(guide.getDocumentType() + ": " + guide.getDocumentNumber());
            }
            if (guide.getPhone() != null) {
                binding.txtPhoneInfo.setText("Teléfono: " + guide.getPhone());
            }
            if (guide.getLanguages() != null) {
                binding.txtLanguagesInfo.setText("Idiomas: " + guide.getLanguages());
            }
            
            // Estado de aprobación
            String status = guide.getGuideApprovalStatus();
            switch (status) {
                case "PENDING":
                    binding.txtStatusInfo.setText("⏳ Pendiente de Aprobación");
                    binding.txtStatusDescription.setText("Tu solicitud está siendo revisada por nuestro equipo de administradores.");
                    break;
                case "REJECTED":
                    binding.txtStatusInfo.setText("❌ Solicitud Rechazada");
                    binding.txtStatusDescription.setText("Lo sentimos, tu solicitud ha sido rechazada. Contacta con soporte para más información.");
                    break;
                default:
                    binding.txtStatusInfo.setText("⏳ En Revisión");
                    binding.txtStatusDescription.setText("Tu solicitud está siendo procesada.");
            }
        }
    }
    
    private void setupClickListeners() {
        binding.btnBackToLogin.setOnClickListener(v -> {
            UserStore.get().logout();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        
        binding.btnRefreshStatus.setOnClickListener(v -> {
            // Simular verificación de estado
            User guide = UserStore.get().getLogged();
            if (guide != null && guide.isGuideApproved()) {
                // Si ahora está aprobado, ir al dashboard
                startActivity(new Intent(this, GuideHomeActivity.class));
                finish();
            } else {
                binding.txtLastCheck.setText("Última verificación: " + 
                    java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()));
            }
        });
    }
}
package com.example.silkroad_iot.ui.guide;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.silkroad_iot.databinding.ActivityGuideQrScannerBinding;
import com.google.android.material.snackbar.Snackbar;

public class GuideQRScannerActivity extends AppCompatActivity {

    private ActivityGuideQrScannerBinding binding;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuideQrScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Finalizar Servicio - QR");
        }

        setupViews();
        checkCameraPermission();
    }

    private void setupViews() {
        // Para demo, simulamos el escaneo QR
        binding.btnSimulateQRScan.setOnClickListener(v -> simulateQRScan());
        binding.btnManualCheckout.setOnClickListener(v -> manualCheckout());
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
            != PackageManager.PERMISSION_GRANTED) {
            
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            binding.txtCameraStatus.setText("Cámara: Habilitada ✓");
        }
    }

    private void simulateQRScan() {
        // Simular escaneo exitoso de QR
        String clientId = "CLI-2024-001";
        String tourId = "TOUR-LIMA-001";
        String timestamp = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
        
        binding.txtScanResult.setText(
            "✅ QR ESCANEADO EXITOSAMENTE\n\n" +
            "Cliente ID: " + clientId + "\n" +
            "Tour ID: " + tourId + "\n" +
            "Hora Check-out: " + timestamp + "\n\n" +
            "🎉 Servicio finalizado correctamente"
        );
        
        binding.txtInstructions.setText("✅ El servicio ha sido marcado como completado.");
        
        Snackbar.make(binding.getRoot(), 
            "✓ Check-out exitoso. Tour finalizado.", 
            Snackbar.LENGTH_LONG).show();
            
        // Simular envío a backend
        simulateBackendUpdate(clientId, tourId);
    }

    private void manualCheckout() {
        // Opción manual en caso de problemas con QR
        String timestamp = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
        
        binding.txtScanResult.setText(
            "⚠️ CHECK-OUT MANUAL\n\n" +
            "Hora finalización: " + timestamp + "\n" +
            "Motivo: QR no disponible\n\n" +
            "Nota: Registrado como finalización manual"
        );
        
        binding.txtInstructions.setText("⚠️ Tour finalizado manualmente.");
        
        Snackbar.make(binding.getRoot(), 
            "⚠️ Check-out manual registrado", 
            Snackbar.LENGTH_LONG).show();
    }

    private void simulateBackendUpdate(String clientId, String tourId) {
        // En una implementación real, aquí enviarías la información al backend
        // Por ahora solo simulamos el proceso
        
        new android.os.Handler().postDelayed(() -> {
            runOnUiThread(() -> {
                binding.txtBackendStatus.setText(
                    "📡 Estado Backend:\n" +
                    "✓ Datos enviados al servidor\n" +
                    "✓ Tour marcado como completado\n" +
                    "✓ Pago procesado\n" +
                    "✓ Calificación habilitada para cliente"
                );
                
                Toast.makeText(this, "Sincronización completada ✓", Toast.LENGTH_SHORT).show();
            });
        }, 1500);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.txtCameraStatus.setText("Cámara: Habilitada ✓");
            } else {
                binding.txtCameraStatus.setText("Cámara: Permiso denegado ❌");
                Snackbar.make(binding.getRoot(), 
                    "Permiso de cámara necesario para escanear QR", 
                    Snackbar.LENGTH_LONG).show();
            }
        }
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
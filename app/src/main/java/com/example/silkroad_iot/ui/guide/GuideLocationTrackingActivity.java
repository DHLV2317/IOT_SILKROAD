package com.example.silkroad_iot.ui.guide;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.silkroad_iot.databinding.ActivityGuideLocationTrackingBinding;
import com.google.android.material.snackbar.Snackbar;

public class GuideLocationTrackingActivity extends AppCompatActivity {

    private ActivityGuideLocationTrackingBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    // Coordenadas estáticas simuladas para demo
    private final double[] demoLatitudes = {-12.0464, -12.0431, -12.0397, -12.0375};
    private final double[] demoLongitudes = {-77.0428, -77.0282, -77.0351, -77.0624};
    private final String[] demoStops = {
        "Plaza de Armas - Lima Centro",
        "Catedral de Lima", 
        "Palacio de Gobierno",
        "Museo Larco"
    };
    private int currentStopIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuideLocationTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Registrar Ubicación");
        }

        setupViews();
        checkLocationPermission();
    }

    private void setupViews() {
        // Mostrar primera parada
        updateCurrentStop();
        
        // Configurar botones
        binding.btnRegisterLocation.setOnClickListener(v -> registerCurrentLocation());
        binding.btnNextStop.setOnClickListener(v -> goToNextStop());
        binding.btnFinishTour.setOnClickListener(v -> finishTour());
    }

    private void updateCurrentStop() {
        if (currentStopIndex < demoStops.length) {
            binding.txtCurrentStop.setText("Parada actual: " + demoStops[currentStopIndex]);
            binding.txtStopCounter.setText("Parada " + (currentStopIndex + 1) + " de " + demoStops.length);
            
            // Mostrar coordenadas de demo
            binding.txtDemoCoordinates.setText(
                String.format("Coord. Demo: %.4f, %.4f", 
                demoLatitudes[currentStopIndex], 
                demoLongitudes[currentStopIndex])
            );
            
            binding.btnNextStop.setVisibility(View.VISIBLE);
            binding.btnFinishTour.setVisibility(currentStopIndex == demoStops.length - 1 ? View.VISIBLE : View.GONE);
        } else {
            binding.txtCurrentStop.setText("¡Tour completado!");
            binding.btnNextStop.setVisibility(View.GONE);
            binding.btnFinishTour.setVisibility(View.VISIBLE);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            binding.txtGpsStatus.setText("GPS: Habilitado ✓");
        }
    }

    private void registerCurrentLocation() {
        if (currentStopIndex >= demoStops.length) {
            Toast.makeText(this, "Tour ya completado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Para demo, usamos coordenadas estáticas
        double lat = demoLatitudes[currentStopIndex];
        double lng = demoLongitudes[currentStopIndex];
        String timestamp = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
        
        // Simular registro de ubicación
        String locationInfo = String.format(
            "📍 Ubicación registrada:\n" +
            "Parada: %s\n" +
            "Lat: %.6f\n" +
            "Lng: %.6f\n" +
            "Hora: %s",
            demoStops[currentStopIndex], lat, lng, timestamp
        );
        
        binding.txtLocationHistory.append(locationInfo + "\n\n");
        
        Snackbar.make(binding.getRoot(), 
            "✓ Ubicación registrada en " + demoStops[currentStopIndex], 
            Snackbar.LENGTH_LONG).show();
        
        // Habilitar botón siguiente parada
        binding.btnNextStop.setEnabled(true);
    }

    private void goToNextStop() {
        if (currentStopIndex < demoStops.length - 1) {
            currentStopIndex++;
            updateCurrentStop();
            binding.btnNextStop.setEnabled(false); // Requiere registrar ubicación primero
        }
    }

    private void finishTour() {
        Snackbar.make(binding.getRoot(), 
            "🎉 ¡Tour finalizado! Proceda con el escaneo QR del cliente.", 
            Snackbar.LENGTH_LONG).show();
            
        // En una implementación real, aquí guardarías toda la información del tour
        new android.os.Handler().postDelayed(() -> finish(), 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.txtGpsStatus.setText("GPS: Habilitado ✓");
            } else {
                binding.txtGpsStatus.setText("GPS: Permiso denegado ❌");
                Snackbar.make(binding.getRoot(), 
                    "Permiso de ubicación necesario para registrar paradas", 
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
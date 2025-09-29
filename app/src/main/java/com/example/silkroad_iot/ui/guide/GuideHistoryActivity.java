package com.example.silkroad_iot.ui.guide;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.databinding.ActivityGuideHistoryBinding;

public class GuideHistoryActivity extends AppCompatActivity {

    private ActivityGuideHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuideHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Historial de Tours");
        }

        generateDetailedHistory();
    }

    private void generateDetailedHistory() {
        String detailedHistory = 
            "═══════════════════════════════════════\n" +
            "📅 SEPTIEMBRE 2025\n" +
            "═══════════════════════════════════════\n\n" +
            
            "🗓️ 25 Sep 2025 - COMPLETADO\n" +
            "📍 City Tour Lima Colonial\n" +
            "🏢 Turismo Lima S.A.C.\n" +
            "👥 Grupo: 8 personas\n" +
            "💰 Pago: S/ 150\n" +
            "⭐ Calificación: 5.0/5.0\n" +
            "💬 \"Excelente guía, muy conocedor de la historia\"\n" +
            "⏰ Duración: 4h 30min\n\n" +
            
            "🗓️ 23 Sep 2025 - COMPLETADO\n" +
            "📍 Tour Gastronómico Barranco\n" +
            "🏢 Lima Foodie Tours\n" +
            "👥 Grupo: 6 personas\n" +
            "💰 Pago: S/ 180\n" +
            "⭐ Calificación: 4.5/5.0\n" +
            "💬 \"Muy buena experiencia culinaria\"\n" +
            "⏰ Duración: 3h 15min\n\n" +
            
            "🗓️ 20 Sep 2025 - COMPLETADO\n" +
            "📍 Machu Picchu Full Day\n" +
            "🏢 Aventuras Cusco EIRL\n" +
            "👥 Grupo: 12 personas\n" +
            "💰 Pago: S/ 400\n" +
            "⭐ Calificación: 5.0/5.0\n" +
            "💬 \"Increíble experiencia, guía muy profesional\"\n" +
            "⏰ Duración: 14h 00min\n\n" +
            
            "🗓️ 18 Sep 2025 - COMPLETADO\n" +
            "📍 Valle Sagrado de los Incas\n" +
            "🏢 InkaTrek Perú\n" +
            "👥 Grupo: 10 personas\n" +
            "💰 Pago: S/ 250\n" +
            "⭐ Calificación: 4.8/5.0\n" +
            "💬 \"Muy informativo y entretenido\"\n" +
            "⏰ Duración: 8h 45min\n\n" +
            
            "🗓️ 15 Sep 2025 - COMPLETADO\n" +
            "📍 Líneas de Nazca (Sobrevuelo)\n" +
            "🏢 Nazca Explorer\n" +
            "👥 Grupo: 4 personas\n" +
            "💰 Pago: S/ 500\n" +
            "⭐ Calificación: 4.9/5.0\n" +
            "💬 \"Experiencia única, guía muy preparado\"\n" +
            "⏰ Duración: 6h 30min\n\n" +
            
            "═══════════════════════════════════════\n" +
            "📅 AGOSTO 2025\n" +
            "═══════════════════════════════════════\n\n" +
            
            "🗓️ 28 Ago 2025 - COMPLETADO\n" +
            "📍 Circuito Mágico del Agua\n" +
            "🏢 Lima Adventures\n" +
            "👥 Grupo: 15 personas\n" +
            "💰 Pago: S/ 120\n" +
            "⭐ Calificación: 4.6/5.0\n" +
            "⏰ Duración: 3h 00min\n\n" +
            
            "🗓️ 25 Ago 2025 - COMPLETADO\n" +
            "📍 Islas Ballestas\n" +
            "🏢 Paracas Tours\n" +
            "👥 Grupo: 18 personas\n" +
            "💰 Pago: S/ 200\n" +
            "⭐ Calificación: 4.7/5.0\n" +
            "⏰ Duración: 5h 15min\n\n" +
            
            "═══════════════════════════════════════\n" +
            "📊 RESUMEN ESTADÍSTICO\n" +
            "═══════════════════════════════════════\n\n" +
            
            "📈 Total tours completados: 23\n" +
            "💰 Ingresos totales: S/ 4,850\n" +
            "⭐ Calificación promedio: 4.8/5.0\n" +
            "👥 Turistas atendidos: 186\n" +
            "⏱️ Horas de servicio: 156h\n" +
            "🏆 Tours con 5 estrellas: 65%\n" +
            "📍 Destinos visitados: 15\n" +
            "🏢 Empresas colaboradoras: 8\n\n" +
            
            "🎯 METAS CUMPLIDAS:\n" +
            "✅ +20 tours este mes\n" +
            "✅ Calificación >4.5\n" +
            "✅ Ingresos >S/4,000\n" +
            "✅ 0 cancelaciones\n" +
            "✅ 100% puntualidad\n\n";

        binding.txtFullHistory.setText(detailedHistory);
        
        // Estadísticas adicionales
        binding.txtMonthlyStats.setText(
            "📊 ESTADÍSTICAS DE SEPTIEMBRE:\n\n" +
            "• Tours realizados: 7\n" +
            "• Ingresos del mes: S/ 1,480\n" +
            "• Promedio por tour: S/ 211\n" +
            "• Mejor calificación: 5.0/5.0\n" +
            "• Turistas atendidos: 58\n" +
            "• Empresas trabajadas: 5"
        );
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
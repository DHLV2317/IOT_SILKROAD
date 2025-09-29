package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.databinding.ActivitySuperadminDetallesLogBinding;
import com.example.silkroad_iot.databinding.ActivitySuperadminDetallesReporteBinding;
import com.example.silkroad_iot.ui.superadmin.entity.ReporteDTO;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class DetallesReporteActivity extends AppCompatActivity {

    ActivitySuperadminDetallesReporteBinding binding;
    LineChart mpLineChart;
    private int posicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminDetallesReporteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        posicion = intent.getIntExtra("posicion", -1);
        ReporteDTO reporte = (ReporteDTO) intent.getSerializableExtra("reporte");

        mpLineChart=binding.lchart;
        LineDataSet lineDataSet1 = new LineDataSet(getEntries(), "Últimos 10 días");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        LineData lineData = new LineData(dataSets);
        mpLineChart.setData(lineData);
        mpLineChart.invalidate();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_download, menu);
        return true;
    }

    private ArrayList<Entry> getEntries() {
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 1f));
        entries.add(new Entry(1, 2f));
        entries.add(new Entry(2, 3f));
        entries.add(new Entry(3, 4f));
        entries.add(new Entry(4, 5f));
        entries.add(new Entry(5, 6f));
        entries.add(new Entry(6, 7f));
        entries.add(new Entry(7, 8f));
        entries.add(new Entry(8, 9f));
        entries.add(new Entry(9, 10f));
        entries.add(new Entry(10, 11f));
        return entries;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.descargar_excel) { // Reemplaza con tu ID real
            descargarExcel();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void descargarExcel() {}
     /*   ArrayList<Entry> dataEntries = getEntries();
        if (dataEntries == null || dataEntries.isEmpty()) {
            Toast.makeText(this, "No hay datos para exportar", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // Tipo MIME para .xlsx
        intent.putExtra(Intent.EXTRA_TITLE, "reporte_datos.xlsx");
        try {
            startActivityForResult(intent, CREATE_FILE_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "No se puede iniciar la selección de archivo. ¿Tienes una app de manejo de archivos?", Toast.LENGTH_LONG).show();
            Log.e("DetallesReporte", "Error al iniciar ACTION_CREATE_DOCUMENT", e);
        }
    }*/
}
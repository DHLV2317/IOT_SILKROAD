package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.databinding.ActivitySuperadminDetallesReporteBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class DetallesReporteActivity extends AppCompatActivity {

    private ActivitySuperadminDetallesReporteBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminDetallesReporteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();
        // puedes mostrar titulo/descripcion en tu layout si existen TextViews
        LineChart chart = binding.lchart;
        LineDataSet ds = new LineDataSet(getEntries(), "Últimos 10 días");
        chart.setData(new LineData(ds));
        chart.invalidate();
    }

    private ArrayList<Entry> getEntries() {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int x=0; x<=10; x++) entries.add(new Entry(x, x+1f));
        return entries;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_download, menu);
        return true;
    }
}
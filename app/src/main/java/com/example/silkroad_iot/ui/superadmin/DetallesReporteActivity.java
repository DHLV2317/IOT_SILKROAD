package com.example.silkroad_iot.ui.superadmin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.EmpresaFb;
import com.example.silkroad_iot.databinding.ActivitySuperadminDetallesReporteBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.firebase.Timestamp;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class DetallesReporteActivity extends AppCompatActivity {

    private ActivitySuperadminDetallesReporteBinding binding;
    private FirebaseFirestore db;
    private String id="1";
    private String TAG = "DetallesReporteActivity";
    private HashMap<Timestamp, Double> data = new HashMap<>();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminDetallesReporteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        setSupportActionBar(binding.toolbar3);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        Intent i = getIntent();
        EmpresaFb empresa = (EmpresaFb) i.getSerializableExtra("empresa");
        if (empresa != null) {
            id = empresa.getId();
            // Opcional: Mostrar nombre de la empresa en la toolbar
            getSupportActionBar().setTitle("Reporte: " + empresa.getNombre());
        }

        // Necesaria...
        // 1. Iniciar la cadena de llamadas asíncronas
        obtenerIdsTours(ids -> {
            // 2. Este bloque se ejecuta cuando los IDs de los tours están listos
            cargarDatosReporte(ids, data -> {
                // 3. ¡Este bloque se ejecuta cuando TODOS los datos del historial están listos!
                // Ahora es seguro procesarlos y actualizar la UI.

                // Procesar los datos para obtener los puntos del gráfico
                LineDataSet ds1 = new LineDataSet(getEntriesLast7Days(data), "Últimos 7 días");
                ds1.setColor(Color.BLUE);
                ds1.setCircleColor(Color.BLUE);

                LineDataSet ds2 = new LineDataSet(getEntriesLast7Months(data), "Últimos 7 meses");
                ds2.setColor(Color.RED);
                ds2.setCircleColor(Color.RED);

                LineDataSet ds3 = new LineDataSet(getEntriesLast7Years(data), "Últimos 7 años");
                ds3.setColor(Color.GREEN);
                ds3.setCircleColor(Color.GREEN);

                // Configurar el gráfico
                LineData lineData= new LineData(ds1, ds2, ds3);
                //LineData lineData= new LineData(ds1);
                LineChart chart = binding.lchart;
                chart.setData(lineData);

                // Añadir más configuración al gráfico (descripción, ejes, etc.)
                chart.getDescription().setText("Ingresos por periodo");
                chart.invalidate(); // Refrescar el gráfico
            });
        });




        /*puedes mostrar titulo/descripcion en tu layout si existen TextViews
        LineChart chart = binding.lchart;
        //LineDataSet ds1 = new LineDataSet(getEntries7days(ids), "Últimos 7 días");
        //ds1.setColor(Color.BLUE);

        //LineDataSet ds2 = new LineDataSet(getEntries7months(), "Últimos 7 meses");
        //ds2.setColor(Color.RED);

        //LineDataSet ds3 = new LineDataSet(getEntries7years(), "Últimos 7 años");
        //ds2.setColor(Color.RED);

        //LineData lineData= new LineData(ds1, ds2, ds3);
        //LineData lineData= getEntries7(ids);;

        chart.setData(lineData);
        chart.invalidate();

        setSupportActionBar(binding.toolbar3);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

    }

    /*private ArrayList<String> obtenerIdsTours(){
        ArrayList<String> ids = new ArrayList<>();
        db.collection("tours")
                .whereEqualTo("id", id)
                .get()
                .addOnSuccessListener(snap -> {
                    for (QueryDocumentSnapshot d : snap){
                        ids.add(d.getId());
                    }
                });
        return ids;
    }*/

    private void obtenerIdsTours(FirestoreCallback firestoreCallback){
        db.collection("tours")
                .whereEqualTo("empresaId", id) // Asumo que el campo es 'id_empresa', ajústalo si es diferente
                .get()
                .addOnSuccessListener(snap -> {
                    ArrayList<String> ids = new ArrayList<>();
                    for (QueryDocumentSnapshot d : snap){
                        ids.add(d.getId());
                    }
                    // Llama al callback con los IDs obtenidos
                    firestoreCallback.onCallback(ids);
                })
                .addOnFailureListener(e -> {
                    // Maneja el error, quizás mostrando un Toast
                    Toast.makeText(this, "Error al obtener tours: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    firestoreCallback.onCallback(new ArrayList<>()); // Llama con lista vacía para no bloquear
                });
    }

    private interface FirestoreCallback {
        void onCallback(ArrayList<String> ids);
    }
    /*private LineData getEntries7(ArrayList<String> ids) {
        ArrayList<Entry> entries = new ArrayList<>();
        Timestamp timestampFromDate = new Timestamp(new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));

        for(int i=0; i<ids.size(); i++){
            db.collection("tours_history")
                    .whereEqualTo("id_tour", ids.get(i))
                    .whereEqualTo("estado", "finalizada")
                    .get()
                    .addOnSuccessListener(snap -> {
                        for (QueryDocumentSnapshot d : snap) {
                            d.getTimestamp("fechaReserva");
                            if(d.getDouble("precio")!=null)
                                data.put(d.getTimestamp("fechaReserva"), d.getDouble("precio"));
                            else
                                data.put(d.getTimestamp("fechaReserva"), 1.0);
                        }
                    });
        }


        //LineDataSet ds1 = new LineDataSet(getEntries7days(), "Últimos 7 días");
        //ds1.setColor(Color.BLUE);

        //LineDataSet ds2 = new LineDataSet(getEntries7months(), "Últimos 7 meses");
        //ds2.setColor(Color.RED);

        //LineDataSet ds3 = new LineDataSet(getEntries7years(), "Últimos 7 años");
        //ds2.setColor(Color.RED);
        LineData lineData= new LineData();
        return lineData;
    }*/

    // Modifica getEntries7 (ahora lo llamaremos cargarDatosReporte)
    private void cargarDatosReporte(ArrayList<String> ids, final DataCallback callback) {
        // Si no hay tours, no hay nada que buscar.
        if (ids == null || ids.isEmpty()) {
            callback.onDataLoaded(new HashMap<>()); // Devuelve data vacía
            return;
        }

        HashMap<Timestamp, Double> data = new HashMap<>();
        final int[] tasksCompleted = {0}; // Contador para saber cuándo han terminado todas las tareas

        for(String tourId : ids){
            //Log.d(TAG, "Tour ID: " + tourId);
            db.collection("tours_history")
                    .whereEqualTo("id_tour", tourId)
                    .whereEqualTo("estado", "finalizada")
                    .get()
                    .addOnCompleteListener(task -> { // Usamos onCompleteListener para manejar éxito y fallo
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot d : task.getResult()) {
                                Timestamp fecha = d.getTimestamp("fechaReserva");
                                Log.d(TAG, "Fecha: " + fecha);
                                Double fix = d.getDouble("pax");//XD funciona pax pero no precio ffffff
                                if (fecha != null) {
                                    data.put(fecha, (fix != null) ? fix : 0.0);
                                    Log.d(TAG, "Precio: " + fix);
                                }
                            }
                        }
                        tasksCompleted[0]++;
                        if (tasksCompleted[0] == ids.size()) {
                            callback.onDataLoaded(data);
                        }
                    });
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_download, menu);
        return true;
    }

    public interface DataCallback {
        void onDataLoaded(HashMap<Timestamp, Double> data);
    }

    private ArrayList<Entry> getEntriesLast7Days(HashMap<Timestamp, Double> data) {
        // aMapa para acumular los montos por día
        Map<LocalDate, Double> dailyTotals = new TreeMap<>();
        LocalDate today = LocalDate.now();
        // Inicializar los últimos 7 días con un monto de 0.0
        for (int i = 0; i < 7; i++) {
            dailyTotals.put(today.minusDays(i), 0.0);
        }

        // Acumular los montos del HashMap 'data'
        for (Map.Entry<Timestamp, Double> entry : data.entrySet()) {
            LocalDate entryDate = entry.getKey().toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (dailyTotals.containsKey(entryDate)) {
                dailyTotals.put(entryDate, dailyTotals.get(entryDate) + entry.getValue());
            }
        }

        // Convertir el mapa de totales a Entries para el gráfico
        ArrayList<Entry> entries = new ArrayList<>();
        int dayIndex = 0;
        for (LocalDate date : dailyTotals.keySet().stream().sorted().collect(Collectors.toList())) {
            // El eje X puede ser simplemente un índice (0 a 6)
            entries.add(new Entry(dayIndex, dailyTotals.get(date).floatValue()));
            dayIndex++;
        }
        return entries;
    }


    private ArrayList<Entry> getEntriesLast7Months(HashMap<Timestamp, Double> data) {
        Map<YearMonth, Double> monthlyTotals = new TreeMap<>();
        YearMonth currentMonth = YearMonth.now();

        for (int i = 0; i < 7; i++) {
            monthlyTotals.put(currentMonth.minusMonths(i), 0.0);
        }

        for (Map.Entry<Timestamp, Double> entry : data.entrySet()) {
            YearMonth entryMonth = YearMonth.from(entry.getKey().toDate().toInstant().atZone(ZoneId.systemDefault()));
            if (monthlyTotals.containsKey(entryMonth)) {
                monthlyTotals.put(entryMonth, monthlyTotals.get(entryMonth) + entry.getValue());
            }
        }

        ArrayList<Entry> entries = new ArrayList<>();
        int monthIndex = 0;
        // Iteramos en orden natural (del más antiguo al más nuevo) para el gráfico
        for (Map.Entry<YearMonth, Double> entry : monthlyTotals.entrySet()) {
            entries.add(new Entry(monthIndex, entry.getValue().floatValue()));
            monthIndex++;
        }
        return entries;
    }

    private ArrayList<Entry> getEntriesLast7Years(HashMap<Timestamp, Double> data) {
        Map<Integer, Double> yearlyTotals = new TreeMap<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = 0; i < 7; i++) {
            yearlyTotals.put(currentYear - i, 0.0);
        }

        for (Map.Entry<Timestamp, Double> entry : data.entrySet()) {
            int entryYear = entry.getKey().toDate().toInstant().atZone(ZoneId.systemDefault()).getYear();
            if (yearlyTotals.containsKey(entryYear)) {
                yearlyTotals.put(entryYear, yearlyTotals.get(entryYear) + entry.getValue());
            }
        }

        ArrayList<Entry> entries = new ArrayList<>();
        int yearIndex = 0;
        for (Map.Entry<Integer, Double> entry : yearlyTotals.entrySet()) {
            entries.add(new Entry(yearIndex, entry.getValue().floatValue()));
            yearIndex++;
        }
        return entries;
    }



}
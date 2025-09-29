package com.example.silkroad_iot.ui.admin;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.AdminRepository;
import com.example.silkroad_iot.ui.common.BaseDrawerActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class AdminReportsActivity extends BaseDrawerActivity {

    private final AdminRepository repo = AdminRepository.get();

    private View rootReports;
    private TextView tIncome, tReservations, tTopService;
    private TextView tBar1, tBar2, tBar3, tBar4;
    private View barFill1, barFill2, barFill3, barFill4;
    private LinearLayout boxTopTours;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Monta el content dentro del drawer
        setupDrawer(R.layout.content_admin_reports, R.menu.menu_drawer_admin, "Reportes");

        // Bind
        rootReports   = findViewById(R.id.rootReports);
        tIncome       = findViewById(R.id.tIncome);
        tReservations = findViewById(R.id.tReservations);
        tTopService   = findViewById(R.id.tTopService);
        tBar1 = findViewById(R.id.tBar1); tBar2 = findViewById(R.id.tBar2);
        tBar3 = findViewById(R.id.tBar3); tBar4 = findViewById(R.id.tBar4);
        barFill1 = findViewById(R.id.barFill1); barFill2 = findViewById(R.id.barFill2);
        barFill3 = findViewById(R.id.barFill3); barFill4 = findViewById(R.id.barFill4);
        boxTopTours   = findViewById(R.id.boxTopTours);

        // Datos base (mock/derivados)
        AdminRepository.ReportSummary s = repo.getReportSummary();
        tIncome.setText("Ingresos: S/ " + new DecimalFormat("#,##0.00").format(s.totalRevenue));
        tReservations.setText("Reservas: " + s.reservations);
        tTopService.setText("Servicio top: " + s.topService);

        // Construir ranking por tour a partir de reservas reales
        List<AdminRepository.Reservation> rs = repo.getReservations();
        HashMap<String, Agg> map = new HashMap<>();
        for (AdminRepository.Reservation r : rs) {
            String tourName = (r.tour == null || r.tour.name == null) ? "(Sin tour)" : r.tour.name;
            Agg a = map.getOrDefault(tourName, new Agg());
            a.count += 1;
            a.revenue += (r.tour == null ? 0 : (r.tour.price * r.people));
            map.put(tourName, a);
        }
        // Pasar a lista ordenada
        List<Row> rows = new ArrayList<>();
        for (String k : map.keySet()) rows.add(new Row(k, map.get(k).revenue, map.get(k).count));
        Collections.sort(rows, Comparator.comparingDouble((Row r) -> r.revenue).reversed());

        // Pintar barras (top 4 como porcentaje del total)
        double total = 0;
        for (Row r : rows) total += r.revenue;
        List<Row> top4 = rows.size() > 4 ? rows.subList(0, 4) : rows;
        while (top4.size() < 4) top4.add(new Row("—", 0, 0)); // completar 4

        setBar(top4.get(3), total, barFill1, tBar1); // menor → mayor
        setBar(top4.get(2), total, barFill2, tBar2);
        setBar(top4.get(1), total, barFill3, tBar3);
        setBar(top4.get(0), total, barFill4, tBar4);

        // Lista top (máx 5)
        boxTopTours.removeAllViews();
        int max = Math.min(5, rows.size());
        for (int i = 0; i < max; i++) {
            Row r = rows.get(i);
            TextView item = new TextView(this);
            item.setText((i+1) + ". " + r.name + "   S/ " +
                    new DecimalFormat("#,##0.00").format(r.revenue) +
                    "   " + r.count + " reservas");
            item.setPadding(12, 10, 12, 10);
            boxTopTours.addView(item);
        }

        // Exportar a PDF
        findViewById(R.id.btnExportPdf).setOnClickListener(v -> exportToPdf(rootReports));
    }

    @Override protected int defaultMenuId() { return R.id.m_reports; }

    // ===== Helpers =====
    private void setBar(Row r, double total, View barFill, TextView label){
        int chartHeight = dp(160);  // alto máx de barra
        double pct = (total <= 0) ? 0 : (r.revenue * 100.0 / total);
        int h = (int) Math.round(chartHeight * pct / 100.0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, h);
        barFill.setLayoutParams(lp);
        label.setText((r.name.equals("—") ? "—" : (Math.round(pct) + "%")));
    }

    private void exportToPdf(View content){
        try {
            // 1) Renderizar el ScrollView a bitmap
            Bitmap bmp = getBitmapFromView(content);
            // 2) Crear documento
            PdfDocument doc = new PdfDocument();
            PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(bmp.getWidth(), bmp.getHeight(), 1).create();
            PdfDocument.Page page = doc.startPage(info);
            page.getCanvas().drawBitmap(bmp, 0, 0, null);
            doc.finishPage(page);

            // 3) Guardar (carpeta privada de la app)
            File out = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                    "reporte_admin.pdf");
            FileOutputStream fos = new FileOutputStream(out);
            doc.writeTo(fos);
            fos.flush(); fos.close();
            doc.close();

            android.widget.Toast.makeText(this, "PDF guardado en: " + out.getAbsolutePath(),
                    android.widget.Toast.LENGTH_LONG).show();
        } catch (Exception e){
            android.widget.Toast.makeText(this, "Error al exportar PDF", android.widget.Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap getBitmapFromView(View v){
        int w = v.getWidth();
        int h = v.getHeight();
        if (w == 0 || h == 0) {
            // forzar medida si aún no está layouted
            int specW = View.MeasureSpec.makeMeasureSpec(getResources().getDisplayMetrics().widthPixels, View.MeasureSpec.EXACTLY);
            int specH = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            v.measure(specW, specH);
            w = v.getMeasuredWidth();
            h = v.getMeasuredHeight();
            v.layout(0, 0, w, h);
        }
        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    private int dp(int v){ return Math.round(getResources().getDisplayMetrics().density * v); }

    // structs simples
    private static class Agg { double revenue = 0; int count = 0; }
    private static class Row {
        String name; double revenue; int count;
        Row(String n, double r, int c){ name=n; revenue=r; count=c; }
    }
}
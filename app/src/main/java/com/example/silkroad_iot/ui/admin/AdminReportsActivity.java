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
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdminReportsActivity extends BaseDrawerActivity {

    private final AdminRepository repo = AdminRepository.get();

    private View rootReports;
    private TextView tIncome, tReservations, tTopService, tChartTitle;
    private TextView tBar1, tBar2, tBar3, tBar4, tLegend1, tLegend2, tLegend3, tLegend4;
    private View barFill1, barFill2, barFill3, barFill4;
    private LinearLayout boxTopTours;

    private final DecimalFormat money = new DecimalFormat("#,##0.00");

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.content_admin_reports, R.menu.menu_drawer_admin, "Reportes");

        rootReports   = findViewById(R.id.rootReports);
        tIncome       = findViewById(R.id.tIncome);
        tReservations = findViewById(R.id.tReservations);
        tTopService   = findViewById(R.id.tTopService);
        tChartTitle   = findViewById(R.id.tChartTitle);

        tBar1 = findViewById(R.id.tBar1); tBar2 = findViewById(R.id.tBar2);
        tBar3 = findViewById(R.id.tBar3); tBar4 = findViewById(R.id.tBar4);
        barFill1 = findViewById(R.id.barFill1); barFill2 = findViewById(R.id.barFill2);
        barFill3 = findViewById(R.id.barFill3); barFill4 = findViewById(R.id.barFill4);

        // Nombres debajo de cada barra
        tLegend1 = findViewById(R.id.tLegend1);
        tLegend2 = findViewById(R.id.tLegend2);
        tLegend3 = findViewById(R.id.tLegend3);
        tLegend4 = findViewById(R.id.tLegend4);

        boxTopTours   = findViewById(R.id.boxTopTours);

        // Resumen
        AdminRepository.ReportSummary s = repo.getReportSummary();
        tIncome.setText("Ingresos: S/ " + money.format(s.totalRevenue));
        tReservations.setText("Reservas: " + s.reservations);
        tTopService.setText("Servicio top: " + s.topService);

        // Ranking tours
        List<AdminRepository.Reservation> rs = repo.getReservations();
        HashMap<String, Agg> map = new HashMap<>();
        for (AdminRepository.Reservation r : rs) {
            String tourName = safeStr(obj(r, "tour"), "name", "nombre", "(Sin tour)");
            double tourPrice = safeNum(obj(r, "tour"), 0d, "price", "precio").doubleValue();
            int pax = safeNum(r, 1, "people", "cantidad_personas").intValue();

            Agg a = map.getOrDefault(tourName, new Agg());
            a.count += 1;
            a.revenue += (tourPrice * pax);
            map.put(tourName, a);
        }

        List<Row> rows = new ArrayList<>();
        for (String k : map.keySet()) rows.add(new Row(k, map.get(k).revenue, map.get(k).count));
        Collections.sort(rows, Comparator.comparingDouble((Row r) -> r.revenue).reversed());

        double total = 0;
        for (Row r : rows) total += r.revenue;

        List<Row> top4 = rows.size() > 4 ? new ArrayList<>(rows.subList(0, 4)) : new ArrayList<>(rows);
        while (top4.size() < 4) top4.add(new Row("—", 0, 0));

        setBar(top4.get(3), total, barFill1, tBar1, tLegend1);
        setBar(top4.get(2), total, barFill2, tBar2, tLegend2);
        setBar(top4.get(1), total, barFill3, tBar3, tLegend3);
        setBar(top4.get(0), total, barFill4, tBar4, tLegend4);

        boxTopTours.removeAllViews();
        int max = Math.min(5, rows.size());
        for (int i = 0; i < max; i++) {
            Row r = rows.get(i);
            TextView item = new TextView(this);
            item.setText((i+1) + ". " + r.name + "   S/ " +
                    money.format(r.revenue) +
                    "   " + r.count + " reservas");
            item.setPadding(12, 10, 12, 10);
            boxTopTours.addView(item);
        }

        findViewById(R.id.btnExportPdf).setOnClickListener(v -> exportToPdf(rootReports));
    }

    @Override protected int defaultMenuId() { return R.id.m_reports; }

    private void setBar(Row r, double total, View barFill, TextView pctLabel, TextView legend){
        int chartHeight = dp(160);
        double pct = (total <= 0) ? 0 : (r.revenue * 100.0 / total);
        int h = (int) Math.round(chartHeight * pct / 100.0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, h);
        barFill.setLayoutParams(lp);
        pctLabel.setText(total <= 0 ? "0%" : (Math.round(pct) + "%"));
        legend.setText(r.name);
    }

    private void exportToPdf(View content){
        try {
            Bitmap bmp = getBitmapFromView(content);
            PdfDocument doc = new PdfDocument();
            PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(bmp.getWidth(), bmp.getHeight(), 1).create();
            PdfDocument.Page page = doc.startPage(info);
            page.getCanvas().drawBitmap(bmp, 0, 0, null);
            doc.finishPage(page);

            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File out = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "reporte_admin_" + ts + ".pdf");
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

    // ===== Helpers reflexión / fallbacks =====
    private static Object f(Object o, String n){
        if (o==null) return null;
        try { Field f=o.getClass().getDeclaredField(n); f.setAccessible(true); return f.get(o); }
        catch (Throwable ignore){ return null; }
    }
    private static Object obj(Object o, String n){ return f(o,n); }

    private static String str(Object o, String n){ Object v=f(o,n); return v==null? "": String.valueOf(v); }
    private static String safeStr(Object o, String primary, String alt, String def){
        String v1 = str(o, primary);
        if (!v1.isEmpty()) return v1;
        String v2 = str(o, alt);
        return v2.isEmpty()? def : v2;
    }

    private static Number safeNum(Object o, Number def, String... names){
        for (String n : names){
            Object v = f(o, n);
            if (v instanceof Number) return (Number) v;
            if (v != null){
                try { return Double.parseDouble(String.valueOf(v)); }
                catch (Exception ignored) {}
            }
        }
        return def;
    }

    private static class Agg { double revenue = 0; int count = 0; }
    private static class Row { String name; double revenue; int count; Row(String n, double r, int c){ name=n; revenue=r; count=c; } }
}
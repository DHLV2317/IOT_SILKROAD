package com.example.silkroad_iot.ui.admin;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminReservationsAdapter extends RecyclerView.Adapter<AdminReservationsAdapter.VH> {

    private final List<Object> all;
    private final List<Object> data;
    private final SimpleDateFormat sdf =
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private String statusFilter = "Todos";

    public AdminReservationsAdapter(List<?> items) {
        this.all  = new ArrayList<>(items);
        this.data = new ArrayList<>(items);
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tTitle, tSub, tDate, tStatus, btnDetail;
        VH(View v){
            super(v);
            img       = v.findViewById(R.id.aImg);
            tTitle    = v.findViewById(R.id.aTitle);
            tSub      = v.findViewById(R.id.aSubtitle);
            tDate     = v.findViewById(R.id.aDate);
            tStatus   = v.findViewById(R.id.aStatus);
            btnDetail = v.findViewById(R.id.btnDetail);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int vt){
        View v = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_admin_reservation, p, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i){
        Object r = data.get(i);

        // ------- datos principales (tour) -------
        String tourName = firstNonEmpty(
                str(r, "tourName"),
                str(obj(r, "tour"), "name"),
                str(obj(r, "tour"), "nombre")
        );

        // ------- cliente -------
        String clientName = firstNonEmpty(
                str(r, "clientName"),
                str(r, "clientEmail"),
                str(r, "id_usuario")
        );

        // ------- pax / total -------
        Number people = firstNum(
                num(r, "people"),
                num(r, "cantidad_personas"),
                num(obj(r, "tour"), "cantidad_personas")
        );

        Number total = firstNum(
                num(r, "total"),
                num(r, "precio"),
                num(obj(r, "tour"), "precio")
        );

        // ------- estado / fecha -------
        String status = firstNonEmpty(
                str(r, "status"),
                str(r, "estado")
        );

        Date date = firstDate(
                date(r, "date"),
                date(r, "fechaReserva"),
                date(r, "fecha_realizado")
        );

        // ------- imagen -------
        String imageUrl = firstNonEmpty(
                str(r, "imageUrl"),
                str(obj(r, "tour"), "imagen"),
                str(obj(r, "tour"), "imageUrl")
        );

        // ------- bind UI -------
        h.tTitle.setText(tourName.isEmpty() ? "(Sin tour)" : tourName);

        String paxText = (people == null ? "1" : String.valueOf(people)) + " pax";
        String money   = "S/ " + (total == null ? "0" : String.valueOf(total));
        h.tSub.setText(
                (clientName.isEmpty() ? "Cliente sin nombre" : clientName)
                        + " · " + paxText + " · " + money
        );

        h.tDate.setText(date == null ? "—" : sdf.format(date));
        if (status == null || status.trim().isEmpty()) status = "pendiente";
        h.tStatus.setText(status);

        int bg = R.color.pill_gray;
        String st = status.toLowerCase(Locale.getDefault());
        if (st.contains("check-in") || st.contains("check-out") || st.contains("final"))
            bg = R.color.teal_200;
        else if (st.contains("rech") || st.contains("cancel"))
            bg = android.R.color.holo_red_light;
        h.tStatus.setBackgroundResource(bg);

        if (imageUrl.isEmpty()){
            Glide.with(h.itemView)
                    .load(R.drawable.ic_menu_24)
                    .error(R.drawable.ic_menu_24)
                    .into(h.img);
        } else {
            Glide.with(h.itemView)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_menu_24)
                    .error(R.drawable.ic_menu_24)
                    .into(h.img);
        }

        // ------- botón detalle (pasamos el objeto entero) -------
        h.btnDetail.setOnClickListener(v -> {
            Intent it = new Intent(v.getContext(), AdminReservationDetailActivity.class);
            if (r instanceof Serializable) {
                it.putExtra("reserva", (Serializable) r);
            }
            v.getContext().startActivity(it);
        });
    }

    @Override public int getItemCount(){ return data.size(); }

    // =========================================================
    // Filtro texto + estado
    // =========================================================
    public void filter(String query, String status){
        String q  = query  == null ? "" : query.trim().toLowerCase(Locale.getDefault());
        String st = status == null ? "Todos" : status;

        data.clear();
        for (Object r : all){
            String tour = firstNonEmpty(
                    str(r, "tourName"),
                    str(obj(r, "tour"), "name"),
                    str(obj(r, "tour"), "nombre")
            ).toLowerCase(Locale.getDefault());

            String cli = firstNonEmpty(
                    str(r, "clientName"),
                    str(r, "clientEmail"),
                    str(r, "id_usuario")
            ).toLowerCase(Locale.getDefault());

            String s = firstNonEmpty(
                    str(r, "status"),
                    str(r, "estado")
            ).toLowerCase(Locale.getDefault());

            boolean matchText   = q.isEmpty() || tour.contains(q) || cli.contains(q);
            boolean matchStatus = st.equals("Todos") || s.equals(st.toLowerCase(Locale.getDefault()));

            if (matchText && matchStatus) data.add(r);
        }
        notifyDataSetChanged();
    }

    public void setStatusFilter(String status){
        this.statusFilter = status == null ? "Todos" : status;
    }

    public String getStatusFilter(){ return statusFilter; }

    public List<Object> getCurrentItems(){ return new ArrayList<>(data); }

    /** Reemplaza los datos al refrescar desde Firestore */
    public void replace(List<?> newItems) {
        all.clear();
        data.clear();
        all.addAll(newItems);
        data.addAll(newItems);
        notifyDataSetChanged();
    }

    // =========================================================
    // helpers reflexión/fallback
    // =========================================================
    private static Object f(Object o, String n){
        if (o==null) return null;
        try {
            Field f=o.getClass().getDeclaredField(n);
            f.setAccessible(true);
            return f.get(o);
        } catch (Throwable ignore){ return null; }
    }
    private static Object obj(Object o, String n){ return f(o,n); }
    private static String str(Object o, String n){
        Object v=f(o,n);
        return v==null? "" : String.valueOf(v);
    }
    private static Number num(Object o, String n){
        Object v=f(o,n);
        return (v instanceof Number)? (Number)v : null;
    }
    private static Date date(Object o, String n){
        Object v=f(o,n);
        return (v instanceof Date)? (Date)v : null;
    }

    private static String firstNonEmpty(String... vals){
        for (String s : vals){
            if (s != null && !s.trim().isEmpty()) return s;
        }
        return "";
    }
    private static Number firstNum(Number... nums){
        for (Number n : nums){ if (n != null) return n; }
        return null;
    }
    private static Date firstDate(Date... ds){
        for (Date d: ds){ if (d != null) return d; }
        return null;
    }
}
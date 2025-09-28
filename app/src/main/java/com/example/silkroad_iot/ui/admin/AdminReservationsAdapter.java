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

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminReservationsAdapter extends RecyclerView.Adapter<AdminReservationsAdapter.VH> {

    private final List<Object> all;
    private final List<Object> data;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private String statusFilter = "Todos";

    public AdminReservationsAdapter(List<?> items){
        this.all  = new ArrayList<>(items);
        this.data = new ArrayList<>(items);
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tTitle, tSub, tDate, tStatus, btnDetail;
        VH(View v){
            super(v);
            img     = v.findViewById(R.id.aImg);
            tTitle  = v.findViewById(R.id.aTitle);
            tSub    = v.findViewById(R.id.aSubtitle);
            tDate   = v.findViewById(R.id.aDate);
            tStatus = v.findViewById(R.id.aStatus);
            btnDetail = v.findViewById(R.id.btnDetail);
        }
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int vt){
        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_admin_reservation, p, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int i){
        Object r = data.get(i);

        String tourName   = str(r, "tourName");
        String clientName = str(r, "clientName");
        Number people     = num(r, "people");
        Number total      = num(r, "total");
        String status     = str(r, "status");
        Date   date       = date(r, "date");
        if (date == null) {
            Number ts = num(r, "date");
            if (ts != null) date = new Date(ts.longValue());
        }
        String imageUrl   = str(r, "imageUrl");

        h.tTitle.setText(tourName.isEmpty()? "(Sin tour)" : tourName);
        h.tSub.setText((clientName.isEmpty()? "Cliente sin nombre" : clientName) +
                " · " + (people==null? "1" : String.valueOf(people)) + " pax · S/ " +
                (total==null? "0" : String.valueOf(total)));
        h.tDate.setText(date==null? "—" : sdf.format(date));
        h.tStatus.setText(status.isEmpty()? "pendiente" : status);

        int bg = R.color.pill_gray;
        String st = status == null ? "" : status.toLowerCase(Locale.getDefault());
        if (st.contains("check-in")) bg = R.color.teal_200;
        else if (st.contains("check-out")) bg = R.color.teal_200;
        else if (st.contains("final")) bg = R.color.teal_200;
        else if (st.contains("rech") || st.contains("cancel")) bg = android.R.color.holo_red_light;
        h.tStatus.setBackgroundResource(bg);

        if (imageUrl.isEmpty()){
            Glide.with(h.itemView).load(R.drawable.ic_menu_24)
                    .error(R.drawable.ic_menu_24).into(h.img);
        } else {
            Glide.with(h.itemView).load(imageUrl)
                    .placeholder(R.drawable.ic_menu_24)
                    .error(R.drawable.ic_menu_24).into(h.img);
        }

        h.btnDetail.setOnClickListener(v -> {
            Intent it = new Intent(v.getContext(), AdminReservationDetailActivity.class);
            it.putExtra("index", h.getBindingAdapterPosition());
            v.getContext().startActivity(it);
        });
    }

    @Override public int getItemCount(){ return data.size(); }

    /** Filtro combinado por texto y estado */
    public void filter(String query, String status){
        String q = query == null ? "" : query.trim().toLowerCase(Locale.getDefault());
        String st = status == null ? "Todos" : status;

        data.clear();
        for (Object r : all){
            String tour = str(r, "tourName").toLowerCase(Locale.getDefault());
            String cli  = str(r, "clientName").toLowerCase(Locale.getDefault());
            String s    = str(r, "status").toLowerCase(Locale.getDefault());

            boolean matchText = q.isEmpty() || tour.contains(q) || cli.contains(q);
            boolean matchStatus = st.equals("Todos") || s.equals(st.toLowerCase(Locale.getDefault()));

            if (matchText && matchStatus) data.add(r);
        }
        notifyDataSetChanged();
    }

    public void setStatusFilter(String status){
        this.statusFilter = status == null ? "Todos" : status;
    }

    public String getStatusFilter(){ return statusFilter; }

    /** Opcional: para generar el PDF con la lista filtrada actual */
    public List<Object> getCurrentItems(){ return new ArrayList<>(data); }

    // ------- helpers reflexión segura -------
    private static Object f(Object o, String n){
        if (o==null) return null;
        try { Field f=o.getClass().getDeclaredField(n); f.setAccessible(true); return f.get(o); }
        catch (Throwable ignore){ return null; }
    }
    private static String str(Object o, String n){ Object v=f(o,n); return v==null? "": String.valueOf(v); }
    private static Number num(Object o, String n){ Object v=f(o,n); return (v instanceof Number)? (Number)v : null; }
    private static Date date(Object o, String n){ Object v=f(o,n); return (v instanceof Date)? (Date)v : null; }
}
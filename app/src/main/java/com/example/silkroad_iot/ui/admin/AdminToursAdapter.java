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

public class AdminToursAdapter extends RecyclerView.Adapter<AdminToursAdapter.VH> {

    private final List<Object> all;
    private final List<Object> data;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public AdminToursAdapter(List<?> items){
        this.all  = new ArrayList<>(items);
        this.data = new ArrayList<>(items);
        setHasStableIds(true);
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tTitle, tSubtitle, tDate, btnDetail;
        ImageView img;
        VH(@NonNull View v){
            super(v);
            tTitle    = v.findViewById(R.id.aTitle);
            tSubtitle = v.findViewById(R.id.aSubtitle);
            tDate     = v.findViewById(R.id.aDate);
            btnDetail = v.findViewById(R.id.btnDetail);
            img       = v.findViewById(R.id.aImg);
        }
    }

    @Override public long getItemId(int position) { return position; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int vt){
        View v = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_admin_tour, p, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i){
        Object t = data.get(i);

        String name   = str(t, "name");
        Double price  = dbl(t, "price");
        Integer ppl   = integer(t, "people");

        Date df = date(t, "dateFrom");
        Date dt = date(t, "dateTo");
        Date single = date(t, "FechaTour");
        if (single == null) single = date(t, "date");

        String fecha = (df != null && dt != null)
                ? (sdf.format(df) + " - " + sdf.format(dt))
                : (single != null ? sdf.format(single) : "sin fecha");

        String desc   = str(t, "description");
        String imgUrl = str(t, "imageUrl");

        if (name.isEmpty()) name = "Sin nombre";
        if (price == null)  price = 0.0;
        if (ppl == null)     ppl = 0;

        h.tTitle.setText(name);
        String meta = "S/ " + price + " · " + ppl + " personas";
        h.tSubtitle.setText(desc.isEmpty() ? meta : (meta + " · " + desc));
        h.tDate.setText(fecha);

        if (imgUrl.isEmpty()) {
            h.img.setImageResource(R.drawable.ic_menu_24);
        } else {
            Glide.with(h.itemView)
                    .load(imgUrl)
                    .placeholder(R.drawable.ic_menu_24)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(h.img);
        }

        h.btnDetail.setOnClickListener(v -> {
            Intent it = new Intent(v.getContext(), AdminTourDetailViewActivity.class);
            it.putExtra("index", h.getBindingAdapterPosition());
            v.getContext().startActivity(it);
        });
    }

    @Override public int getItemCount(){ return data.size(); }

    public void filter(String query) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.getDefault());
        data.clear();
        if (q.isEmpty()) {
            data.addAll(all);
        } else {
            for (Object t : all) {
                String name = str(t, "name").toLowerCase(Locale.getDefault());
                if (name.contains(q)) data.add(t);
            }
        }
        notifyDataSetChanged();
    }

    // ---------- helpers reflexión seguros ----------
    private static Object f(Object o, String n){
        if (o==null) return null;
        try { Field f=o.getClass().getDeclaredField(n); f.setAccessible(true); return f.get(o); }
        catch (Throwable ignore){ return null; }
    }
    private static String  str(Object o, String n){ Object v=f(o,n); return v==null? "" : String.valueOf(v); }
    private static Double  dbl(Object o, String n){ Object v=f(o,n); return (v instanceof Number)? ((Number)v).doubleValue() : null; }
    private static Integer integer(Object o, String n){ Object v=f(o,n); return (v instanceof Number)? ((Number)v).intValue() : null; }
    private static Date    date(Object o, String n){ Object v=f(o,n); return (v instanceof Date)? (Date)v : null; }
}
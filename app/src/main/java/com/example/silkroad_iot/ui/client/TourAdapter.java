package com.example.silkroad_iot.ui.client;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.TourFB;

import java.util.ArrayList;
import java.util.List;

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.VH> {
    private final List<TourFB> tours;

    public TourAdapter(List<TourFB> tours) {
        // ⚠️ IMPORTANTE: no copies la lista, usa la referencia directamente
        this.tours = tours;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView t1, t2;
        ImageView img;

        VH(View v) {
            super(v);
            t1 = v.findViewById(R.id.tTourName);
            t2 = v.findViewById(R.id.tTourPrice);
            img = v.findViewById(R.id.imgTour);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tour, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        TourFB t = tours.get(i);
        Log.d("TOUR_ADAPTER_BIND", "🖼️ Dibujando tour: " + t.getNombre());

        h.t1.setText(t.getNombre());
        h.t2.setText("S/ " + t.getPrecio() + " - " + t.getCantidad_personas() + " personas");

        Glide.with(h.itemView)
                .load(t.getImagen())
                .into(h.img);

        h.itemView.setOnClickListener(v -> {
            Context ctx = v.getContext();
            Intent intent = new Intent(ctx, TourDetailActivity.class);
            intent.putExtra("tour", t);
            ctx.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tours.size();
    }

    public void updateData(List<TourFB> newList) {
        Log.d("TOUR_ADAPTER", "updateData() llamado. Recibidos: " + newList.size() + " tours. tours.hash=" + tours.hashCode() + " newList.hash=" + newList.hashCode());

        tours.clear();
        tours.addAll(newList);

        Log.d("TOUR_ADAPTER", "Adapter actualizado. tours.size() = " + tours.size());
        notifyDataSetChanged();
    }

}

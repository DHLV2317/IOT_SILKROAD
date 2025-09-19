package com.example.silkroad_iot.ui.client;

import android.content.Context;
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
import com.example.silkroad_iot.data.Tour;

import java.util.List;

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.VH> {
    List<Tour> tours;

    public TourAdapter(List<Tour> tours) {
        this.tours = tours;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView t1, t2, t3;
        ImageView img;

        public VH(View v) {
            super(v);
            t1 = v.findViewById(R.id.tTourName);
            t2 = v.findViewById(R.id.tTourPrice);
            t3 = v.findViewById(R.id.tTourDescription);
            img = v.findViewById(R.id.imgTour);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tour, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        Tour t = tours.get(i);
        h.t1.setText(t.name);
        h.t2.setText("S/ " + t.price + " - " + t.people + " personas");
        h.t3.setText(t.description);
        Glide.with(h.itemView).load(t.imageUrl).into(h.img);

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
}

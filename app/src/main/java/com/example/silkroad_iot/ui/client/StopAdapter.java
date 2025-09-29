package com.example.silkroad_iot.ui.client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.Stop;

import java.util.List;

public class StopAdapter extends RecyclerView.Adapter<StopAdapter.VH> {
    private final List<Stop> stops;

    public StopAdapter(List<Stop> stops) {
        this.stops = stops;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvTime, tvCost;
        ImageView ivMap;

        VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvStopName);
            tvAddress = v.findViewById(R.id.tvStopAddress);
            tvTime = v.findViewById(R.id.tvStopTime);
            tvCost = v.findViewById(R.id.tvStopCost);
            ivMap = v.findViewById(R.id.ivMapImage);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stop, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Stop stop = stops.get(position);

        holder.tvName.setText((position + 1) + "° Parada: " + stop.name);
        holder.tvAddress.setText("Dirección: " + stop.address);
        holder.tvTime.setText("Tiempo: " + stop.time);
        holder.tvCost.setText(String.format("Costo: S/. %.2f", stop.cost));
    }

    @Override
    public int getItemCount() {
        return stops.size();
    }
}

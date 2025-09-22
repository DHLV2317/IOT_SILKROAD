package com.example.silkroad_iot.ui.client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.TourOrder;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TourOrderAdapter extends RecyclerView.Adapter<TourOrderAdapter.VH> {
    private final List<TourOrder> orders;

    public TourOrderAdapter(List<TourOrder> orders) {
        this.orders = orders;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvStatus, tvOrderDate, tvTotalPrice;

        VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvCompanyName);
            tvDate = v.findViewById(R.id.tvTourDate);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvOrderDate = v.findViewById(R.id.tvOrderDate);
            tvTotalPrice = v.findViewById(R.id.tvTotalPrice);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tour_order, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TourOrderAdapter.VH holder, int position) {
        TourOrder order = orders.get(position);

        holder.tvName.setText(order.tour.name); // Nombre de la empresa

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.tvDate.setText("Tour: " + sdf.format(order.date));
        holder.tvStatus.setText(order.status != null ? order.status.name() : "RESERVADO");

        holder.tvOrderDate.setText("Reservado el: " + sdf.format(order.createdAt));

        double total = order.quantity * order.tour.price;
        holder.tvTotalPrice.setText("S/. " + String.format("%.2f", total));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}


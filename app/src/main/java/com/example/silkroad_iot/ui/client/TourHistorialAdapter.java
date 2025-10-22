package com.example.silkroad_iot.ui.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.data.TourHistorialFB;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TourHistorialAdapter extends RecyclerView.Adapter<TourHistorialAdapter.VH> {

    private final List<TourHistorialFB> historialList;
    private final OnOrderClickListener listener;

    public TourHistorialAdapter(List<TourHistorialFB> historialList, OnOrderClickListener listener) {
        this.historialList = historialList;
        this.listener = listener;
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView tvTourName, tvDate, tvStatus;

        public VH(@NonNull View v) {
            super(v);
            tvTourName = v.findViewById(R.id.tvCompanyName); // ✅ CAMBIA AQUÍ
            tvDate = v.findViewById(R.id.tvTourDate);
            tvStatus = v.findViewById(R.id.tvStatus);        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tour_order, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TourHistorialFB historial = historialList.get(position);

        // 🕒 Fecha
        Date fecha = historial.getFechaRealizado();
        String fechaStr = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(fecha);
        holder.tvDate.setText("Realizado el: " + fechaStr);

        holder.tvStatus.setText("Estado: " + historial.getEstado());


        // 🔄 Obtener nombre del tour desde Firestore
        FirebaseFirestore.getInstance()
                .collection("tours")
                .document(historial.getIdTour())
                .get()
                .addOnSuccessListener(doc -> {
                    TourFB tour = doc.toObject(TourFB.class);
                    if (tour != null) {
                        holder.tvTourName.setText(tour.getNombre());
                    } else {
                        holder.tvTourName.setText("Tour desconocido");
                    }
                })
                .addOnFailureListener(e -> {
                    holder.tvTourName.setText("Error cargando tour");
                });
    }

    @Override
    public int getItemCount() {
        return historialList.size();
    }

    public List<TourHistorialFB> getOrders() {
        return historialList;
    }

    public interface OnOrderClickListener {
        void onOrderClick(TourHistorialFB order);
    }


}

package com.example.silkroad_iot.ui.client;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
        TextView tvTourName, tvDate, tvStatus, tvTotalPrice,tvOrderDate;

        public VH(@NonNull View v) {
            super(v);
            tvTourName = v.findViewById(R.id.tvCompanyName); // ✅ CAMBIA AQUÍ
            tvDate = v.findViewById(R.id.tvTourDate);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvTotalPrice= v.findViewById(R.id.tvTotalPrice);
            tvOrderDate= v.findViewById(R.id.tvOrderDate);

        }
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

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // 🕒 Fecha del tour (fechaRealizado)
        Date fechaTour = historial.getFechaRealizado();
        if (fechaTour != null) {
            holder.tvDate.setText("Inicio del Tour: " + sdf.format(fechaTour));
        } else {
            holder.tvDate.setText("Inicio del Tour: -");
            Log.w("HISTORIAL", "⚠️ fechaRealizado es null en posición " + position);
        }

        // 📅 Fecha de reserva
        Date fechaReserva = historial.getFechaReserva();
        if (fechaReserva != null) {
            holder.tvOrderDate.setText("Reservado el: " + sdf.format(fechaReserva));
        } else {
            holder.tvOrderDate.setText("Reservado el: -");
            Log.w("HISTORIAL", "⚠️ fechaReserva es null en posición " + position);
        }

        holder.tvStatus.setText("Estado: " + historial.getEstado());

        // ✅ Cargar datos del tour una sola vez
        FirebaseFirestore.getInstance()
                .collection("tours")
                .document(historial.getIdTour())
                .get()
                .addOnSuccessListener(doc -> {
                    TourFB tour = new TourFB();

                    tour.setId(doc.getId());
                    tour.setNombre(doc.getString("nombre"));
                    tour.setPrecio(doc.getDouble("precio"));

                    Object data = doc.get("id_paradas");
                    if (data instanceof List) {
                        tour.setId_paradas((List<String>) data);
                    } else if (data instanceof String) {
                        tour.setId_paradas(java.util.Collections.singletonList((String) data));
                    }

                    // ✅ Validación por seguridad
                    holder.tvTourName.setText(tour.getNombre() != null ? tour.getNombre() : "Sin nombre");

                    Double precio = tour.getPrecio();
                    if (precio != null) {
                        holder.tvTotalPrice.setText(String.format(Locale.getDefault(), "S/ %.2f", precio));
                    } else {
                        holder.tvTotalPrice.setText("S/ -");
                    }

                    holder.itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(v.getContext(), OrderDetailActivity.class);
                        intent.putExtra("tourFB", tour);
                        intent.putExtra("historialFB", historial);
                        intent.putExtra("historialId", historial.getId());
                        v.getContext().startActivity(intent);
                    });
                })

                .addOnFailureListener(e -> {
                    holder.tvTourName.setText("Error cargando tour");
                    holder.tvTotalPrice.setText("S/ -");
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

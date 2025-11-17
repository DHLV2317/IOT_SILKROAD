package com.example.silkroad_iot.ui.client;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silkroad_iot.databinding.ItemTourOrderBinding;
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
        ItemTourOrderBinding binding;

        public VH(ItemTourOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTourOrderBinding binding = ItemTourOrderBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false
        );
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TourHistorialFB historial = historialList.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // 🕒 Fecha del tour (fechaRealizado)
        Date fechaTour = historial.getFechaRealizado();
        if (fechaTour != null) {
            holder.binding.tvTourDate.setText("Inicio del Tour: " + sdf.format(fechaTour));
        } else {
            holder.binding.tvTourDate.setText("Inicio del Tour: -");
            Log.w("HISTORIAL", "⚠️ fechaRealizado es null en posición " + position);
        }

        // 📅 Fecha de reserva
        Date fechaReserva = historial.getFechaReserva();
        if (fechaReserva != null) {
            holder.binding.tvOrderDate.setText("Reservado el: " + sdf.format(fechaReserva));
        } else {
            holder.binding.tvOrderDate.setText("Reservado el: -");
            Log.w("HISTORIAL", "⚠️ fechaReserva es null en posición " + position);
        }

        holder.binding.tvStatus.setText("Estado: " + historial.getEstado());

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
                    holder.binding.tvCompanyName.setText(tour.getNombre() != null ? tour.getNombre() : "Sin nombre");

                    Double precio = tour.getPrecio();
                    if (precio != null) {
                        holder.binding.tvTotalPrice.setText(String.format(Locale.getDefault(), "S/ %.2f", precio));
                    } else {
                        holder.binding.tvTotalPrice.setText("S/ -");
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
                    holder.binding.tvCompanyName.setText("Error cargando tour");
                    holder.binding.tvTotalPrice.setText("S/ -");
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

package com.example.silkroad_iot.ui.guide;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.TourOffer;
import java.util.List;

public class TourOfferAdapter extends RecyclerView.Adapter<TourOfferAdapter.TourOfferViewHolder> {

    private List<TourOffer> tourOfferList;

    public TourOfferAdapter(List<TourOffer> tourOfferList) {
        this.tourOfferList = tourOfferList;
    }

    @NonNull
    @Override
    public TourOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tour_offer, parent, false);
        return new TourOfferViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TourOfferViewHolder holder, int position) {
        TourOffer currentOffer = tourOfferList.get(position);
        holder.tvCompanyName.setText(currentOffer.getCompanyName());
        holder.tvTourName.setText(currentOffer.getTourName());
        holder.tvPayment.setText("Pago: " + currentOffer.getPayment());

        holder.btnAcceptOffer.setOnClickListener(v -> {
            // Lógica para aceptar la oferta
            Toast.makeText(v.getContext(), "Oferta aceptada: " + currentOffer.getTourName(), Toast.LENGTH_SHORT).show();
            // Aquí podrías, por ejemplo, eliminar la oferta de la lista y notificar al adapter
            // tourOfferList.remove(position);
            // notifyItemRemoved(position);
            // notifyItemRangeChanged(position, tourOfferList.size());
        });

        holder.btnRejectOffer.setOnClickListener(v -> {
            // Lógica para rechazar la oferta
            Toast.makeText(v.getContext(), "Oferta rechazada: " + currentOffer.getTourName(), Toast.LENGTH_SHORT).show();
            // Aquí podrías, por ejemplo, eliminar la oferta de la lista y notificar al adapter
            // tourOfferList.remove(position);
            // notifyItemRemoved(position);
            // notifyItemRangeChanged(position, tourOfferList.size());
        });
    }

    @Override
    public int getItemCount() {
        return tourOfferList.size();
    }

    static class TourOfferViewHolder extends RecyclerView.ViewHolder {
        TextView tvTourName;
        TextView tvPayment;
        TextView tvCompanyName;
        Button btnAcceptOffer;
        Button btnRejectOffer;

        public TourOfferViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTourName = itemView.findViewById(R.id.tvTourName);
            tvPayment = itemView.findViewById(R.id.tvPayment);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            btnAcceptOffer = itemView.findViewById(R.id.btnAcceptOffer);
            btnRejectOffer = itemView.findViewById(R.id.btnRejectOffer);
        }
    }
}
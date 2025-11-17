package com.example.silkroad_iot.ui.guide;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silkroad_iot.databinding.ItemTourOfferBinding;
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
        ItemTourOfferBinding binding = ItemTourOfferBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false
        );
        return new TourOfferViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TourOfferViewHolder holder, int position) {
        TourOffer currentOffer = tourOfferList.get(position);
        holder.binding.tvCompanyName.setText(currentOffer.getCompanyName());
        holder.binding.tvTourName.setText(currentOffer.getTourName());
        holder.binding.tvPayment.setText("Pago: " + currentOffer.getPayment());

        holder.binding.btnAcceptOffer.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Oferta aceptada: " + currentOffer.getTourName(), Toast.LENGTH_SHORT).show();
        });

        holder.binding.btnRejectOffer.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Oferta rechazada: " + currentOffer.getTourName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return tourOfferList.size();
    }

    static class TourOfferViewHolder extends RecyclerView.ViewHolder {
        ItemTourOfferBinding binding;

        public TourOfferViewHolder(ItemTourOfferBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.databinding.ItemTourBinding;
import com.example.silkroad_iot.ui.util.AnimationHelper;

import java.util.ArrayList;
import java.util.List;

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.VH> {
    private final List<TourFB> tours = new ArrayList<>();

    public TourAdapter(List<TourFB> tours) {
        if (tours != null) {
            this.tours.addAll(tours);
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        ItemTourBinding binding;

        VH(ItemTourBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTourBinding binding = ItemTourBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false
        );
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        TourFB t = tours.get(i);

        h.binding.tTourName.setText(t.getNombre());
        h.binding.tTourDescription.setText(t.getDescription() != null ? t.getDescription() : "");
        h.binding.tPeople.setText(t.getCantidad_personas() + " pax");
        h.binding.tRating.setText("4.5");
        h.binding.tDuration.setText("2h");

        Glide.with(h.binding.imgTour.getContext())
                .load(t.getImagen())
                .placeholder(R.drawable.placeholder_image_primary)
                .error(R.drawable.placeholder_image_primary)
                .centerCrop()
                .into(h.binding.imgTour);

        h.itemView.setOnClickListener(v -> {
            AnimationHelper.scaleUp(v);
            v.postDelayed(() -> {
                Context ctx = v.getContext();
                Intent intent = new Intent(ctx, TourDetailActivity.class);
                intent.putExtra("tour", t);
                ctx.startActivity(intent);
            }, 150);
        });
    }

    @Override
    public int getItemCount() {
        return tours.size();
    }

    public void updateData(List<TourFB> newList) {
        if (newList == null) newList = new ArrayList<>();
        
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new TourDiffCallback(this.tours, newList));
        
        this.tours.clear();
        this.tours.addAll(newList);
        
        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * DiffUtil.Callback para comparar listas de TourFB
     */
    private static class TourDiffCallback extends DiffUtil.Callback {
        private final List<TourFB> oldList;
        private final List<TourFB> newList;

        TourDiffCallback(List<TourFB> oldList, List<TourFB> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            TourFB oldTour = oldList.get(oldItemPosition);
            TourFB newTour = newList.get(newItemPosition);
            
            // Compara por ID único
            if (oldTour.getId() != null && newTour.getId() != null) {
                return oldTour.getId().equals(newTour.getId());
            }
            
            // Fallback: compara por nombre si no hay ID
            return oldTour.getDisplayName().equals(newTour.getDisplayName());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            TourFB oldTour = oldList.get(oldItemPosition);
            TourFB newTour = newList.get(newItemPosition);
            
            // Compara contenido relevante para la UI
            return oldTour.getDisplayName().equals(newTour.getDisplayName()) &&
                   oldTour.getDisplayPrice() == newTour.getDisplayPrice() &&
                   oldTour.getDisplayPeople() == newTour.getDisplayPeople() &&
                   safeEquals(oldTour.getDisplayImageUrl(), newTour.getDisplayImageUrl());
        }

        private boolean safeEquals(String s1, String s2) {
            if (s1 == null && s2 == null) return true;
            if (s1 == null || s2 == null) return false;
            return s1.equals(s2);
        }
    }

}

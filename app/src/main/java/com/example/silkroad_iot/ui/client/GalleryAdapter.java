package com.example.silkroad_iot.ui.client;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.databinding.ItemGalleryBinding;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.VH> {
    private final List<String> images;

    public GalleryAdapter(List<String> images) { this.images = images; }

    static class VH extends RecyclerView.ViewHolder {
        ItemGalleryBinding binding;
        VH(ItemGalleryBinding binding) { 
            super(binding.getRoot()); 
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGalleryBinding binding = ItemGalleryBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false
        );
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String url = images.get(position);
        Glide.with(holder.binding.imgGallery.getContext())
                .load(url)
                .placeholder(R.drawable.logo_silkroad)
                .centerCrop()
                .into(holder.binding.imgGallery);
    }

    @Override
    public int getItemCount() { return images.size(); }
}


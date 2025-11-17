package com.example.silkroad_iot.ui.client;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.EmpresaFb;
import com.example.silkroad_iot.databinding.ItemCompanyBinding;
import com.example.silkroad_iot.ui.util.AnimationHelper;

import java.util.ArrayList;
import java.util.List;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.VH>{
    private final List<EmpresaFb> data = new ArrayList<>();
    private final List<EmpresaFb> fullList = new ArrayList<>();

    public CompanyAdapter(List<EmpresaFb> data) {
        if (data != null) {
            this.data.addAll(data);
            this.fullList.addAll(data);
        }
    }

    public void updateData(List<EmpresaFb> newList) {
        if (newList == null) newList = new ArrayList<>();
        
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
            new CompanyDiffCallback(this.data, newList)
        );
        
        this.data.clear();
        this.data.addAll(newList);
        this.fullList.clear();
        this.fullList.addAll(newList);
        
        diffResult.dispatchUpdatesTo(this);
    }

    public void filterList(String query) {
        List<EmpresaFb> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            for (EmpresaFb c : fullList) {
                if (c.getNombre().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(c);
                }
            }
        }
        
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
            new CompanyDiffCallback(this.data, filteredList)
        );
        
        this.data.clear();
        this.data.addAll(filteredList);
        
        diffResult.dispatchUpdatesTo(this);
    }

    static class VH extends RecyclerView.ViewHolder {
        ItemCompanyBinding binding;

        VH(ItemCompanyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCompanyBinding binding = ItemCompanyBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false
        );
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        EmpresaFb c = data.get(i);

        h.binding.tTitle.setText(c.getNombre());
        h.binding.tRating.setText("⭐ 4.5");

        Glide.with(h.binding.imgCompany.getContext())
                .load(c.getImagen())
                .placeholder(R.drawable.placeholder_image_warm)
                .error(R.drawable.placeholder_image_warm)
                .centerCrop()
                .into(h.binding.imgCompany);

        h.itemView.setOnClickListener(v -> {
            AnimationHelper.scaleUp(v);
            v.postDelayed(() -> {
                Context ctx = v.getContext();
                Intent intent = new Intent(ctx, ToursActivity.class);
                intent.putExtra("company", c);
                ctx.startActivity(intent);
            }, 150);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * DiffUtil.Callback para comparar empresas
     */
    private static class CompanyDiffCallback extends DiffUtil.Callback {
        private final List<EmpresaFb> oldList;
        private final List<EmpresaFb> newList;

        CompanyDiffCallback(List<EmpresaFb> oldList, List<EmpresaFb> newList) {
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
            EmpresaFb oldItem = oldList.get(oldItemPosition);
            EmpresaFb newItem = newList.get(newItemPosition);
            
            if (oldItem.getId() != null && newItem.getId() != null) {
                return oldItem.getId().equals(newItem.getId());
            }
            
            return oldItem.getNombre().equals(newItem.getNombre());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            EmpresaFb oldItem = oldList.get(oldItemPosition);
            EmpresaFb newItem = newList.get(newItemPosition);
            
            return oldItem.getNombre().equals(newItem.getNombre()) &&
                   safeEquals(oldItem.getImagen(), newItem.getImagen());
        }

        private boolean safeEquals(String s1, String s2) {
            if (s1 == null && s2 == null) return true;
            if (s1 == null || s2 == null) return false;
            return s1.equals(s2);
        }
    }
}

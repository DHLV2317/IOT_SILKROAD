package com.example.silkroad_iot.ui.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.GuideFb;
import com.example.silkroad_iot.databinding.ItemAdminGuideBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminGuidesAdapter extends RecyclerView.Adapter<AdminGuidesAdapter.VH> {

    public interface Callbacks {
        void onAssignClicked(int position);
        void onDetailClicked(int position);
    }

    private final List<GuideFb> all  = new ArrayList<>();
    private final List<GuideFb> data = new ArrayList<>();
    private final Callbacks cb;

    public AdminGuidesAdapter(List<GuideFb> items, Callbacks callbacks) {
        if (items != null) {
            all.addAll(items);
            data.addAll(items);
        }
        this.cb = callbacks;
    }

    public void updateData(List<GuideFb> items) {
        if (items == null) items = new ArrayList<>();
        
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
            new GuideDiffCallback(this.data, items)
        );
        
        all.clear();
        data.clear();
        all.addAll(items);
        data.addAll(items);
        
        diffResult.dispatchUpdatesTo(this);
    }

    static class VH extends RecyclerView.ViewHolder {
        ItemAdminGuideBinding binding;

        VH(ItemAdminGuideBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminGuideBinding binding = ItemAdminGuideBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false
        );
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        GuideFb g = data.get(position);

        String foto = safe(g.getFotoUrl());
        Glide.with(h.binding.aImg.getContext())
                .load(foto.isEmpty() ? R.drawable.ic_person_24 : foto)
                .placeholder(R.drawable.placeholder_image_warm)
                .error(R.drawable.ic_person_24)
                .centerCrop()
                .into(h.binding.aImg);

        String name   = safe(g.getNombre());
        String langs  = safe(g.getLangs());
        String state  = safe(g.getEstado());
        String actual = safe(g.getTourActual());

        h.binding.aTitle.setText(name.isEmpty() ? "(Sin nombre)" : name);
        h.binding.aSubtitle.setText(langs.isEmpty() ? "—" : langs);
        h.binding.aStatus.setText(state.isEmpty() ? "—" : state);

        int hist = (g.getHistorial() == null) ? 0 : g.getHistorial().size();
        h.binding.aExtra.setText(
                actual.isEmpty()
                        ? hist + " tours"
                        : hist + " tours • Actual: " + actual
        );

        h.binding.btnAssign.setOnClickListener(v -> {
            int pos = h.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && cb != null) cb.onAssignClicked(pos);
        });

        h.binding.btnDetail.setOnClickListener(v -> {
            int pos = h.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && cb != null) cb.onDetailClicked(pos);
        });
    }

    @Override public int getItemCount() { return data.size(); }

    public void filter(String q) {
        String s = q == null ? "" : q.toLowerCase(Locale.getDefault()).trim();
        
        List<GuideFb> filtered = new ArrayList<>();
        if (s.isEmpty()) {
            filtered.addAll(all);
        } else {
            for (GuideFb g : all) {
                String name  = safe(g.getNombre()).toLowerCase(Locale.getDefault());
                String langs = safe(g.getLangs()).toLowerCase(Locale.getDefault());
                String state = safe(g.getEstado()).toLowerCase(Locale.getDefault());
                if (name.contains(s) || langs.contains(s) || state.contains(s)) {
                    filtered.add(g);
                }
            }
        }
        
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
            new GuideDiffCallback(this.data, filtered)
        );
        
        data.clear();
        data.addAll(filtered);
        
        diffResult.dispatchUpdatesTo(this);
    }

    private static String safe(String s) { return s == null ? "" : s; }

    /**
     * DiffUtil.Callback para comparar guías
     */
    private static class GuideDiffCallback extends DiffUtil.Callback {
        private final List<GuideFb> oldList;
        private final List<GuideFb> newList;

        GuideDiffCallback(List<GuideFb> oldList, List<GuideFb> newList) {
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
            GuideFb oldGuide = oldList.get(oldItemPosition);
            GuideFb newGuide = newList.get(newItemPosition);
            
            if (oldGuide.getId() != null && newGuide.getId() != null) {
                return oldGuide.getId().equals(newGuide.getId());
            }
            
            return safe(oldGuide.getNombre()).equals(safe(newGuide.getNombre()));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            GuideFb oldGuide = oldList.get(oldItemPosition);
            GuideFb newGuide = newList.get(newItemPosition);
            
            return safe(oldGuide.getNombre()).equals(safe(newGuide.getNombre())) &&
                   safe(oldGuide.getLangs()).equals(safe(newGuide.getLangs())) &&
                   safe(oldGuide.getEstado()).equals(safe(newGuide.getEstado())) &&
                   oldGuide.getHistorial() == newGuide.getHistorial() &&
                   oldGuide.getTourActual() == newGuide.getTourActual();
        }
    }
}
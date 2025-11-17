package com.example.silkroad_iot.ui.admin;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.databinding.ItemAdminTourBinding;
import com.example.silkroad_iot.ui.util.AnimationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminToursAdapter extends RecyclerView.Adapter<AdminToursAdapter.VH> {

    private final List<TourFB> data = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public AdminToursAdapter(List<TourFB> items){ replace(items); }

    public void replace(List<TourFB> items){
        if (items == null) items = new ArrayList<>();
        
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
            new TourDiffCallback(this.data, items)
        );
        
        this.data.clear();
        this.data.addAll(items);
        
        diffResult.dispatchUpdatesTo(this);
    }

    static class VH extends RecyclerView.ViewHolder {
        ItemAdminTourBinding binding;

        VH(ItemAdminTourBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int vt){
        ItemAdminTourBinding binding = ItemAdminTourBinding.inflate(
            LayoutInflater.from(p.getContext()), p, false
        );
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i){
        TourFB t = data.get(i);

        String name   = t.getDisplayName();
        String desc   = t.getDescription() == null ? "" : t.getDescription();
        double price  = t.getPrecio();
        int people    = t.getCantidad_personas();
        String imgUrl = t.getDisplayImageUrl();

        String fecha = "sin fecha";
        if (t.getDateFrom() != null && t.getDateTo() != null) {
            fecha = sdf.format(t.getDateFrom()) + " - " + sdf.format(t.getDateTo());
        } else if (t.getDateFrom() != null) {
            fecha = sdf.format(t.getDateFrom());
        } else if (t.getDateTo() != null) {
            fecha = sdf.format(t.getDateTo());
        }

        h.binding.aTitle.setText(name == null || name.isEmpty() ? "Sin nombre" : name);

        String meta = "S/ " + String.format(Locale.getDefault(),"%.2f", price) + " · " + people + " personas";
        h.binding.aSubtitle.setText(desc.isEmpty() ? meta : meta + " · " + desc);
        h.binding.aDate.setText(fecha);

        Glide.with(h.binding.aImg.getContext())
                .load(imgUrl)
                .placeholder(R.drawable.placeholder_image_warm)
                .error(R.drawable.placeholder_image_warm)
                .centerCrop()
                .into(h.binding.aImg);

        h.binding.btnDetail.setOnClickListener(v -> {
            AnimationHelper.bounce(v);
            v.postDelayed(() -> {
                Intent it = new Intent(v.getContext(), AdminTourDetailViewActivity.class);
                it.putExtra("tourId", t.getId());
                v.getContext().startActivity(it);
            }, 200);
        });
    }

    @Override public int getItemCount(){ return data.size(); }

    /**
     * DiffUtil.Callback para AdminToursAdapter
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
            
            if (oldTour.getId() != null && newTour.getId() != null) {
                return oldTour.getId().equals(newTour.getId());
            }
            
            return oldTour.getDisplayName().equals(newTour.getDisplayName());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            TourFB oldTour = oldList.get(oldItemPosition);
            TourFB newTour = newList.get(newItemPosition);
            
            boolean nameEquals = oldTour.getDisplayName().equals(newTour.getDisplayName());
            boolean priceEquals = oldTour.getDisplayPrice() == newTour.getDisplayPrice();
            boolean peopleEquals = oldTour.getDisplayPeople() == newTour.getDisplayPeople();
            boolean imageEquals = safeEquals(oldTour.getDisplayImageUrl(), newTour.getDisplayImageUrl());
            boolean dateEquals = datesEqual(oldTour.getDateFrom(), newTour.getDateFrom()) &&
                                 datesEqual(oldTour.getDateTo(), newTour.getDateTo());
            
            return nameEquals && priceEquals && peopleEquals && imageEquals && dateEquals;
        }

        private boolean safeEquals(String s1, String s2) {
            if (s1 == null && s2 == null) return true;
            if (s1 == null || s2 == null) return false;
            return s1.equals(s2);
        }

        private boolean datesEqual(Date d1, Date d2) {
            if (d1 == null && d2 == null) return true;
            if (d1 == null || d2 == null) return false;
            return d1.equals(d2);
        }
    }
}

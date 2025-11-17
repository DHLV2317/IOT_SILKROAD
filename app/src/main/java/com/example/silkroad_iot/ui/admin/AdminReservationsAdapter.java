package com.example.silkroad_iot.ui.admin;

import android.content.Intent;
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
import com.example.silkroad_iot.data.ReservaWithTour;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.data.TourHistorialFB;
import com.example.silkroad_iot.databinding.ItemAdminReservationBinding;
import com.example.silkroad_iot.ui.util.AnimationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminReservationsAdapter extends RecyclerView.Adapter<AdminReservationsAdapter.VH> {

    private final List<ReservaWithTour> all;
    private final List<ReservaWithTour> data;
    private final SimpleDateFormat sdf =
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private String statusFilter = "Todos";

    public AdminReservationsAdapter(List<ReservaWithTour> items) {
        this.all  = new ArrayList<>(items);
        this.data = new ArrayList<>(items);
    }

    static class VH extends RecyclerView.ViewHolder {
        ItemAdminReservationBinding binding;
        
        VH(ItemAdminReservationBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int vt){
        ItemAdminReservationBinding binding = ItemAdminReservationBinding.inflate(
            LayoutInflater.from(p.getContext()), p, false
        );
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i){
        ReservaWithTour item = data.get(i);
        TourHistorialFB r = item.getReserva();
        TourFB tour       = item.getTour();

        // ------- datos principales (tour) -------
        String tourName = tour != null ? tour.getDisplayName() : "(Sin tour)";

        // ------- cliente -------
        String clientName = r.getIdUsuario() != null ? r.getIdUsuario() : "Cliente sin nombre";

        // ------- pax / total -------
        int pax = r.getPax() > 0
                ? r.getPax()
                : (tour != null && tour.getDisplayPeople() > 0 ? tour.getDisplayPeople() : 1);

        double precioUnit = tour != null ? tour.getDisplayPrice() : 0.0;
        double total = precioUnit * pax;

        // ------- estado / fecha -------
        String status = r.getEstado();
        if (status == null || status.trim().isEmpty()) status = "pendiente";

        Date date = r.getFechaReserva() != null
                ? r.getFechaReserva()
                : r.getFechaRealizado();

        // ------- imagen -------
        String imageUrl = tour != null ? tour.getImageUrl() : null;

        // ------- bind UI con ViewBinding -------
        h.binding.aTitle.setText(tourName);

        String paxText = pax + " pax";
        String money   = "S/ " + String.format(Locale.getDefault(), "%.2f", total);
        h.binding.aSubtitle.setText(clientName + " · " + paxText + " · " + money);

        h.binding.aDate.setText(date == null ? "—" : sdf.format(date));
        h.binding.aStatus.setText(status);

        // Color según estado
        int bg = R.color.pill_gray;
        String st = status.toLowerCase(Locale.getDefault());
        if (st.contains("check-in") || st.contains("check-out")
                || st.contains("final") || st.contains("acept")) {
            bg = R.color.teal_200;
        } else if (st.contains("rech") || st.contains("cancel")) {
            bg = android.R.color.holo_red_light;
        }
        h.binding.aStatus.setBackgroundResource(bg);

        if (imageUrl == null || imageUrl.trim().isEmpty()){
            Glide.with(h.itemView)
                    .load(R.drawable.placeholder_image_cool)
                    .into(h.binding.aImg);
        } else {
            Glide.with(h.itemView)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image_cool)
                    .error(R.drawable.placeholder_image_cool)
                    .centerCrop()
                    .into(h.binding.aImg);
        }

        // ------- botón detalle con animación -------
        h.binding.btnDetail.setOnClickListener(v -> {
            AnimationHelper.bounce(v);
            v.postDelayed(() -> {
                Intent it = new Intent(v.getContext(), AdminReservationDetailActivity.class);
                it.putExtra("reserva", item);
                v.getContext().startActivity(it);
            }, 200);
        });
    }

    @Override public int getItemCount(){ return data.size(); }

    // =========================================================
    // Filtro texto + estado con DiffUtil
    // =========================================================
    public void filter(String query, String status){
        String q  = query  == null ? "" : query.trim().toLowerCase(Locale.getDefault());
        String stFilter = status == null ? "Todos" : status.toLowerCase(Locale.getDefault());

        List<ReservaWithTour> filtered = new ArrayList<>();
        for (ReservaWithTour item : all){

            TourHistorialFB r = item.getReserva();
            TourFB tour       = item.getTour();

            String tourName = tour != null ? tour.getDisplayName() : "";
            String cli      = r.getIdUsuario() != null ? r.getIdUsuario() : "";
            String s        = r.getEstado() != null ? r.getEstado() : "pendiente";

            String sLower = s.toLowerCase(Locale.getDefault());

            boolean matchText = q.isEmpty()
                    || tourName.toLowerCase(Locale.getDefault()).contains(q)
                    || cli.toLowerCase(Locale.getDefault()).contains(q);

            boolean matchStatus = stFilter.equals("todos")
                    || sLower.equals(stFilter);

            if (matchText && matchStatus) filtered.add(item);
        }
        
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
            new ReservationDiffCallback(this.data, filtered)
        );
        
        this.data.clear();
        this.data.addAll(filtered);
        
        diffResult.dispatchUpdatesTo(this);
    }

    public void setStatusFilter(String status){
        this.statusFilter = status == null ? "Todos" : status;
    }

    public String getStatusFilter(){ return statusFilter; }

    public void replace(List<ReservaWithTour> newItems) {
        if (newItems == null) newItems = new ArrayList<>();
        
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
            new ReservationDiffCallback(this.data, newItems)
        );
        
        all.clear();
        all.addAll(newItems);
        data.clear();
        data.addAll(newItems);
        
        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * DiffUtil.Callback para comparar reservaciones
     */
    private static class ReservationDiffCallback extends DiffUtil.Callback {
        private final List<ReservaWithTour> oldList;
        private final List<ReservaWithTour> newList;

        ReservationDiffCallback(List<ReservaWithTour> oldList, List<ReservaWithTour> newList) {
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
            ReservaWithTour oldItem = oldList.get(oldItemPosition);
            ReservaWithTour newItem = newList.get(newItemPosition);
            
            TourHistorialFB oldReserva = oldItem.getReserva();
            TourHistorialFB newReserva = newItem.getReserva();
            
            if (oldReserva.getId() != null && newReserva.getId() != null) {
                return oldReserva.getId().equals(newReserva.getId());
            }
            
            // Fallback: compara por tour + usuario
            String oldKey = (oldItem.getTour() != null ? oldItem.getTour().getId() : "") + 
                           "_" + oldReserva.getIdUsuario();
            String newKey = (newItem.getTour() != null ? newItem.getTour().getId() : "") + 
                           "_" + newReserva.getIdUsuario();
            return oldKey.equals(newKey);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            ReservaWithTour oldItem = oldList.get(oldItemPosition);
            ReservaWithTour newItem = newList.get(newItemPosition);
            
            TourHistorialFB oldReserva = oldItem.getReserva();
            TourHistorialFB newReserva = newItem.getReserva();
            
            boolean statusEquals = safeEquals(oldReserva.getEstado(), newReserva.getEstado());
            boolean paxEquals = oldReserva.getPax() == newReserva.getPax();
            boolean dateEquals = datesEqual(oldReserva.getFechaReserva(), newReserva.getFechaReserva());
            
            return statusEquals && paxEquals && dateEquals;
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
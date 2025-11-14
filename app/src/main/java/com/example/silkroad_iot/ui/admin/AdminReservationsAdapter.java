package com.example.silkroad_iot.ui.admin;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.ReservaWithTour;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.data.TourHistorialFB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminReservationsAdapter
        extends RecyclerView.Adapter<AdminReservationsAdapter.VH> {

    private final List<ReservaWithTour> all;
    private final List<ReservaWithTour> data;
    private final SimpleDateFormat sdf =
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    private String statusFilter = "Todos";

    public AdminReservationsAdapter(List<ReservaWithTour> items) {
        this.all  = new ArrayList<>(items);
        this.data = new ArrayList<>(items);
    }

    /** Reemplaza completamente la lista base */
    public void setItems(List<ReservaWithTour> items) {
        all.clear();
        data.clear();
        if (items != null) {
            all.addAll(items);
            data.addAll(items);
        }
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tTitle, tSub, tDate, tStatus, btnDetail;
        VH(View v) {
            super(v);
            img       = v.findViewById(R.id.aImg);
            tTitle    = v.findViewById(R.id.aTitle);
            tSub      = v.findViewById(R.id.aSubtitle);
            tDate     = v.findViewById(R.id.aDate);
            tStatus   = v.findViewById(R.id.aStatus);
            btnDetail = v.findViewById(R.id.btnDetail);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int vt) {
        View v = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_admin_reservation, p, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        ReservaWithTour item = data.get(i);
        TourHistorialFB r = item.getReserva();
        TourFB tour       = item.getTour();

        String tourName = (tour != null) ? tour.getDisplayName() : "(Sin tour)";
        String clientId = r.getIdUsuario() == null ? "" : r.getIdUsuario();

        int    people = (tour != null) ? tour.getDisplayPeople() : 1;
        double total  = (tour != null) ? tour.getDisplayPrice() : 0.0;

        String status = r.getEstado();
        if (status == null || status.trim().isEmpty()) status = "pendiente";

        Date date = r.getFechaReserva() != null ? r.getFechaReserva()
                : r.getFechaRealizado();

        String imageUrl = (tour != null) ? tour.getDisplayImageUrl() : "";

        // --------- Pintar ----------
        h.tTitle.setText(tourName.isEmpty() ? "(Sin tour)" : tourName);

        String cliLabel = clientId.isEmpty() ? "Cliente sin nombre" : clientId;
        h.tSub.setText(cliLabel + " · " + people + " pax · S/ " + total);

        h.tDate.setText(date == null ? "—" : sdf.format(date));
        h.tStatus.setText(status);

        int bg = R.color.pill_gray;
        String st = status.toLowerCase(Locale.getDefault());
        if (st.contains("check-in"))       bg = R.color.teal_200;
        else if (st.contains("check-out")) bg = R.color.teal_200;
        else if (st.contains("final"))     bg = R.color.teal_200;
        else if (st.contains("rech") ||
                st.contains("cancel"))    bg = android.R.color.holo_red_light;
        h.tStatus.setBackgroundResource(bg);

        if (imageUrl == null || imageUrl.isEmpty()) {
            Glide.with(h.itemView).load(R.drawable.ic_menu_24)
                    .error(R.drawable.ic_menu_24)
                    .into(h.img);
        } else {
            Glide.with(h.itemView).load(imageUrl)
                    .placeholder(R.drawable.ic_menu_24)
                    .error(R.drawable.ic_menu_24)
                    .into(h.img);
        }

        // Detalle
        h.btnDetail.setOnClickListener(v -> {
            Intent it = new Intent(v.getContext(), AdminReservationDetailActivity.class);
            it.putExtra("reserva", item);  // ReservaWithTour debe implementar Serializable
            v.getContext().startActivity(it);
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    /** Filtro combinado por texto y estado */
    public void filter(String query, String status) {
        String q  = query  == null ? "" : query.trim().toLowerCase(Locale.getDefault());
        String st = status == null ? "Todos" : status;

        data.clear();

        for (ReservaWithTour item : all) {
            TourHistorialFB r = item.getReserva();
            TourFB tour       = item.getTour();

            String tourName = tour != null ? tour.getDisplayName() : "";
            String clientId = r.getIdUsuario() == null ? "" : r.getIdUsuario();
            String s        = r.getEstado() == null ? "" : r.getEstado();

            boolean matchText =
                    q.isEmpty()
                            || tourName.toLowerCase(Locale.getDefault()).contains(q)
                            || clientId.toLowerCase(Locale.getDefault()).contains(q);

            boolean matchStatus =
                    st.equals("Todos")
                            || s.toLowerCase(Locale.getDefault())
                            .equals(st.toLowerCase(Locale.getDefault()));

            if (matchText && matchStatus) {
                data.add(item);
            }
        }
        notifyDataSetChanged();
    }

    public void setStatusFilter(String status) {
        this.statusFilter = status == null ? "Todos" : status;
    }

    public String getStatusFilter() { return statusFilter; }

    /** Lista filtrada actual (útil para reportes/PDF) */
    public List<ReservaWithTour> getCurrentItems() {
        return new ArrayList<>(data);
    }
}
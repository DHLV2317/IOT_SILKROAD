package com.example.silkroad_iot.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.GuideFb;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminGuidesAdapter extends RecyclerView.Adapter<AdminGuidesAdapter.VH> {

    public interface Callbacks {
        void onAssignClicked(int position);
        void onDetailClicked(int position);
    }

    private final List<GuideFb> all = new ArrayList<>();
    private final List<GuideFb> data = new ArrayList<>();
    private final Callbacks callbacks;

    public AdminGuidesAdapter(List<GuideFb> items, Callbacks callbacks) {
        if (items != null) {
            all.addAll(items);
            data.addAll(items);
        }
        this.callbacks = callbacks;
    }

    /** Reemplaza los datos al recargar desde Firestore. */
    public void updateData(List<GuideFb> items) {
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
        TextView tTitle, tSubtitle, tStatus, tExtra;
        MaterialButton btnAssign, btnDetail;
        VH(View v){
            super(v);
            img       = v.findViewById(R.id.aImg);
            tTitle    = v.findViewById(R.id.aTitle);
            tSubtitle = v.findViewById(R.id.aSubtitle);
            tStatus   = v.findViewById(R.id.aStatus);
            tExtra    = v.findViewById(R.id.aExtra);
            btnAssign = v.findViewById(R.id.btnAssign);
            btnDetail = v.findViewById(R.id.btnDetail);
        }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int vt){
        View v = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_admin_guide, p, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i){
        GuideFb g = data.get(i);

        String photo = g.getFotoUrl();
        Glide.with(h.itemView)
                .load(photo == null || photo.trim().isEmpty() ? R.drawable.ic_person_24 : photo)
                .into(h.img);

        String name = safe(g.getNombre());
        h.tTitle.setText(name.isEmpty() ? "(Sin nombre)" : name);

        String langs = safe(g.getLangs());
        h.tSubtitle.setText(langs.isEmpty() ? "—" : langs);

        String state = safe(g.getEstado());
        h.tStatus.setText(state.isEmpty() ? "—" : state);

        int historyCount = (g.getHistorial() == null) ? 0 : g.getHistorial().size();
        String currentTour = safe(g.getTourActual());
        h.tExtra.setText(currentTour.isEmpty()
                ? (historyCount + " tours")
                : (historyCount + " tours • Actual: " + currentTour));

        h.btnAssign.setOnClickListener(v -> {
            int pos = h.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && callbacks != null) {
                callbacks.onAssignClicked(pos);
            }
        });

        h.btnDetail.setOnClickListener(v -> {
            int pos = h.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && callbacks != null) {
                callbacks.onDetailClicked(pos);
            }
        });
    }

    @Override public int getItemCount(){ return data.size(); }

    /** Filtro por nombre / idiomas / estado. */
    public void filter(String q){
        String s = q == null ? "" : q.trim().toLowerCase(Locale.getDefault());
        data.clear();
        if (s.isEmpty()){
            data.addAll(all);
        } else {
            for (GuideFb g : all){
                String name  = safe(g.getNombre()).toLowerCase(Locale.getDefault());
                String langs = safe(g.getLangs()).toLowerCase(Locale.getDefault());
                String state = safe(g.getEstado()).toLowerCase(Locale.getDefault());
                if (name.contains(s) || langs.contains(s) || state.contains(s)) {
                    data.add(g);
                }
            }
        }
        notifyDataSetChanged();
    }

    private static String safe(String s){ return s == null ? "" : s; }
}
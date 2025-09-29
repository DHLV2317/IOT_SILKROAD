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
import com.example.silkroad_iot.data.AdminRepository;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminGuidesAdapter extends RecyclerView.Adapter<AdminGuidesAdapter.VH> {

    private final List<AdminRepository.Guide> all;
    private final List<AdminRepository.Guide> data;

    public AdminGuidesAdapter(List<AdminRepository.Guide> items){
        this.all  = new ArrayList<>(items);
        this.data = new ArrayList<>(items);
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

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int vt){
        View v = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_admin_guide, p, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int i){
        AdminRepository.Guide g = data.get(i);

        Glide.with(h.itemView).load(R.drawable.ic_person_24).into(h.img);
        h.tTitle.setText(g.name == null ? "(Sin nombre)" : g.name);
        h.tSubtitle.setText(g.langs == null ? "—" : g.langs);
        h.tStatus.setText(g.state == null ? "—" : g.state);
        h.tExtra.setText((g.history == null ? 0 : g.history.size()) + " tours");

        // Asignar tour (lo que ya tenías)
        h.btnAssign.setOnClickListener(v -> {
            if (h.itemView.getContext() instanceof AdminGuidesActivity) {
                ((AdminGuidesActivity) h.itemView.getContext())
                        .showAssignTourDialog(h.getBindingAdapterPosition());
            }
        });

        // NUEVO: abrir detalle del guía
        h.btnDetail.setOnClickListener(v -> {
            Intent it = new Intent(v.getContext(), AdminGuideDetailActivity.class);
            it.putExtra("index", h.getBindingAdapterPosition());
            v.getContext().startActivity(it);
        });
    }

    @Override public int getItemCount(){ return data.size(); }

    public void filter(String q){
        String s = q == null ? "" : q.trim().toLowerCase(Locale.getDefault());
        data.clear();
        if (s.isEmpty()){
            data.addAll(all);
        } else {
            for (AdminRepository.Guide g : all){
                String name = g.name == null ? "" : g.name.toLowerCase(Locale.getDefault());
                String langs= g.langs== null ? "" : g.langs.toLowerCase(Locale.getDefault());
                if (name.contains(s) || langs.contains(s)) data.add(g);
            }
        }
        notifyDataSetChanged();
    }
}
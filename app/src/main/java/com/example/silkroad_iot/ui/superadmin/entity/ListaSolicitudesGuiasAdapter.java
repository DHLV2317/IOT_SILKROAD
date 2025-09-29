package com.example.silkroad_iot.ui.superadmin.entity;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.silkroad_iot.databinding.SpAdministradorRvBinding;
import com.example.silkroad_iot.ui.superadmin.DetallesAdministradorActivity;
import com.example.silkroad_iot.ui.superadmin.DetallesSolicitudGuiaActivity;

public class ListaSolicitudesGuiasAdapter
        extends RecyclerView.Adapter<ListaSolicitudesGuiasAdapter.SolicitudGuiaViewHolder>{
    private List<Guia> lGuias;
    private Context context;

    @NonNull
    @Override
    public SolicitudGuiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        SpAdministradorRvBinding binding = SpAdministradorRvBinding.inflate(inflater, parent, false);
        return new SolicitudGuiaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudGuiaViewHolder holder, int position) {
        Guia guia = lGuias.get(position);
        holder.guia = guia;

        TextView tvNombreGuia= holder.binding.textView1;
        TextView tvTelefono = holder.binding.textView2;

        tvNombreGuia.setText(guia.getNombres());
        tvTelefono.setText(guia.getTelefono());

        holder.binding.getRoot().setOnClickListener(view -> {
            Intent intent = new Intent(context, DetallesSolicitudGuiaActivity.class);
            intent.putExtra("guia", guia);
            intent.putExtra("posicion", position);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lGuias.size();
    }

    public void setListaSolicitudesGuias(List<Guia> listaGuias) {
        this.lGuias = listaGuias;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public static class SolicitudGuiaViewHolder extends RecyclerView.ViewHolder {
        SpAdministradorRvBinding binding;
        Guia guia;

        public SolicitudGuiaViewHolder(SpAdministradorRvBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
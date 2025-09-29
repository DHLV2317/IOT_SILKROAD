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

public class ListaAdministradoresAdapter
        extends RecyclerView.Adapter<ListaAdministradoresAdapter.AdministradorViewHolder>{
    private List<Administrador> lAdministradores;
    private Context context;

    @NonNull
    @Override
    public AdministradorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        SpAdministradorRvBinding binding = SpAdministradorRvBinding.inflate(inflater, parent, false);
        return new AdministradorViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdministradorViewHolder holder, int position) {
        Administrador administrador = lAdministradores.get(position);
        holder.administrador = administrador;
        TextView tvNombreAdmin= holder.binding.textView1;
        TextView tvNombreEmpresa = holder.binding.textView2;

        tvNombreAdmin.setText(administrador.getNombre());
        tvNombreEmpresa.setText(administrador.getNombreEmpresa());

        holder.binding.getRoot().setOnClickListener(view -> {
            Intent intent = new Intent(context, DetallesAdministradorActivity.class);
            intent.putExtra("administrador", administrador);
            intent.putExtra("posicion", position);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lAdministradores.size();
    }

    public void setListaAdministradores(List<Administrador> listaAdministradores) {
        this.lAdministradores = listaAdministradores;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public static class AdministradorViewHolder extends RecyclerView.ViewHolder {
        SpAdministradorRvBinding binding;
        Administrador administrador;

        public AdministradorViewHolder(SpAdministradorRvBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
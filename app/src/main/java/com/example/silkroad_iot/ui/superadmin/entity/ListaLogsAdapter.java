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
import com.example.silkroad_iot.ui.superadmin.DetallesLogActivity;

public class ListaLogsAdapter
        extends RecyclerView.Adapter<ListaLogsAdapter.LogViewHolder>{
    private List<Log> lLogs;
    private Context context;

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        SpAdministradorRvBinding binding = SpAdministradorRvBinding.inflate(inflater, parent, false);
        return new LogViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        Log log = lLogs.get(position);
        holder.log = log;

        TextView tvNombreAdmin= holder.binding.textView1;
        TextView tvNombreEmpresa = holder.binding.textView2;

        tvNombreAdmin.setText(log.getNombre());
        tvNombreEmpresa.setText(log.getUsuario());

        holder.binding.getRoot().setOnClickListener(view -> {
            Intent intent = new Intent(context, DetallesLogActivity.class);
            intent.putExtra("log", log);
            intent.putExtra("posicion", position);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lLogs.size();
    }

    public void setListaLogs(List<Log> listaLogs) {
        this.lLogs = listaLogs;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public static class LogViewHolder extends RecyclerView.ViewHolder {
        SpAdministradorRvBinding binding;
        Log log;

        public LogViewHolder(SpAdministradorRvBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

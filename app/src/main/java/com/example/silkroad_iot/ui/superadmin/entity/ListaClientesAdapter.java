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
import com.example.silkroad_iot.ui.superadmin.DetallesClienteActivity;

public class ListaClientesAdapter
        extends RecyclerView.Adapter<ListaClientesAdapter.ClienteViewHolder>{
    private List<Cliente> lClientes;
    private Context context;

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        SpAdministradorRvBinding binding = SpAdministradorRvBinding.inflate(inflater, parent, false);
        return new ClienteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Cliente cliente = lClientes.get(position);
        holder.cliente = cliente;

        TextView tvNombreCliente= holder.binding.textView1;
        TextView tvTelefonoCliente = holder.binding.textView2;

        tvNombreCliente.setText(cliente.getNombres());
        tvTelefonoCliente.setText(cliente.getTelefono());

        holder.binding.getRoot().setOnClickListener(view -> {
            Intent intent = new Intent(context, DetallesClienteActivity.class);
            intent.putExtra("cliente", cliente);
            intent.putExtra("posicion", position);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lClientes.size();
    }

    public void setListaClientes(List<Cliente> listaClientes) {
        this.lClientes = listaClientes;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public static class ClienteViewHolder extends RecyclerView.ViewHolder {
        SpAdministradorRvBinding binding;
        Cliente cliente;

        public ClienteViewHolder(SpAdministradorRvBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

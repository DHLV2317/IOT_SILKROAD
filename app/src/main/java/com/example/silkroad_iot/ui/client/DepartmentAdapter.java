package com.example.silkroad_iot.ui.client;

import android.view.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.silkroad_iot.databinding.ItemDepartmentBinding;
import com.example.silkroad_iot.data.Department;
import java.util.List;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.VH>{
    public interface OnClick { void onClick(Department d); }
    private final List<Department> data; 
    private final OnClick cb;
    
    public DepartmentAdapter(List<Department> data, OnClick cb){ 
        this.data=data; 
        this.cb=cb; 
    }
    
    static class VH extends RecyclerView.ViewHolder{
        ItemDepartmentBinding binding;
        VH(ItemDepartmentBinding binding){ 
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    
    @NonNull @Override 
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v){
        ItemDepartmentBinding binding = ItemDepartmentBinding.inflate(
            LayoutInflater.from(p.getContext()), p, false
        );
        return new VH(binding);
    }
    
    @Override 
    public void onBindViewHolder(@NonNull VH h, int i){
        Department d = data.get(i);
        h.binding.tTitle.setText(d.name);
        h.itemView.setOnClickListener(v -> cb.onClick(d));
    }
    
    @Override 
    public int getItemCount(){ 
        return data.size(); 
    }
}
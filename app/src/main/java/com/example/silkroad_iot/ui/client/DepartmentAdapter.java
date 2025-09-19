package com.example.silkroad_iot.ui.client;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.Department;
import java.util.List;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.VH>{
    public interface OnClick { void onClick(Department d); }
    private final List<Department> data; private final OnClick cb;
    public DepartmentAdapter(List<Department> data, OnClick cb){ this.data=data; this.cb=cb; }
    static class VH extends RecyclerView.ViewHolder{
        TextView t; VH(View v){ super(v); t=v.findViewById(R.id.tTitle); }
    }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p,int v){
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_department,p,false));
    }
    @Override public void onBindViewHolder(@NonNull VH h,int i){
        Department d=data.get(i); h.t.setText(d.name);
        h.itemView.setOnClickListener(v->cb.onClick(d));
    }
    @Override public int getItemCount(){ return data.size(); }
}
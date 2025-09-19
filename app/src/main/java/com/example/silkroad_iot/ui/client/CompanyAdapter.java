package com.example.silkroad_iot.ui.client;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.Company;

import java.util.ArrayList;
import java.util.List;
import com.bumptech.glide.Glide;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.VH>{
    private final List<Company> data;
    private final List<Company> fullList;
    public CompanyAdapter(List<Company> data) {
        this.data = new ArrayList<>(data);     // para poder modificarla
        this.fullList = new ArrayList<>(data); // copia completa
    }

    public void filterList(String query) {
        data.clear();
        if (query.isEmpty()) {
            data.addAll(fullList);
        } else {
            for (Company c : fullList) {
                if (c.getN().toLowerCase().contains(query.toLowerCase())) {
                    data.add(c);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView t1, t2;
        ImageView img;

        VH(View v) {
            super(v);
            t1 = v.findViewById(R.id.tTitle);
            t2 = v.findViewById(R.id.tRating);
            img = v.findViewById(R.id.imgCompany); // Este es el nuevo
        }
    }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p,int v){
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_company,p,false));
    }
    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        Company c = data.get(i);
        h.t1.setText(c.getN());  // o c.getName()
        h.t2.setText("* " + c.getR());  // o c.getRating()

        Glide.with(h.img.getContext())
                .load(c.getImageUrl()) // o c.getImageUrl()
                .into(h.img);

        h.itemView.setOnClickListener(v -> {
            Context ctx = v.getContext();
            Intent intent = new Intent(ctx, ToursActivity.class);
            intent.putExtra("company", c); // 'c' es la empresa actual
            ctx.startActivity(intent);
        });



    }
    @Override public int getItemCount(){ return data.size(); }
}
package com.example.silkroad_iot.ui.client;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silkroad_iot.data.TourOrder;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityTourHistoryBinding;

import java.util.List;

public class TourHistoryActivity extends AppCompatActivity {
    ActivityTourHistoryBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityTourHistoryBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        User u = UserStore.get().getLogged();
        if (u == null) return;

        List<TourOrder> orders = OrderStore.getOrdersByUser(u.getEmail());

        b.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        b.rvHistory.setAdapter(new TourOrderAdapter(orders));
    }
}


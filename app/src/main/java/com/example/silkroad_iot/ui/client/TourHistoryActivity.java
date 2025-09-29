package com.example.silkroad_iot.ui.client;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silkroad_iot.data.TourOrder;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityTourHistoryBinding;

import java.util.List;

public class TourHistoryActivity extends AppCompatActivity {
    ActivityTourHistoryBinding b;
    private ActivityResultLauncher<Intent> detailLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        detailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("updatedOrder")) {
                            TourOrder updated = (TourOrder) data.getSerializableExtra("updatedOrder");

                            // Encuentra y actualiza la lista original
                            List<TourOrder> currentOrders = ((TourOrderAdapter) b.rvHistory.getAdapter()).getOrders();
                            for (int i = 0; i < currentOrders.size(); i++) {
                                if (currentOrders.get(i).createdAt.equals(updated.createdAt)) {
                                    currentOrders.set(i, updated);
                                    b.rvHistory.getAdapter().notifyItemChanged(i);
                                    break;
                                }
                            }
                        }
                    }
                }
        );


        super.onCreate(savedInstanceState);
        b = ActivityTourHistoryBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // Usar el toolbar del binding
        setSupportActionBar(b.toolbar);

        // Título (opcional: puedes ponerlo en XML o aquí)
        getSupportActionBar().setTitle("TourHistory");

        User u = UserStore.get().getLogged();
        if (u == null) return;

        List<TourOrder> orders = OrderStore.getOrdersByUser(u.getEmail());


        TourOrderAdapter adapter = new TourOrderAdapter(orders, order -> {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra("order", order);
            detailLauncher.launch(intent);
        });
        b.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        b.rvHistory.setAdapter(adapter);
    }
}


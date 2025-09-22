package com.example.silkroad_iot.ui.client;

import com.example.silkroad_iot.data.TourOrder;

import java.util.ArrayList;
import java.util.List;

public class OrderStore {
    private static final List<TourOrder> orders = new ArrayList<>();

    public static void addOrder(TourOrder order) {
        orders.add(order);
    }

    public static List<TourOrder> getOrdersByUser(String email) {
        List<TourOrder> result = new ArrayList<>();
        for (TourOrder o : orders) {
            if (o.userEmail.equals(email)) {
                result.add(o);
            }
        }
        return result;
    }



}


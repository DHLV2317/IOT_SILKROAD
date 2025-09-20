package com.example.silkroad_iot.data;

import java.io.Serializable;
import java.util.Date;

public class TourOrder implements Serializable {
    public Tour tour;
    public int quantity;
    public Date date; // fecha de compra
    public String userEmail;

    public TourOrder(Tour tour, int quantity, Date date, String userEmail) {
        this.tour = tour;
        this.quantity = quantity;
        this.date = date;
        this.userEmail = userEmail;
    }
}


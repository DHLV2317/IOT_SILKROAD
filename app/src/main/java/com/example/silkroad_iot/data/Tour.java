package com.example.silkroad_iot.data;

import java.io.Serializable;
import java.util.Date;

public class Tour implements Serializable {
    public String name;
    public double price;
    public int people;
    public String description;
    public String imageUrl;
    public double rating;

    public Date FechaTour;

    public Tour(String name, double price, int people, String description, String imageUrl, double rating, Date fechaTour) {
        this.name = name;
        this.price = price;
        this.people = people;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rating = rating;
        FechaTour = fechaTour;
    }

}


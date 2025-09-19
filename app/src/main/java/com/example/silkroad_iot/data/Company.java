package com.example.silkroad_iot.data;
public class Company {
    private String n;  // nombre
    private double r;  // rating
    private String imageUrl; // nueva propiedad

    public Company(String n, double r, String imageUrl) {
        this.n = n;
        this.r = r;
        this.imageUrl = imageUrl;
    }

    public String getN() {
        return n;
    }

    public double getR() {
        return r;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
package com.example.silkroad_iot.data;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

@IgnoreExtraProperties
public class ServiceFB implements Serializable {

    private String name;
    private Boolean included;
    private Double price;

    public ServiceFB() {}

    public ServiceFB(String name, Boolean included, Double price) {
        this.name = name;
        this.included = included;
        this.price = price;
    }

    @PropertyName("name")
    public String getName() { return name; }
    @PropertyName("name")
    public void setName(String name) { this.name = name; }

    @PropertyName("included")
    public Boolean getIncluded() { return included; }
    @PropertyName("included")
    public void setIncluded(Boolean included) { this.included = included; }

    @PropertyName("price")
    public Double getPrice() { return price; }
    @PropertyName("price")
    public void setPrice(Double price) { this.price = price; }
}
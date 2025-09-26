package com.example.silkroad_iot.data;

import java.io.Serializable;

public class Stop implements Serializable {
    public String name;
    public String address;
    public String time;     // Ejemplo: "20 min"
    public double cost;

    public Stop(String name, String address, String time, double cost) {
        this.name = name;
        this.address = address;
        this.time = time;
        this.cost = cost;
    }
}

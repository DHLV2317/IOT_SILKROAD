package com.example.silkroad_iot.data;

import java.io.Serializable;

public class ReservaWithTour implements Serializable {

    private final TourHistorialFB reserva;
    private final TourFB tour;

    public ReservaWithTour(TourHistorialFB reserva, TourFB tour) {
        this.reserva = reserva;
        this.tour = tour;
    }

    public TourHistorialFB getReserva() { return reserva; }
    public TourFB getTour() { return tour; }
}
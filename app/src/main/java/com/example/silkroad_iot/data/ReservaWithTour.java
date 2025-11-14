package com.example.silkroad_iot.data;

import java.io.Serializable;

public class ReservaWithTour implements Serializable {

    private TourHistorialFB reserva;
    private TourFB tour;

    public ReservaWithTour() {}

    public ReservaWithTour(TourHistorialFB reserva, TourFB tour) {
        this.reserva = reserva;
        this.tour = tour;
    }

    public TourHistorialFB getReserva() { return reserva; }
    public void setReserva(TourHistorialFB reserva) { this.reserva = reserva; }

    public TourFB getTour() { return tour; }
    public void setTour(TourFB tour) { this.tour = tour; }
}
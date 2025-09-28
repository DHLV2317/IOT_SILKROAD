package com.example.silkroad_iot.data;

public class TourOffer {
    private String tourName;
    private String payment;
    private String companyName; // "El guía ve las empresas de turismo y los tours disponibles"

    public TourOffer(String tourName, String payment, String companyName) {
        this.tourName = tourName;
        this.payment = payment;
        this.companyName = companyName;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
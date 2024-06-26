package com.example.vetandaid.model;

public class VetSchedule extends Schedule {
    private String clientId;

    public VetSchedule() {
    }

    public VetSchedule(String clientId) {
        this.clientId = clientId;
    }

    public VetSchedule(String hour, String petId, String date, String clientId) {
        super(hour, petId, date);
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
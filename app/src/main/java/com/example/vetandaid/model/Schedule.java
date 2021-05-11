package com.example.vetandaid.model;

public class Schedule {
    private String hour;
    private String petId;
    private String date;

    public Schedule() {
    }

    public Schedule(String hour, String petId, String date) {
        this.hour = hour;
        this.petId = petId;
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
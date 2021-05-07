package com.example.vetandaid.model;

public class Schedule {
    private String hour;
    private String petId;
    private String weekDay;

    public Schedule() {
    }

    public Schedule(String hour, String petId, String weekDay) {
        this.hour = hour;
        this.petId = petId;
        this.weekDay = weekDay;
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

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }
}

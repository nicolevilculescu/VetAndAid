package com.example.vetandaid.model;

public class ClientSchedule extends Schedule {
    private String clinicId;

    public ClientSchedule() {
    }

    public ClientSchedule(String clinicId) {
        this.clinicId = clinicId;
    }

    public ClientSchedule(String hour, String petId, String weekDay, String clinicId) {
        super(hour, petId, weekDay);
        this.clinicId = clinicId;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }
}

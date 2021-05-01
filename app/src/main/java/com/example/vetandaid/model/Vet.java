package com.example.vetandaid.model;

public class Vet extends User {
    private String registration_number;
    private String clinic_name;
    private String openingHourWeek;
    private String closingHourWeek;
    private String openingHourSaturday;
    private String closingHourSaturday;
    private String openingHourSunday;
    private String closingHourSunday;

    public Vet() {
    }

    public Vet(String email, String password, String firstName, String lastName, String phone, String address, String accType,
               String registration_number, String clinic_name) {
        super(email, password, firstName, lastName, phone, address, accType);
        this.registration_number = registration_number;
        this.clinic_name = clinic_name;
        this.openingHourWeek = "-";
        this.closingHourWeek = "-";
        this.openingHourSaturday = "-";
        this.closingHourSaturday = "-";
        this.openingHourSunday = "-";
        this.closingHourSunday = "-";
    }

    public String getRegistration_number() {
        return registration_number;
    }

    public void setRegistration_number(String registration_number) {
        this.registration_number = registration_number;
    }

    public String getClinic_name() {
        return clinic_name;
    }

    public void setClinic_name(String clinic_name) {
        this.clinic_name = clinic_name;
    }

    public String getOpeningHourWeek() {
        return openingHourWeek;
    }

    public void setOpeningHourWeek(String openingHourWeek) {
        this.openingHourWeek = openingHourWeek;
    }

    public String getClosingHourWeek() {
        return closingHourWeek;
    }

    public void setClosingHourWeek(String closingHourWeek) {
        this.closingHourWeek = closingHourWeek;
    }

    public String getOpeningHourSaturday() {
        return openingHourSaturday;
    }

    public void setOpeningHourSaturday(String openingHourSaturday) {
        this.openingHourSaturday = openingHourSaturday;
    }

    public String getClosingHourSaturday() {
        return closingHourSaturday;
    }

    public void setClosingHourSaturday(String closingHourSaturday) {
        this.closingHourSaturday = closingHourSaturday;
    }

    public String getOpeningHourSunday() {
        return openingHourSunday;
    }

    public void setOpeningHourSunday(String openingHourSunday) {
        this.openingHourSunday = openingHourSunday;
    }

    public String getClosingHourSunday() {
        return closingHourSunday;
    }

    public void setClosingHourSunday(String closingHourSunday) {
        this.closingHourSunday = closingHourSunday;
    }
}

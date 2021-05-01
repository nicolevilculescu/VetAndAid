package com.example.vetandaid.model;

public class MedicalHistory {
    private String date;
    private String problem;
    private String treatment;
    private String description;

    public MedicalHistory() {}

    public MedicalHistory(String date, String problem, String treatment, String description) {
        this.date = date;
        this.problem = problem;
        this.treatment = treatment;
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

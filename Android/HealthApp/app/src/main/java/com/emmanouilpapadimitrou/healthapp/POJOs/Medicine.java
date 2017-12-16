package com.emmanouilpapadimitrou.healthapp.POJOs;


import com.google.firebase.database.Exclude;

public class Medicine {
    private String id;
    private String name;
    private String doctor;
    private String date;
    private String dose;
    private String frequency;

    public Medicine() {
    }

    public Medicine(String id, String name, String doctor, String date, String dose, String frequency) {
        this.id = id;
        this.name = name;
        this.doctor = doctor;
        this.date = date;
        this.dose = dose;
        this.frequency = frequency;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public String getDoctor() {
        return doctor;
    }

    @Exclude
    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}

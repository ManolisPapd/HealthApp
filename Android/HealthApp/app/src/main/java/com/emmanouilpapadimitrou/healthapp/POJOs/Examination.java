package com.emmanouilpapadimitrou.healthapp.POJOs;


import com.google.firebase.database.Exclude;

public class Examination {
    private String id;
    private String name;
    private String date;
    private String doctor;


    public Examination() {
    }

    public Examination(String id, String name, String date, String doctor) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.doctor = doctor;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Exclude
    public String getDoctor() {
        return doctor;
    }

    @Exclude
    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }
}


package com.emmanouilpapadimitrou.healthapp.POJOs;

import com.google.firebase.database.Exclude;

public class History {
    private String id;
    private String date;
    private String type;
    private String typeId;
    private String doctor;
    private String condition;

    public History() {
    }

    public History(String id, String date, String type, String typeId, String doctor, String condition) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.typeId = typeId;
        this.doctor = doctor;
        this.condition = condition;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Exclude
    public String getType() {
        return type;
    }

    @Exclude
    public void setType(String type) {
        this.type = type;
    }

    @Exclude
    public String getDoctor() {
        return doctor;
    }

    @Exclude
    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    @Exclude
    public String getTypeId() {
        return typeId;
    }

    @Exclude
    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}

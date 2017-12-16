package com.emmanouilpapadimitrou.healthapp.POJOs;

import com.google.firebase.database.Exclude;

public class Note {
    private String id;
    private String date;
    private String info;
    private String user;

    public Note() {
    }

    public Note(String id, String date, String info, String user) {
        this.id = id;
        this.date = date;
        this.info = info;
        this.user = user;
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Exclude
    public String getUser() {
        return user;
    }

    @Exclude
    public void setUser(String user) {
        this.user = user;
    }
}

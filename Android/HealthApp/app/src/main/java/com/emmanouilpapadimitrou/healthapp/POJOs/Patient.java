package com.emmanouilpapadimitrou.healthapp.POJOs;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Patient implements Parcelable {
    private String id;
    private String name;
    private String surname;
    private String fathername;
    private String gender;
    private String birthdate;
    private String entrydate;
    private String ethnicity;
    private String town;
    private String address;
    private String tk;
    private String telephone1;
    private String telephone2;

    private ArrayList<String> users;

    public Patient() {
    }

    protected Patient(Parcel in) {
        id = in.readString();
        name = in.readString();
        surname = in.readString();
        fathername = in.readString();
        gender = in.readString();
        birthdate = in.readString();
        entrydate = in.readString();
        ethnicity = in.readString();
        town = in.readString();
        address = in.readString();
        tk = in.readString();
        telephone1 = in.readString();
        telephone2 = in.readString();
        users = in.createStringArrayList();
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFathername() {
        return fathername;
    }

    public void setFathername(String fathername) {
        this.fathername = fathername;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getEntrydate() {
        return entrydate;
    }

    public void setEntrydate(String entryDate) {
        this.entrydate = entryDate;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTk() {
        return tk;
    }

    public void setTk(String tk) {
        this.tk = tk;
    }

    public String getTelephone1() {
        return telephone1;
    }

    public void setTelephone1(String telephone1) {
        this.telephone1 = telephone1;
    }

    public String getTelephone2() {
        return telephone2;
    }

    public void setTelephone2(String telephone2) {
        this.telephone2 = telephone2;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(fathername);
        dest.writeString(gender);
        dest.writeString(birthdate);
        dest.writeString(entrydate);
        dest.writeString(ethnicity);
        dest.writeString(town);
        dest.writeString(address);
        dest.writeString(tk);
        dest.writeString(telephone1);
        dest.writeString(telephone2);
        dest.writeStringList(users);
    }
}

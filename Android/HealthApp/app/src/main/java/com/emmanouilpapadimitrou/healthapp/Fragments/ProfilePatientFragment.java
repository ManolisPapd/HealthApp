package com.emmanouilpapadimitrou.healthapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emmanouilpapadimitrou.healthapp.POJOs.Patient;
import com.emmanouilpapadimitrou.healthapp.Activities.PatientsActivity;
import com.emmanouilpapadimitrou.healthapp.R;


public class ProfilePatientFragment extends Fragment {
    private TextView name_input;
    private TextView surname_input;
    private TextView fathername_input;
    private TextView gender_input;
    private TextView birthdate_input;
    private TextView entrydate_input;
    private TextView ethnicity_input;
    private TextView town_input;
    private TextView address_input;
    private TextView tk_input;
    private TextView telephone1_input;
    private TextView telephone2_input;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_patient,container,false);

        //Παίρνουμε τον επιλεγμένο χρήστη με όλα του τα στοιχεία
        Patient patient = ((PatientsActivity)getActivity()).getCurrentPatient();

        //Συνδέουμε τις μεταβλητές με το layout για την προβολή στοιχείων
        name_input = (TextView) view.findViewById(R.id.name_input);
        surname_input = (TextView) view.findViewById(R.id.surname_input);
        fathername_input = (TextView) view.findViewById(R.id.fathername_input);
        gender_input = (TextView) view.findViewById(R.id.gender_input);
        birthdate_input = (TextView) view.findViewById(R.id.birthdate_input);
        entrydate_input = (TextView) view.findViewById(R.id.entrydate_input);
        ethnicity_input = (TextView) view.findViewById(R.id.ethnicity_input);
        town_input = (TextView) view.findViewById(R.id.town_input);
        address_input = (TextView) view.findViewById(R.id.address_input);
        tk_input = (TextView) view.findViewById(R.id.tk_input);
        telephone1_input = (TextView) view.findViewById(R.id.telephone1_input);
        telephone2_input = (TextView) view.findViewById(R.id.telephone2_input);

        //Προβάλουμε τις πληροφορίες του ασθενούς στον χρήστη
        name_input.setText(patient.getName());
        surname_input.setText(patient.getSurname());
        fathername_input.setText(patient.getFathername());
        gender_input.setText(patient.getGender());
        birthdate_input.setText(patient.getBirthdate());
        entrydate_input.setText(patient.getEntrydate());
        ethnicity_input.setText(patient.getEthnicity());
        town_input.setText(patient.getTown());
        address_input.setText(patient.getAddress());
        tk_input.setText(patient.getTk());
        telephone1_input.setText(patient.getTelephone1());
        telephone2_input.setText(patient.getTelephone2());


        return view;
    }
}

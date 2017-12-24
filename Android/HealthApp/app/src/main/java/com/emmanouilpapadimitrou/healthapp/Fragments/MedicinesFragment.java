package com.emmanouilpapadimitrou.healthapp.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.emmanouilpapadimitrou.healthapp.Adapters.MedicinesAdapter;
import com.emmanouilpapadimitrou.healthapp.POJOs.History;
import com.emmanouilpapadimitrou.healthapp.POJOs.Medicine;
import com.emmanouilpapadimitrou.healthapp.POJOs.Patient;
import com.emmanouilpapadimitrou.healthapp.POJOs.Users;
import com.emmanouilpapadimitrou.healthapp.Activities.PatientsActivity;
import com.emmanouilpapadimitrou.healthapp.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MedicinesFragment extends Fragment {
    private String userID;
    private FirebaseAuth firebaseAuth;
    private Firebase database;
    private ListView medicinesList;
    private ArrayList<Medicine> allMedicines;
    private FloatingActionButton addMedicineBtn;
    private Users currentUser;
    private DatabaseReference referenceDB;
    private Patient patient;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicines,container,false);


        //Παίρνουμε τον χρήστη
        currentUser = ((PatientsActivity)getActivity()).getCurrentUser();


        //Ορισμός της λίστας που περιέχει όλα τα αντικείμενα των φαρμάκων
        allMedicines = new ArrayList<Medicine>();

        //Μεταβλητή που μας δίνει τα στοιχεία του συνδεδεμένου χρήστη
        firebaseAuth = FirebaseAuth.getInstance();

        //Ανάθεση του ID σε μεταβλητή για μελλοντική χρήστη
        userID = firebaseAuth.getCurrentUser().getUid();

        //Ορισμός της βάσης στην μεταβλητή για οποιαδήποτε μελλοντική χρήστη
        database = new Firebase("https://healthapp-f2bba.firebaseio.com/");
        referenceDB =  FirebaseDatabase.getInstance().getReference();

        //Σύνδεση μεταβλητής με την λίστα στο layout
        medicinesList = (ListView) view.findViewById(R.id.medicinesList);

        //Παίρνουμε τον επιλεγμένο χρήστη με όλα του τα στοιχεία
        patient = ((PatientsActivity)getActivity()).getCurrentPatient();

        //Παίρνουμε όλα τα φάρμακα από την βάση
        database.child("medicines").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for(final DataSnapshot medicine : dataSnapshot.getChildren()){
                    final Medicine m = new Medicine();

                    for(final DataSnapshot medicineChild : medicine.child("users").getChildren()){
                        //Θα βρούμε το όνομα του γιατρού από το id
                        final String docID = String.valueOf(medicineChild.getKey());

                        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                for(DataSnapshot doctorTemp : dataSnapshot1.getChildren()){
                                    if(docID.equals(String.valueOf(doctorTemp.getKey()))){
                                        m.setId(String.valueOf(medicine.getKey()));
                                        m.setName(String.valueOf( medicine.child("name").getValue()));
                                        m.setDate(String.valueOf( medicine.child("date").getValue()));
                                        m.setDose(String.valueOf( medicine.child("dose").getValue()));
                                        m.setFrequency(String.valueOf( medicine.child("frequency").getValue()));
                                        m.setDoctor("Δρ. " + String.valueOf(doctorTemp.child("name").getValue())+" " + String.valueOf(doctorTemp.child("surname").getValue()));



                                        for(DataSnapshot patientTempID : medicine.child("patients").getChildren()){
                                            //Αν είναι ο χρήστης που επιλέχθηκε βάλε το φάρμακο στην λίστα
                                            if(patient.getId().equals(String.valueOf(patientTempID.getKey()))){
                                                allMedicines.add(m);
                                            }

                                        }


                                    }
                                }

                                //Εμφανίζουμε όλα τα φάρμακα του ασθενούς με τις απαραίτητες πληροφορίες

                                // Create the adapter to convert the array to views
                                MedicinesAdapter adapter = new MedicinesAdapter(getActivity(),allMedicines);
                                // Attach the adapter to a ListView
                                medicinesList.setAdapter(adapter);


                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }


                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        //Κουμπί εισαγωγής νέου φαρμάκου
        addMedicineBtn = (FloatingActionButton) view.findViewById(R.id.addMedicineBtn);
        addMedicineBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Το κουμπί πατήθηκε
                if (currentUser.getType().equals("doctor")) {
                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                    View mView = getLayoutInflater().inflate(R.layout.medicine_dialog, null);
                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    final EditText medicineInputName = (EditText) mView.findViewById(R.id.medicineInputName);
                    final EditText doseInputName = (EditText) mView.findViewById(R.id.doseInputName);
                    final EditText frequencyInputName = (EditText) mView.findViewById(R.id.frequencyInputName);

                    //Κουμπιά
                    Button inputButton = (Button) mView.findViewById(R.id.inputButton);
                    Button denyButton = (Button) mView.findViewById(R.id.denyButton);

                    //Κουμπί για πίσω
                    denyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    inputButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Ελέγχουμε αν είναι άδεια τα πεδία
                            if (!medicineInputName.getText().toString().isEmpty()
                                    && !doseInputName.getText().toString().isEmpty()
                                    && !frequencyInputName.getText().toString().isEmpty()) {


                                //Βρίσκουμε πόσα φάρμακα υπάρχουν στην βάση ώστε να φτιάξουμε το νέο id για το φάρμακο
                                database.child("medicines").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (final DataSnapshot medTmp : dataSnapshot.getChildren()) {
                                            String currentMedId = medTmp.getKey();
                                            currentMedId = currentMedId.substring(currentMedId.length() - 6);
                                            int newIntMedicineId = Integer.parseInt(currentMedId) + 1;

                                            //Βάζουμε τα απαραίτητα μηδενικά ώστε να υπακούει στο format του id
                                            String newStringMedicineId = String.valueOf(newIntMedicineId);

                                            String zeroes = "";
                                            switch (newStringMedicineId.length()) {
                                                case 1:
                                                    zeroes += "00000";
                                                    break;
                                                case 2:
                                                    zeroes += "0000";
                                                    break;
                                                case 3:
                                                    zeroes += "000";
                                                    break;
                                                case 4:
                                                    zeroes += "00";
                                                    break;
                                                case 5:
                                                    zeroes += "0";
                                                    break;
                                            }

                                            newStringMedicineId = "med" + zeroes + newStringMedicineId;


                                            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                            String date = df.format(Calendar.getInstance().getTime());


                                            final Medicine newMedicine = new Medicine(newStringMedicineId,
                                                    medicineInputName.getText().toString(),
                                                    "Δρ. " + currentUser.getName() + " " + currentUser.getSurname(),
                                                    date,
                                                    doseInputName.getText().toString(),
                                                    frequencyInputName.getText().toString()
                                            );

                                            //Βάζουμε τα στοιχεία του νέου φάρμακος στο πεδίο medicines
                                            referenceDB.child("medicines")
                                                    .child(newMedicine.getId())
                                                    .setValue(newMedicine);

                                            referenceDB.child("medicines")
                                                    .child(newMedicine.getId())
                                                    .child("patients")
                                                    .child(patient.getId())
                                                    .setValue(true);

                                            referenceDB.child("medicines")
                                                    .child(newMedicine.getId())
                                                    .child("users")
                                                    .child(userID)
                                                    .setValue(true);


                                            //Ενημέρωση ιστορικού
                                            //Δημιουργία id νέου ιστορικού
                                            final String finalNewStringMedId = newStringMedicineId;
                                            database.child("history").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (final DataSnapshot histId : dataSnapshot.getChildren()) {
                                                        String currentHistId = histId.getKey();
                                                        currentHistId = currentHistId.substring(currentHistId.length() - 11);
                                                        int newIntHistoryId = Integer.parseInt(currentHistId) + 1;

                                                        //Βάζουμε τα απαραίτητα μηδενικά ώστε να υπακούει στο format του id
                                                        String newStringHistoryId = String.valueOf(newIntHistoryId);

                                                        String zeroes = "";
                                                        switch (newStringHistoryId.length()) {
                                                            case 1:
                                                                zeroes += "0000000000";
                                                                break;
                                                            case 2:
                                                                zeroes += "000000000";
                                                                break;
                                                            case 3:
                                                                zeroes += "00000000";
                                                                break;
                                                            case 4:
                                                                zeroes += "0000000";
                                                                break;
                                                            case 5:
                                                                zeroes += "000000";
                                                                break;
                                                            case 6:
                                                                zeroes += "00000";
                                                                break;
                                                            case 7:
                                                                zeroes += "0000";
                                                                break;
                                                            case 8:
                                                                zeroes += "000";
                                                                break;
                                                            case 9:
                                                                zeroes += "00";
                                                                break;
                                                            case 10:
                                                                zeroes += "0";
                                                                break;

                                                        }

                                                        newStringHistoryId = "h" + zeroes + newStringHistoryId;

                                                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                                        String date = df.format(Calendar.getInstance().getTime());

                                                        History newHistory = new History(newStringHistoryId,
                                                                date,
                                                                "medicines",
                                                                newMedicine.getId(),
                                                                currentUser.getName() + " " + currentUser.getSurname(),
                                                                "create",
                                                                newMedicine.getName() + "\n" + newMedicine.getDose() + "\n" + newMedicine.getFrequency()
                                                        );


                                                        //Εισαγωγή νέου ιστορικού στην βάση
                                                        referenceDB.child("history")
                                                                .child(newHistory.getId())
                                                                .setValue(newHistory);

                                                        referenceDB.child("history")
                                                                .child(newHistory.getId())
                                                                .child("type")
                                                                .child("medicines")
                                                                .setValue(finalNewStringMedId);

                                                        referenceDB.child("history")
                                                                .child(newHistory.getId())
                                                                .child("patients")
                                                                .child(patient.getId())
                                                                .setValue(true);

                                                        referenceDB.child("history")
                                                                .child(newHistory.getId())
                                                                .child("users")
                                                                .child(userID)
                                                                .setValue(true);

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(FirebaseError firebaseError) {

                                                }
                                            });

                                        }
                                        Toast.makeText(getActivity(), "Εισαγωγή φάρμακος επιτυχής!", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();

                                        ((PatientsActivity) getActivity()).reloadCurrentFragment();
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });

                            } else {
                                Toast.makeText(getActivity(), "Μην αφήνετε πεδία κενά!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                    dialog.show();

                }
                else{
                    Toast.makeText(getActivity(), "Μόνο γιατρός μπορεί να εισάγει φάρμακα!", Toast.LENGTH_LONG).show();
                }
            }

        });



        //Διαγραφή φάρμακος

        //Ελέγχουμε ποιο φάρμακο πάτησε από την λίστα
        medicinesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final Medicine medicineSelected = (Medicine) medicinesList.getItemAtPosition(position);

                //Προβολή παραθύρου για επιβεβαίωση διαγραφής φαρμάκου
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.remove_confirmation_dialog,null);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                //Κουμπιά
                Button denyButton = (Button) mView.findViewById(R.id.denyButton);
                Button confirmButton = (Button) mView.findViewById(R.id.confirmButton);

                //Κουμπί για πίσω
                denyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //Κουμπί για επιβεβαίωση διαγραφής
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Διαγραφή από το πεδίο medicines
                        database.child("medicines").child(medicineSelected.getId()).removeValue();

                        //Ενημέρωση ιστορικού
                        //Δημιουργία id νέου ιστορικού

                        database.child("history").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(final DataSnapshot histId : dataSnapshot.getChildren()) {
                                    String currentHistId = histId.getKey();
                                    currentHistId = currentHistId.substring(currentHistId.length() - 11);
                                    int newIntHistoryId = Integer.parseInt(currentHistId) + 1;

                                    //Βάζουμε τα απαραίτητα μηδενικά ώστε να υπακούει στο format του id
                                    String newStringHistoryId = String.valueOf(newIntHistoryId);

                                    String zeroes = "";
                                    switch (newStringHistoryId.length()) {
                                        case 1:
                                            zeroes += "0000000000";
                                            break;
                                        case 2:
                                            zeroes += "000000000";
                                            break;
                                        case 3:
                                            zeroes += "00000000";
                                            break;
                                        case 4:
                                            zeroes += "0000000";
                                            break;
                                        case 5:
                                            zeroes += "000000";
                                            break;
                                        case 6:
                                            zeroes += "00000";
                                            break;
                                        case 7:
                                            zeroes += "0000";
                                            break;
                                        case 8:
                                            zeroes += "000";
                                            break;
                                        case 9:
                                            zeroes += "00";
                                            break;
                                        case 10:
                                            zeroes += "0";
                                            break;

                                    }

                                    newStringHistoryId ="h"+ zeroes + newStringHistoryId;

                                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                    String date = df.format(Calendar.getInstance().getTime());

                                    History newHistory = new History(newStringHistoryId,
                                            date,
                                            "medicines",
                                            medicineSelected.getId(),
                                            currentUser.getName() +" " + currentUser.getSurname(),
                                            "delete",
                                            medicineSelected.getName() +"\n"+medicineSelected.getDose() +"\n" + medicineSelected.getFrequency()

                                    );


                                    //Εισαγωγή νέου ιστορικού στην βάση
                                    referenceDB.child("history")
                                            .child(newHistory.getId())
                                            .setValue(newHistory);

                                    referenceDB.child("history")
                                            .child(newHistory.getId())
                                            .child("type")
                                            .child("medicines")
                                            .setValue(newHistory.getTypeId());

                                    referenceDB.child("history")
                                            .child(newHistory.getId())
                                            .child("patients")
                                            .child(patient.getId())
                                            .setValue(true);

                                    referenceDB.child("history")
                                            .child(newHistory.getId())
                                            .child("users")
                                            .child(userID)
                                            .setValue(true);

                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });



                        Toast.makeText(getActivity(),"Επιτυχής διαγραφή!",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        ((PatientsActivity)getActivity()).reloadCurrentFragment();


                    }
                });





                dialog.show();


                return false;
            }
        });


        return view;
    }
}

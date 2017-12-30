package com.emmanouilpapadimitrou.healthapp.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.emmanouilpapadimitrou.healthapp.Activities.PatientsActivity;
import com.emmanouilpapadimitrou.healthapp.Adapters.ExaminationsAdapter;
import com.emmanouilpapadimitrou.healthapp.Adapters.HistoryAdapter;
import com.emmanouilpapadimitrou.healthapp.POJOs.*;
import com.emmanouilpapadimitrou.healthapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


public class HistoryFragment extends Fragment {

    private ArrayList<History> allHistory;
    private FirebaseAuth firebaseAuth;
    private String userID;
    private DatabaseReference referenceDB;
    private ListView historyList;
    private Users currentUser;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history,container,false);


        //Παίρνουμε τον χρήστη
        currentUser = ((PatientsActivity)getActivity()).getCurrentUser();


        //Ορισμός της λίστας που περιέχει όλα τα αντικείμενα των εξετάσεων
        allHistory = new ArrayList<History>();

        //Μεταβλητή που μας δίνει τα στοιχεία του συνδεδεμένου χρήστη
        firebaseAuth = FirebaseAuth.getInstance();

        //Ανάθεση του ID σε μεταβλητή για μελλοντική χρήστη
        userID = firebaseAuth.getCurrentUser().getUid();


        //Ορισμός της βάσης στην μεταβλητή για οποιαδήποτε μελλοντική χρήστη
        referenceDB =  FirebaseDatabase.getInstance().getReference();

        //Σύνδεση μεταβλητής με την λίστα στο layout
        historyList = (ListView) view.findViewById(R.id.historyList);

        //Παίρνουμε τον επιλεγμένο χρήστη με όλα του τα στοιχεία
        final Patient patient = ((PatientsActivity)getActivity()).getCurrentPatient();

        //Παίρνουμε όλες τις εξετάσεις από την βάση
        referenceDB.child("history").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(final DataSnapshot history : dataSnapshot.getChildren()){
                    final History h = new History();

                    for(final DataSnapshot historyChild : history.child("users").getChildren()){
                        //Θα βρούμε το όνομα του γιατρού από το id
                        final String docID = String.valueOf(historyChild.getKey());

                        referenceDB.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                for(DataSnapshot userTemp : dataSnapshot1.getChildren()){
                                    if(docID.equals(String.valueOf(userTemp.getKey()))){
                                        h.setId(String.valueOf(history.getKey()));
                                        for(DataSnapshot typeTempID : history.child("type").getChildren()){
                                            h.setType(String.valueOf(typeTempID.getKey()));
                                        }

                                        for(DataSnapshot typeTempID : history.child("type").getChildren()){
                                            h.setTypeId(String.valueOf(typeTempID.getValue()));
                                        }
                                        h.setDate(String.valueOf( history.child("date").getValue()));


                                        if(String.valueOf(userTemp.child("type").getValue()).equals("doctor")){
                                            h.setDoctor("Δρ. "+ String.valueOf(userTemp.child("name").getValue())+" " + String.valueOf(userTemp.child("surname").getValue()));
                                        }
                                        else{
                                            h.setDoctor(String.valueOf(userTemp.child("name").getValue())+" " + String.valueOf(userTemp.child("surname").getValue()));
                                        }
                                        
                                        h.setCondition(String.valueOf( history.child("condition").getValue()));

                                        for(DataSnapshot patientTempID : history.child("patients").getChildren()){
                                            //Αν είναι ο χρήστης που επιλέχθηκε βάλε την εξέταση στην λίστα
                                            if(patient.getId().equals(String.valueOf(patientTempID.getKey()))){
                                                allHistory.add(h);
                                            }

                                        }



                                    }
                                }


                                //Εμφανίζουμε όλες τις εξετάσεις του ασθενούς με τις απαραίτητες πληροφορίες

                                //Create the adapter to convert the array to views
                                HistoryAdapter adapter = new HistoryAdapter(getActivity(),allHistory);


                                // Attach the adapter to a ListView
                                historyList.setAdapter(adapter);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }


                        });
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });



        //Προβολή λεπτομερειών
        historyList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final History historySelected = (History) historyList.getItemAtPosition(position);

                //Προβολή παραθύρου για επιβεβαίωση διαγραφής φαρμάκου
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.view_history_details,null);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                //textview που θα προβληθούν οι λεπτομέρειες
                final TextView detailsView = (TextView) mView.findViewById(R.id.detailsView);

                //Κουμπιά
                Button denyButton = (Button) mView.findViewById(R.id.denyButton);

                //Παίρνουμε τις λεπτομέρειες του επιλεγμένου ιστορικού
                referenceDB.child("history").addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       for(final DataSnapshot history : dataSnapshot.getChildren()){

                           if(history.getKey().equals(historySelected.getId())){
                               detailsView.setText(String.valueOf(history.child("details").getValue()));
                           }


                       }
                   }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }


                });


                        //Κουμπί για πίσω
                        denyButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });




                dialog.show();
                return false;
            }
        });

        return view;
    }
}

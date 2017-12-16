package com.emmanouilpapadimitrou.healthapp.Fragments;



import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import android.widget.Toast;

import com.emmanouilpapadimitrou.healthapp.Activities.PatientsActivity;
import com.emmanouilpapadimitrou.healthapp.Adapters.NotesAdapter;
import com.emmanouilpapadimitrou.healthapp.POJOs.*;
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

public class NotesFragment extends Fragment {

    private ArrayList<Note> allNotes;
    private FirebaseAuth firebaseAuth;
    private String userID;
    private DatabaseReference referenceDB;
    private Firebase database;
    private ListView notesList;
    private FloatingActionButton addNotesBtn;
    private Users currentUser;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        //Παίρνουμε τον χρήστη
        currentUser = ((PatientsActivity)getActivity()).getCurrentUser();

        //Ορισμός της λίστας που περιέχει όλα τα αντικείμενα των εξετάσεων
        allNotes = new ArrayList<Note>();

        //Μεταβλητή που μας δίνει τα στοιχεία του συνδεδεμένου χρήστη
        firebaseAuth = FirebaseAuth.getInstance();

        //Ανάθεση του ID σε μεταβλητή για μελλοντική χρήστη
        userID = firebaseAuth.getCurrentUser().getUid();


        //Ορισμός της βάσης στην μεταβλητή για οποιαδήποτε μελλοντική χρήστη
        database = new Firebase("https://healthapp-f2bba.firebaseio.com/");
        referenceDB =  FirebaseDatabase.getInstance().getReference();

        //Σύνδεση μεταβλητής με την λίστα στο layout
        notesList = (ListView) view.findViewById(R.id.notesList);

        //Παίρνουμε τον επιλεγμένο χρήστη με όλα του τα στοιχεία
        final Patient patient = ((PatientsActivity)getActivity()).getCurrentPatient();

        //Παίρνουμε όλες τις σημειώσεις από την βάση
        database.child("notes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(final DataSnapshot note : dataSnapshot.getChildren()){
                    final Note n = new Note();

                    for(final DataSnapshot noteChild : note.child("users").getChildren()){
                        //Θα βρούμε το όνομα του χρήστη από το id
                        final String userID = String.valueOf(noteChild.getKey());

                        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                for(DataSnapshot userTemp : dataSnapshot1.getChildren()){
                                    if(userID.equals(String.valueOf(userTemp.getKey()))){
                                        n.setId(String.valueOf(note.getKey()));
                                        n.setInfo(String.valueOf( note.child("info").getValue()));
                                        n.setDate(String.valueOf( note.child("date").getValue()));
                                        n.setUser(String.valueOf(userTemp.child("name").getValue())+" " + String.valueOf(userTemp.child("surname").getValue()));


                                        for(DataSnapshot patientTempID : note.child("patients").getChildren()){
                                            //Αν είναι ο χρήστης που επιλέχθηκε βάλε την εξέταση στην λίστα
                                            if(patient.getId().equals(String.valueOf(patientTempID.getKey()))){
                                                allNotes.add(n);
                                            }

                                        }


                                    }
                                }
                                //Εμφανίζουμε όλες τις εξετάσεις του ασθενούς με τις απαραίτητες πληροφορίες

                                // Create the adapter to convert the array to views
                                NotesAdapter adapter = new NotesAdapter(getActivity(),allNotes);
                                // Attach the adapter to a ListView
                                notesList.setAdapter(adapter);
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


        //Κουμπί εισαγωγής νέας σημείωσης
        addNotesBtn = (FloatingActionButton) view.findViewById(R.id.addNotesBtn);
        addNotesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Το κουμπί πατήθηκε
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.note_dialog,null);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                //Παίρνουμε την σημείωση του χρήστη
                final EditText noteInput = (EditText) mView.findViewById(R.id.noteInput);



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


                //Κουμπί εισαγωγής
                inputButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       //Τρέχουσα ημερομηνία
                       DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                       final String date = df.format(Calendar.getInstance().getTime());


                       //Ελέγχουμε αν είναι άδειο τα πεδίο
                       if(!noteInput.getText().toString().isEmpty()){
                           database.child("notes").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   for(final DataSnapshot noteTmp : dataSnapshot.getChildren()){
                                       String currentNoteId = noteTmp.getKey();
                                       currentNoteId = currentNoteId.substring(currentNoteId.length() - 6);
                                       int newIntNoteId = Integer.parseInt(currentNoteId) + 1;

                                       //Βάζουμε τα απαραίτητα μηδενικά ώστε να υπακούει στο format του id
                                       String newStringNoteId = String.valueOf(newIntNoteId);

                                       String zeroes = "";
                                       switch(newStringNoteId.length()){
                                           case 1:
                                               zeroes+="00000";
                                               break;
                                           case 2:
                                               zeroes+="0000";
                                               break;
                                           case 3:
                                               zeroes+="000";
                                               break;
                                           case 4:
                                               zeroes+="00";
                                               break;
                                           case 5:
                                               zeroes+="0";
                                               break;
                                       }

                                       newStringNoteId ="note"+ zeroes + newStringNoteId;

                                       final Note newNote = new Note(newStringNoteId,
                                               date,
                                               noteInput.getText().toString(),
                                               currentUser.getName() +" " + currentUser.getSurname());


                                       //Βάζουμε τα στοιχεία της σημείωσης στο πεδίο notes
                                       referenceDB.child("notes")
                                               .child(newNote.getId())
                                               .setValue(newNote);

                                       referenceDB.child("notes")
                                               .child(newNote.getId())
                                               .child("patients")
                                               .child(patient.getId())
                                               .setValue(true);

                                       referenceDB.child("notes")
                                               .child(newNote.getId())
                                               .child("users")
                                               .child(userID)
                                               .setValue(true);


                                       //Ενημέρωση ιστορικού
                                       //Δημιουργία id νέου ιστορικού
                                       final String finalNewStringNoteId = newStringNoteId;
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
                                                           "examinations",
                                                           newNote.getId(),
                                                           currentUser.getName() +" " + currentUser.getSurname(),
                                                           "create"
                                                   );


                                                   //Εισαγωγή νέου ιστορικού στην βάση
                                                   referenceDB.child("history")
                                                           .child(newHistory.getId())
                                                           .setValue(newHistory);

                                                   referenceDB.child("history")
                                                           .child(newHistory.getId())
                                                           .child("type")
                                                           .child("notes")
                                                           .setValue(finalNewStringNoteId);

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

                                   Toast.makeText(getActivity(), "Εισαγωγή σημείωσης επιτυχής!", Toast.LENGTH_LONG).show();
                                   dialog.dismiss();
                               }

                               @Override
                               public void onCancelled(FirebaseError firebaseError) {

                               }
                           });
                       }
                       else{
                           Toast.makeText(getActivity(),"Μην αφήνετε πεδία κενά!",Toast.LENGTH_LONG).show();
                       }

                   }
               });







                dialog.show();

            }
        });


        //Διαγραφή Σημείωσης

        //Ελέγχουμε ποια σημείωση πάτησε από την λίστα
        notesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Note noteSelected = (Note) notesList.getItemAtPosition(position);

                //Προβολή παραθύρου για επιβεβαίωση διαγραφής φαρμάκου
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.remove_confirmation_dialog,null);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                TextView removeMedicineView = (TextView) mView.findViewById(R.id.removeMedicineView);
                removeMedicineView.setText("Διαγραφή σημείωσης;\nΔεν μπορεί να αναιρεθεί!");

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
                        database.child("notes").child(noteSelected.getId()).removeValue();

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
                                            "examinations",
                                            noteSelected.getId(),
                                            currentUser.getName() +" " + currentUser.getSurname(),
                                            "delete"
                                    );


                                    //Εισαγωγή νέου ιστορικού στην βάση
                                    referenceDB.child("history")
                                            .child(newHistory.getId())
                                            .setValue(newHistory);

                                    referenceDB.child("history")
                                            .child(newHistory.getId())
                                            .child("type")
                                            .child("notes")
                                            .setValue(noteSelected.getId());

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

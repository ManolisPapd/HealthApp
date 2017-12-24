package com.emmanouilpapadimitrou.healthapp.Fragments;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.emmanouilpapadimitrou.healthapp.Adapters.ExaminationsAdapter;
import com.emmanouilpapadimitrou.healthapp.POJOs.Examination;
import com.emmanouilpapadimitrou.healthapp.POJOs.History;
import com.emmanouilpapadimitrou.healthapp.POJOs.Patient;
import com.emmanouilpapadimitrou.healthapp.POJOs.Users;
import com.emmanouilpapadimitrou.healthapp.Activities.PatientsActivity;
import com.emmanouilpapadimitrou.healthapp.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ΕxaminationsFragment extends Fragment {

    private ArrayList<Examination> allExaminations;
    private FirebaseAuth firebaseAuth;
    private String userID;
    private DatabaseReference referenceDB;
    private Firebase database;
    private ListView examinationsList;
    private FloatingActionButton addExaminationsBtn;
    private Users currentUser;
    private ImageView resultsImage;
    private StorageReference storage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_examinations, container, false);

        //Παίρνουμε τον χρήστη
        currentUser = ((PatientsActivity)getActivity()).getCurrentUser();

        //Γίνεται reference με τα αποθηκευμένα αρχεία στο firebase
        storage = FirebaseStorage.getInstance().getReference();

        //Ορισμός της λίστας που περιέχει όλα τα αντικείμενα των εξετάσεων
        allExaminations = new ArrayList<Examination>();

        //Μεταβλητή που μας δίνει τα στοιχεία του συνδεδεμένου χρήστη
        firebaseAuth = FirebaseAuth.getInstance();

        //Ανάθεση του ID σε μεταβλητή για μελλοντική χρήστη
        userID = firebaseAuth.getCurrentUser().getUid();


        //Ορισμός της βάσης στην μεταβλητή για οποιαδήποτε μελλοντική χρήστη
        database = new Firebase("https://healthapp-f2bba.firebaseio.com/");
        referenceDB =  FirebaseDatabase.getInstance().getReference();

        //Σύνδεση μεταβλητής με την λίστα στο layout
        examinationsList = (ListView) view.findViewById(R.id.examinationsList);

        //Παίρνουμε τον επιλεγμένο χρήστη με όλα του τα στοιχεία
        final Patient patient = ((PatientsActivity)getActivity()).getCurrentPatient();

        //Παίρνουμε όλες τις εξετάσεις από την βάση
        database.child("examinations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(final DataSnapshot examination : dataSnapshot.getChildren()){
                    final Examination e = new Examination();

                    for(final DataSnapshot examinationChild : examination.child("users").getChildren()){
                        //Θα βρούμε το όνομα του γιατρού από το id
                        final String docID = String.valueOf(examinationChild.getKey());

                        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                for(DataSnapshot doctorTemp : dataSnapshot1.getChildren()){
                                    if(docID.equals(String.valueOf(doctorTemp.getKey()))){
                                        e.setId(String.valueOf(examination.getKey()));
                                        e.setName(String.valueOf( examination.child("name").getValue()));
                                        e.setDate(String.valueOf( examination.child("date").getValue()));
                                        e.setDoctor(String.valueOf(doctorTemp.child("name").getValue())+" " + String.valueOf(doctorTemp.child("surname").getValue()));


                                        for(DataSnapshot patientTempID : examination.child("patients").getChildren()){
                                            //Αν είναι ο χρήστης που επιλέχθηκε βάλε την εξέταση στην λίστα
                                            if(patient.getId().equals(String.valueOf(patientTempID.getKey()))){
                                                allExaminations.add(e);
                                            }

                                        }


                                    }
                                }


                                //Εμφανίζουμε όλες τις εξετάσεις του ασθενούς με τις απαραίτητες πληροφορίες

                                // Create the adapter to convert the array to views
                                ExaminationsAdapter adapter = new ExaminationsAdapter(getActivity(),allExaminations);
                                // Attach the adapter to a ListView
                                examinationsList.setAdapter(adapter);
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


        //Κουμπί εισαγωγής νέας εξέτασης
        addExaminationsBtn = (FloatingActionButton) view.findViewById(R.id.addExaminationsBtn);
        addExaminationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Το κουμπί πατήθηκε
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.examination_dialog,null);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                final EditText examinationInputName = (EditText) mView.findViewById(R.id.examinationInputName);

                //Αντιστοιχία widget DatePicker στο layout
                final DatePicker datePicker = (DatePicker) mView.findViewById(R.id.datePicker);


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
                       //Παίρνουμε τα στοιχεία τις ημερομηνίας που επιλέχθηκε
                       final int day  = datePicker.getDayOfMonth();
                       final int month= datePicker.getMonth();
                       final int year = datePicker.getYear()-1900;
                       SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                       final String formatedDate = sdf.format(new Date(year, month, day));

                       //Ελέγχουμε αν είναι άδειο τα πεδίο
                       if(!examinationInputName.getText().toString().isEmpty()){
                           //Βρίσκουμε πόσες εξετάσεις υπάρχουν στην βάση ώστε να φτιάξουμε το νέο id για την εξέταση
                           database.child("examinations").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {
                                 for(final DataSnapshot examTmp : dataSnapshot.getChildren()){
                                     String currentExamId = examTmp.getKey();
                                     currentExamId = currentExamId.substring(currentExamId.length() - 6);
                                     int newIntExamId = Integer.parseInt(currentExamId) + 1;

                                     //Βάζουμε τα απαραίτητα μηδενικά ώστε να υπακούει στο format του id
                                     String newStringExamId = String.valueOf(newIntExamId);

                                     String zeroes = "";
                                     switch(newStringExamId.length()){
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

                                     newStringExamId ="ex"+ zeroes + newStringExamId;

                                     final Examination newExamination = new Examination(newStringExamId,
                                             examinationInputName.getText().toString(),
                                             formatedDate,
                                             currentUser.getName() +" " + currentUser.getSurname());



                                     //Βάζουμε τα στοιχεία της νέας εξέτασης στο πεδίο examinations
                                     referenceDB.child("examinations")
                                             .child(newExamination.getId())
                                             .setValue(newExamination);

                                     referenceDB.child("examinations")
                                             .child(newExamination.getId())
                                             .child("patients")
                                             .child(patient.getId())
                                             .setValue(true);

                                     referenceDB.child("examinations")
                                             .child(newExamination.getId())
                                             .child("users")
                                             .child(userID)
                                             .setValue(true);

                                     //Ενημέρωση ιστορικού
                                     //Δημιουργία id νέου ιστορικού
                                     final String finalNewStringExamId = newStringExamId;
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
                                                         newExamination.getId(),
                                                         currentUser.getName() +" " + currentUser.getSurname(),
                                                         "create",
                                                         newExamination.getName()
                                                         );


                                                 //Εισαγωγή νέου ιστορικού στην βάση
                                                 referenceDB.child("history")
                                                         .child(newHistory.getId())
                                                         .setValue(newHistory);

                                                 referenceDB.child("history")
                                                         .child(newHistory.getId())
                                                         .child("type")
                                                         .child("examinations")
                                                         .setValue(finalNewStringExamId);

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

                                 Toast.makeText(getActivity(), "Εισαγωγή εξέτασης επιτυχής!", Toast.LENGTH_LONG).show();
                                 dialog.dismiss();

                                 ((PatientsActivity)getActivity()).reloadCurrentFragment();
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


        //Προβολή αποτελεσμάτων εξέτασης αν υπάρχει
        examinationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Examination examinationSelected = (Examination) examinationsList.getItemAtPosition(position);

                //Προβολή εικόνας
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.examinations_results_image_dialog,null);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                //Κουμπί
                Button denyButton = (Button) mView.findViewById(R.id.denyButton);

                //Εικόνα αποτελέσματος
                resultsImage = (ImageView) mView.findViewById(R.id.resultsImage);



                StorageReference filePath = storage.child(patient.getId() +"/" + examinationSelected.getId() +".jpg" );


                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getContext()).load(uri.toString()).into(resultsImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        resultsImage.setImageResource(R.drawable.noresults);
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

            }
        });




        return view;
    }
}

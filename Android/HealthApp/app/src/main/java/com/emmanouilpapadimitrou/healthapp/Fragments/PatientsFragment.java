package com.emmanouilpapadimitrou.healthapp.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.emmanouilpapadimitrou.healthapp.POJOs.Patient;
import com.emmanouilpapadimitrou.healthapp.POJOs.Users;
import com.emmanouilpapadimitrou.healthapp.Activities.PatientsActivity;
import com.emmanouilpapadimitrou.healthapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class PatientsFragment extends Fragment {

    private ListView patientsList;
    private String userID;
    private ArrayList<String> patientsID;
    private ArrayList<Patient> allPatients;
    private DatabaseReference referenceDB;
    private ArrayList<String> patientNames;
    private ArrayAdapter<String> listViewAdapter;
    private FirebaseAuth firebaseAuth;
    private Users currentUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_patients,container,false);

        //Ορισμός της λίστας που περιέχει όλα τα αντικείμενα των ασθενών
        allPatients = new ArrayList<Patient>();

        //Μεταβλητή που μας δίνει τα στοιχεία του συνδεδεμένου χρήστη
        firebaseAuth = FirebaseAuth.getInstance();

        //Ανάθεση του ID σε μεταβλητή για μελλοντική χρήστη
        userID = firebaseAuth.getCurrentUser().getUid();

        //Ορισμός της βάσης στην μεταβλητή για οποιαδήποτε μελλοντική χρήστη
        referenceDB =  FirebaseDatabase.getInstance().getReference();
        patientNames = new ArrayList<>();
        listViewAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,patientNames);


        //Σύνδεση μεταβλητής με την λίστα στο layout
        patientsList = (ListView) view.findViewById(R.id.patientsList);


        final HashMap<String,String> patientInfoForList = new HashMap<>();

        //Παίρνουμε όλους τους ασθενείς από την βάση
        referenceDB.child("patients").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot patient : dataSnapshot.getChildren()){

                    Patient p = new Patient();
                    p.setId(String.valueOf(patient.getKey()));
                    p.setName(String.valueOf( patient.child("name").getValue()));
                    p.setSurname(String.valueOf( patient.child("surname").getValue()));
                    p.setFathername(String.valueOf( patient.child("fathername").getValue()));
                    p.setGender(String.valueOf( patient.child("gender").getValue()));
                    p.setBirthdate(String.valueOf( patient.child("birthdate").getValue()));
                    p.setEntrydate(String.valueOf( patient.child("entrydate").getValue()));
                    p.setEthnicity(String.valueOf( patient.child("ethnicity").getValue()));
                    p.setTown(String.valueOf( patient.child("town").getValue()));
                    p.setAddress(String.valueOf( patient.child("address").getValue()));
                    p.setTk(String.valueOf( patient.child("tk").getValue()));
                    p.setTelephone1(String.valueOf( patient.child("telephone1").getValue()));
                    p.setTelephone2(String.valueOf( patient.child("telephone2").getValue()));

                    allPatients.add(p);


                    for(DataSnapshot patientChild : patient.child("users").getChildren()){

                        if(firebaseAuth.getCurrentUser().getUid().equals(String.valueOf( patientChild.getKey()))){
                            patientInfoForList.put(p.getSurname() +" " + p.getName(),p.getId());

                        }
                    }

                }

                List<HashMap<String,String>> listItems = new ArrayList<>();
                SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),listItems,R.layout.list_item,
                        new String[]{"name","id"},
                        new int[]{R.id.text1,R.id.text2});


                Iterator it = patientInfoForList.entrySet().iterator();
                while(it.hasNext()){
                    HashMap<String,String> resultsMap = new HashMap<>();
                    Map.Entry pair = (Map.Entry) it.next();
                    resultsMap.put("name",pair.getKey().toString());
                    resultsMap.put("id",pair.getValue().toString());
                    listItems.add(resultsMap);

                }

                //Ταξινόμηση λίστας με βάση το όνομα
                Collections.sort(listItems, new Comparator<HashMap<String, String>>() {
                    @Override
                    public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                        return o1.get("name").compareTo(o2.get("name"));
                    }
                });

                patientsList.setAdapter(simpleAdapter);

                //Αναγνώριση ποιον ασθενή επέλεξε ο χρήστης
                patientsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Παίρνουμε το όνομα του ασθενή χρησιμοποιόντας την θέση στην λίστα
                        HashMap<String,String> map =(HashMap<String,String>)patientsList.getItemAtPosition(position);
                        String value = map.get("id");

                        final Intent intent = new Intent(getActivity(), PatientsActivity.class);
                        //TODO
                        // Βρες ποιος ασθενής είναι από όλους ασθενής που έχεις στην λίστα με τα αντικείμενα των ασθενών
                        for(final Patient tempPatient : allPatients){
                            if(value.equals(tempPatient.getId())){
                                //Δημιουργία object του τρέχοντος χρήστη ώστε να χρησιμοποιείται εύκολα στην υπόλοιπη εφαρμογή
                                userID = firebaseAuth.getCurrentUser().getUid();
                                referenceDB.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot doctorTemp : dataSnapshot.getChildren()){
                                            if(userID.equals(String.valueOf(doctorTemp.getKey()))){

                                                currentUser = new Users(String.valueOf(doctorTemp.child("name").getValue()),
                                                        String.valueOf(doctorTemp.child("surname").getValue()),
                                                        String.valueOf(doctorTemp.child("type").getValue()));



                                                //Στέλνουμε την κλάση του ασθενούς στο PatientsActivity για χρήση των στοιχείων του
                                                intent.putExtra("Patient", tempPatient);
                                                intent.putExtra("User", currentUser);
                                                break;
                                            }
                                        }


                                        //Ξεκινάει το activity που αφορά το κύριο μέρος της εφαρμογής για κάθε ασθενή
                                        //Στέλνοντας τα απαραίτητα στοιχεία για διαχείριση
                                        startActivity(intent);


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }


                                });

                                break;
                            }
                        }



                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Ασθενείς");
    }






}

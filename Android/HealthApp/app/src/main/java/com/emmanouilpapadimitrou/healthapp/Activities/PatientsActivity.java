package com.emmanouilpapadimitrou.healthapp.Activities;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.emmanouilpapadimitrou.healthapp.Adapters.SectionsPageAdapter;
import com.emmanouilpapadimitrou.healthapp.Fragments.*;
import com.emmanouilpapadimitrou.healthapp.POJOs.Patient;
import com.emmanouilpapadimitrou.healthapp.POJOs.Users;
import com.emmanouilpapadimitrou.healthapp.R;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

public class PatientsActivity extends AppCompatActivity {


    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private Patient patient;
    private FirebaseAuth firebaseAuth;
    private String userID;
    private Users currentUser;
    private Firebase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_tabbed);

        mSectionsPageAdapter  = new SectionsPageAdapter(getSupportFragmentManager());


        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        //Μεταβλητή που μας δίνει τα στοιχεία του συνδεδεμένου χρήστη
        firebaseAuth = FirebaseAuth.getInstance();

        database = new Firebase("https://healthapp-f2bba.firebaseio.com/");

        //TODO kanto sto PatientsFragment kai steilto me parceable



    }

    public void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProfilePatientFragment(),"Προφιλ");
        adapter.addFragment(new ΕxaminationsFragment(),"Εξετασεις");
        adapter.addFragment(new MedicinesFragment(),"Φαρμακα");
        adapter.addFragment(new NotesFragment(),"Σημειωσεις");
        adapter.addFragment(new HistoryFragment(),"Ιστορικο");
        adapter.addFragment(new ChatFragment(),"Chat");

        viewPager.setAdapter(adapter);
    }


    public Patient getCurrentPatient(){
        //Παίρνουμε την κλάση του ασθενούς που τέθηκε από το PatiensFragment
        patient = getIntent().getExtras().getParcelable("Patient");
        return patient;
    }

    public Users getCurrentUser(){
        //Παίρνουμε την κλάση του ασθενούς που τέθηκε από το PatiensFragment
        currentUser = getIntent().getExtras().getParcelable("User");
        return currentUser;
    }



    public void reloadCurrentFragment(){

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        setupViewPager(mViewPager);

    }





}

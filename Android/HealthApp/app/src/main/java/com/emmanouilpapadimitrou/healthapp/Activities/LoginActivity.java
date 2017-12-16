package com.emmanouilpapadimitrou.healthapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.emmanouilpapadimitrou.healthapp.NavDrawer;
import com.emmanouilpapadimitrou.healthapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //Αντιστοίχηση στοιχείων από το layout
    private Button buttonLogin;
    private EditText editTextEmail;
    private EditText editTextPassword;


    //Παράθυρο για progress
    private ProgressDialog progressDialog;

    //Αντικείμενο του Firebase για χρησιμοποίηση των δυνατοτήτων του
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Σύνδεση μεταβλητών με τα στοιχεία στο layout για επεξεργασία
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        //Instance της βάσης
        firebaseAuth = FirebaseAuth.getInstance();

        //Αν ο χρήστης έχει κάνει ήδη σύνδεση, να κάνει redirect στο profile του
        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(),NavDrawer.class));

        }

        //Ορισμός του progress παραθύρου
        progressDialog = new ProgressDialog(this);

        //Όταν ο χρήστης πατήσει σύνδεση
        buttonLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //Όταν πατήσει σύνδεση ο χρήστης
        //Καλούμε την παρακάτω συνάρτηση
        if(v == buttonLogin){
            userLogin();
        }


    }

    private void userLogin() {
        //Αναθέτουμε σε δύο μεταβλητές τα δοσμένα στοιχεία του χρήστη
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //Ελέγχουμε αν δεν έδωσε email
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Παρακαλώ εισάγετε το email σας!",Toast.LENGTH_SHORT).show();
            return;
        }

        //Ελέγχουμε αν δεν έδωσε κωδικό
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Παρακαλώ εισάγετε τον κωδικό σας!",Toast.LENGTH_SHORT).show();
            return;
        }

        //Παράθυρο progress αν έδωσε και email και κωδικό
        progressDialog.setMessage("Σύνδεση στο σύστημα.....");
        progressDialog.show();

        //Το firebase δίνει δυνατότητα για άμεση διαχείριση του email και του κωδικού του χρήστη στην σύνδεση στο σύστημα
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Παίρνουμε τα στοιχεία του χρήστη από την βάση για να κάνουμε ελέγχους
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //Κλείνουμε το παράθυρο για το progress
                progressDialog.dismiss();

                //Αν η σύνδεση είναι επιτυχής
                if(task.isSuccessful()){
                    //Κλείνει το τωρινό activity
                    finish();
                    //Και ανοίγει το βασικό activity της εφαρμογής
                    startActivity(new Intent(getApplicationContext(),NavDrawer.class));


                }
                //Αν η σύνδεση δεν ήταν επιτυχής τα παρακάτω catches θα μας εμφανίσουν το είδος του προβλήματος
                else{
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthWeakPasswordException e) {
                        editTextPassword.setError(getString(R.string.error_weak_password));
                        editTextPassword.requestFocus();
                    } catch(FirebaseAuthInvalidCredentialsException e) {
                        editTextEmail.setError(getString(R.string.error_data));
                        editTextEmail.requestFocus();
                    } catch(FirebaseAuthInvalidUserException e){
                        editTextEmail.setError(getString(R.string.error_data));
                        editTextEmail.requestFocus();
                    } catch(Exception e) {
                        Log.e("TAG", e.getMessage());
                    }
                }
            }
        });

    }
}

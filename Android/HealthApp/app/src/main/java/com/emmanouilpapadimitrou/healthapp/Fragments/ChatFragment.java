package com.emmanouilpapadimitrou.healthapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.emmanouilpapadimitrou.healthapp.Activities.PatientsActivity;
import com.emmanouilpapadimitrou.healthapp.POJOs.Message;
import com.emmanouilpapadimitrou.healthapp.POJOs.Patient;
import com.emmanouilpapadimitrou.healthapp.POJOs.Users;
import com.emmanouilpapadimitrou.healthapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class ChatFragment extends Fragment implements View.OnClickListener {

    private FirebaseUser fUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference referenceDB;
    private DatabaseReference usersDB;
    private Patient patient;
    private EditText editMessage;
    private FloatingActionButton sendMessageBtn;
    private RecyclerView messageList;
    private Timer timer;
    private String nameToWrite;
    private String getType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_chat, container, false);


        //Μεταβλητή που μας δίνει τα στοιχεία του συνδεδεμένου χρήστη
        firebaseAuth = FirebaseAuth.getInstance();


        //Παίρνουμε τον επιλεγμένο χρήστη με όλα του τα στοιχεία
        patient = ((PatientsActivity)getActivity()).getCurrentPatient();

        //Ορισμός της βάσης στην μεταβλητή για οποιαδήποτε μελλοντική χρήστη
        database = FirebaseDatabase.getInstance();
        referenceDB =  FirebaseDatabase.getInstance().getReference().child("messages").child(patient.getId());



        editMessage = (EditText) view.findViewById(R.id.editMessage);


        sendMessageBtn = (FloatingActionButton) view.findViewById(R.id.sendMessageBtn);
        sendMessageBtn.setOnClickListener(this);


        final FirebaseRecyclerAdapter <Message,MessageViewHolder> FBRA = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.single_message,
                MessageViewHolder.class,
                referenceDB
        ) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
                viewHolder.setContent(model.getContent());
                viewHolder.setName(model.getName());
                viewHolder.setTime(model.getTime());

            }
        };


        messageList = (RecyclerView) view.findViewById(R.id.messageRec);
        messageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        messageList.setLayoutManager(linearLayoutManager);
        messageList.setAdapter(FBRA);


        messageList.scrollToPosition(messageList.getAdapter().getItemCount() - 1);

        return view;
    }



    @Override
    public void onClick(View v) {
        if(v == sendMessageBtn){
            fUser = firebaseAuth.getCurrentUser();
            usersDB = FirebaseDatabase.getInstance().getReference().child("users").child(fUser.getUid());
            final String messageValue = editMessage.getText().toString().trim();
            if(!TextUtils.isEmpty(messageValue)){
                final DatabaseReference newPost = referenceDB.push();
                usersDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        newPost.child("content").setValue(messageValue);

                        getType = dataSnapshot.child("type").getValue().toString();
                        if(getType.equals("doctor")){
                            nameToWrite = "Δρ. " + dataSnapshot.child("name").getValue() + " " + dataSnapshot.child("surname").getValue();
                        }
                        else{
                            nameToWrite = dataSnapshot.child("name").getValue() + " " + dataSnapshot.child("surname").getValue();
                        }

                        newPost.child("name").setValue(nameToWrite).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        String time = simpleDateFormat.format(calendar.getTime());

                        newPost.child("time").setValue(time).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                        editMessage.setText("");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public MessageViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setContent(String content){
            TextView messageText = (TextView) mView.findViewById(R.id.messageText);
            messageText.setText(content);
        }

        public void setName(String name){
            TextView usernameText = (TextView) mView.findViewById(R.id.usernameText);
            usernameText.setText(name);
        }

        public void setTime(String time){
            TextView messageTime = (TextView) mView.findViewById(R.id.messageTime);
            messageTime.setText(time);
        }
    }
}

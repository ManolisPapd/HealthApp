package com.emmanouilpapadimitrou.healthapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.emmanouilpapadimitrou.healthapp.POJOs.History;
import com.emmanouilpapadimitrou.healthapp.R;

import java.util.ArrayList;


public class HistoryAdapter extends ArrayAdapter<History> {

    public HistoryAdapter(Context context, ArrayList<History> history) {
        super(context, 0, history);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        History history = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_history, parent, false);
        }

        TextView dateHistoryTextView = (TextView) convertView.findViewById(R.id.dateHistoryTextView);
        TextView userNameTextView = (TextView) convertView.findViewById(R.id.userNameTextView);
        TextView typeNameTextView = (TextView) convertView.findViewById(R.id.typeNameTextView);
        TextView conditionNameTextView = (TextView) convertView.findViewById(R.id.conditionNameTextView);

        dateHistoryTextView.setText(history.getDate());
        userNameTextView.setText(history.getDoctor());

        String type = "N/A";
        switch(history.getType()){
            case "medicines" :
                type = "Φάρμακα";
                break;
            case "notes":
                type = "Σημειώσεις";
                break;
            case "examinations":
                type = "Εξετάσεις";
                break;
        }

        typeNameTextView.setText(type);


        String condition = "N/A";
        switch(history.getCondition()){
            case "create" :
                condition = "Προσθήκη στο πεδίο";
                break;
            case "delete":
                condition = "Αφαίρεση από το πεδίο";
                break;

        }
        conditionNameTextView.setText(condition);




        return convertView;
    }
}

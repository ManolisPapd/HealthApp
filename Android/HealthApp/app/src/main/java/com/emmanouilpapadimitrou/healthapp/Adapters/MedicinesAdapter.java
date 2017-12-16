package com.emmanouilpapadimitrou.healthapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.emmanouilpapadimitrou.healthapp.POJOs.Medicine;
import com.emmanouilpapadimitrou.healthapp.R;

import java.util.ArrayList;

public class MedicinesAdapter extends ArrayAdapter<Medicine> {
    public MedicinesAdapter(Context context, ArrayList<Medicine> medicines) {
        super(context, 0, medicines);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Medicine medicine = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_medicine, parent, false);
        }
        TextView medicineTextView = (TextView) convertView.findViewById(R.id.medicineTextView);
        TextView doseTextView = (TextView) convertView.findViewById(R.id.doseTextView);
        TextView doctorTextView = (TextView) convertView.findViewById(R.id.doctorTextView);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
        TextView frequencyTextView = (TextView) convertView.findViewById(R.id.frequencyTextView);


        medicineTextView.setText(medicine.getName());
        doseTextView.setText(medicine.getDose());
        doctorTextView.setText(medicine.getDoctor());
        dateTextView.setText(medicine.getDate());
        frequencyTextView.setText(medicine.getFrequency());


        return convertView;
    }
}

package com.emmanouilpapadimitrou.healthapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.emmanouilpapadimitrou.healthapp.POJOs.Examination;
import com.emmanouilpapadimitrou.healthapp.R;

import java.util.ArrayList;


public class ExaminationsAdapter extends ArrayAdapter<Examination> {

    public ExaminationsAdapter(Context context, ArrayList<Examination> examinations) {
        super(context, 0, examinations);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Examination examination = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_examinations, parent, false);
        }

        TextView examinationTextView = (TextView) convertView.findViewById(R.id.examinationTextView);
        TextView dateExaminationTextView = (TextView) convertView.findViewById(R.id.dateExaminationTextView);

        examinationTextView.setText(examination.getName());
        dateExaminationTextView.setText(examination.getDate());


        return convertView;
    }
}

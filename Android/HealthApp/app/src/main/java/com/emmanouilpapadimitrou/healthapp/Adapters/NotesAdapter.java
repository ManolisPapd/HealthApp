package com.emmanouilpapadimitrou.healthapp.Adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.emmanouilpapadimitrou.healthapp.POJOs.Note;
import com.emmanouilpapadimitrou.healthapp.R;

import java.util.ArrayList;

public class NotesAdapter extends ArrayAdapter<Note> {
    public NotesAdapter(Context context, ArrayList<Note> notes) {
        super(context, 0, notes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Note note = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_notes, parent, false);
        }

        TextView dateNoteTextView = (TextView) convertView.findViewById(R.id.dateNoteTextView);
        TextView infoTextView = (TextView) convertView.findViewById(R.id.infoTextView);
        TextView userTextView = (TextView) convertView.findViewById(R.id.userTextView);

        dateNoteTextView.setText(note.getDate());
        infoTextView.setText(note.getInfo());
        userTextView.setText(note.getUser());


        return convertView;
    }
}

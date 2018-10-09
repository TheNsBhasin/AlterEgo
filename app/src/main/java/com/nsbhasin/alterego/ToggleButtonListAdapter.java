package com.nsbhasin.alterego;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ToggleButton;

public class ToggleButtonListAdapter extends ArrayAdapter<String> {
    private final String[] mValues;

    public ToggleButtonListAdapter(@NonNull Context context, int resource, String[] values) {
        super(context, resource, values);
        this.mValues = values;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interest, parent, false);
        ToggleButton toggleButton = view.findViewById(R.id.toggle_button);
        toggleButton.setTextOff(mValues[position]);
        toggleButton.setTextOn(mValues[position]);
        return view;
    }
}

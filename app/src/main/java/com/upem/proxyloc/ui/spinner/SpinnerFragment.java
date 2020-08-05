package com.upem.proxyloc.ui.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.upem.proxyloc.R;
import com.upem.proxyloc.ui.tools.TimePickerFragment;

public class SpinnerFragment extends Fragment {

    View roott;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
            roott=root;
           Spinner spinner = (Spinner) root.findViewById(R.id.planets_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(),
                R.array.planets_array, R.layout.spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Button but =  root.findViewById(R.id.but);

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });



        return root;
    }



        public void showTimePickerDialog() {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(this.getFragmentManager(), "timePicker");
    }
}
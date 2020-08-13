package com.upem.proxyloc.ui.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.upem.proxyloc.R;
import com.upem.proxyloc.services.DBHelper;
import com.upem.proxyloc.ui.tools.TimePickerFragment;

public class SpinnerFragment extends Fragment {

    View roott;
    public static String date;
    private DBHelper dbHelper;
    private String result;
    private ChipGroup chipGroup ;
    private  Spinner spinner;
    private int count =0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
            roott=root;
             spinner = root.findViewById(R.id.planets_spinner);

        Bundle bundle = this.getArguments();
        result = bundle.getString("bundleKey");
         // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(),
                R.array.planets_array, R.layout.spinner_item);
         // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Button but =  root.findViewById(R.id.but);
        dbHelper =  new DBHelper(getContext());

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });


        Button apply =  root.findViewById(R.id.button);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
              // dbHelper.insertactivity(result,spinner.getSelectedItem().toString(),date);
                    addchip();
                //dbHelper.deleteallactivities();

            }
        });


        Log.e("Spin", "Spinner: " + dbHelper.getActivities()  );

         chipGroup = root.findViewById(R.id.chipGroup);


        return root;
    }



        public void showTimePickerDialog() {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(this.getFragmentManager(), "timePicker");

    }

    public void addchip(){
        if(count<12){

        Chip chip = new Chip(getContext());

        chip.setText(spinner.getSelectedItem().toString());

        chipGroup.addView(chip);count++; }

    }
}
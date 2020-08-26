package com.upem.proxyloc.ui.spinner;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.upem.proxyloc.R;
import com.upem.proxyloc.services.DBHelper;
import com.upem.proxyloc.ui.timePicker.TimePickerFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class SpinnerFragment extends Fragment {

    View roott;
    public static String date;
    private DBHelper dbHelper;
    private String categorie;
    private ChipGroup chipGroup ;
    private  Spinner spinner;
    private int count =0;
    private int id=0;
    private Vector<Spinnerdata>  Vecdata ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
            roott=root;
             spinner = root.findViewById(R.id.planets_spinner);

        Bundle bundle = this.getArguments();
        categorie = bundle.getString("bundleKey");
         // Create an ArrayAdapter using the string array and a default spinner layout
        if(categorie.equals("0")){
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(), R.array.planets_array, R.layout.spinner_item);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown);
            spinner.setAdapter(adapter);
        }
        if(categorie.equals("1")){
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(), R.array.array2, R.layout.spinner_item);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown);
            spinner.setAdapter(adapter);
        }
        if(categorie.equals("2")){
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(), R.array.array3, R.layout.spinner_item);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown);
            spinner.setAdapter(adapter);
        }
        if(categorie.equals("3")){
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(), R.array.array4, R.layout.spinner_item);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown);
            spinner.setAdapter(adapter);
        }
        if(categorie.equals("4")){
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(), R.array.array5, R.layout.spinner_item);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown);
            spinner.setAdapter(adapter);
        }


        Button but =  root.findViewById(R.id.but);
        Button but2 =  root.findViewById(R.id.button2);
        dbHelper =  new DBHelper(getContext());
        Vecdata = new Vector<>();

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Spinnerdata data:Vecdata){
                    dbHelper.insertactivity(data.categorie,data.selected,data.date);
                    chipGroup.removeAllViews();
                }
            }
        });


        Button apply =  root.findViewById(R.id.button);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              // dbHelper.insertactivity(categorie,spinner.getSelectedItem().toString(),date);
                    addchip();
                //dbHelper.deleteallactivities();

            }
        });


        Log.e("Spin", "Spinner: " + dbHelper.getActivities()  );

         chipGroup = root.findViewById(R.id.chipGroup);
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm");
        date =sdf.format(new Date());
        Log.e("f", date);
        return root;
    }



        public void showTimePickerDialog() {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(this.getFragmentManager(), "timePicker");

    }

    public void addchip(){

        if(count<12){

        Spinnerdata obj = new Spinnerdata(categorie,date,spinner.getSelectedItem().toString(), id);

        final Chip chip = new Chip(getContext());

        chip.setText(spinner.getSelectedItem().toString());
            chip.setCloseIconResource(R.drawable.ic_close_black_24dp);
            chip.setCloseIconVisible(true);
            chip.setId(count);

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   count--;
                    chipGroup.removeView(chip);
                    for (Spinnerdata data:Vecdata) {
                        if(chip.getId() == data.getId()){

                            Vecdata.remove(data);

                            break;
                        }
                    }

                }
            });
        chipGroup.addView(chip);
        Vecdata.add(obj);

        count++;
            id++;}

    }


     class Spinnerdata{

        private String categorie;
        private String date ;
        private String selected;
        private int id ;


         public Spinnerdata(String categorie, String date, String selected, int id) {
             this.categorie = categorie;
             this.date = date;
             this.id = id;
             this.selected = selected;
         }


         public String getCategorie() {
             return categorie;
         }

         public void setCategorie(String categorie) {
             this.categorie = categorie;
         }

         public String getDate() {
             return date;
         }

         public void setDate(String date) {
             this.date = date;
         }

         public String getSelected() {
             return selected;
         }

         public void setSelected(String selected) {
             this.selected = selected;
         }


         public int getId() {
             return id;
         }

         public void setId(int id) {
             this.id = id;
         }

         @Override
         public String toString() {
             return "Spinnerdata{" +
                     "categorie=" + categorie +
                     ", date='" + date + '\'' +
                     ", selected='" + selected + '\'' +
                     '}';
         }
     }
}
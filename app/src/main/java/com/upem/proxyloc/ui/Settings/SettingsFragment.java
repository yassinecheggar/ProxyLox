package com.upem.proxyloc.ui.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.upem.proxyloc.R;
import com.upem.proxyloc.services.Global;

public class SettingsFragment extends Fragment {

    private SettingsViewModel galleryViewModel;
    private Switch MSwitch;
    private Switch SSwitch;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final SharedPreferences prefs = root.getContext().getSharedPreferences("ProxyLoxStatus", root.getContext().MODE_PRIVATE);

        MSwitch = root.findViewById(R.id.switchmal);
        SSwitch = root.findViewById(R.id.switchsym);

        String restoredText = prefs.getString("status", null);
        Log.e("gallery", "onCreateView: " + restoredText);
        if (restoredText != null) {

            if(restoredText.equals("1")){
                MSwitch.setChecked(true);
            }

            if(restoredText.equals("2")) {
                SSwitch.setChecked(true);
            }

            SSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked== true){
                        prefs.edit().putString("status","2").commit();
                        Global.Userstauts = "2";
                    }else{
                        prefs.edit().putString("status","0").commit();
                        Global.Userstauts = "0";
                    }
                }
            });



        }

        MSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("gal", "onCheckedChanged: " );
                if(isChecked== true){

                    prefs.edit().putString("status","1").commit();
                    Global.Userstauts = "1";
                }else{
                    prefs.edit().putString("status","2").commit();
                    Global.Userstauts = "0";
                }
            }
        });

        return root;
    }
}
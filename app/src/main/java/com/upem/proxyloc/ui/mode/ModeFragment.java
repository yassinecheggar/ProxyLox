package com.upem.proxyloc.ui.mode;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.upem.proxyloc.R;
import com.upem.proxyloc.services.BLE;
import com.upem.proxyloc.services.TopicSubscriber;

public class ModeFragment extends Fragment {


private Switch aSwitch;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_send, container, false);

         aSwitch = root.findViewById(R.id.switchmode);
         if(isMyServiceRunning(BLE.class)==true){aSwitch.setChecked(true);}

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if((isMyServiceRunning(BLE.class)==false)&& aSwitch.isChecked()==true){
                getActivity().startService(new Intent(getActivity(),BLE.class));
                    Log.e("start ble", "onCheckedChanged: ");
                }else if(((isMyServiceRunning(BLE.class)==true)&& aSwitch.isChecked()==false)){
                    getActivity().stopService(new Intent(getActivity(), BLE.class));
                    Log.e("Stop ble", "onCheckedChanged: ");
                }

            }
        })

        ;

        return root;
    }



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
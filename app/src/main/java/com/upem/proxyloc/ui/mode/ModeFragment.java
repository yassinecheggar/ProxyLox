package com.upem.proxyloc.ui.mode;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.upem.proxyloc.Home;
import com.upem.proxyloc.R;
import com.upem.proxyloc.services.BLE;
import com.upem.proxyloc.services.TopicPublisher;
import com.upem.proxyloc.services.TopicSubscriber;
import com.upem.proxyloc.services.Wifi;

import java.util.Timer;
import java.util.TimerTask;

public class ModeFragment extends Fragment {


private Switch aSwitch;
private Button button;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_send, container, false);

         aSwitch = root.findViewById(R.id.switchmode);
         button = root.findViewById(R.id.buttonWifi);
        final Intent ble  = new Intent(getActivity(), BLE.class);

         if(isMyServiceRunning(BLE.class)==true){aSwitch.setChecked(true);}

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if((isMyServiceRunning(BLE.class)==false)&& aSwitch.isChecked()==true){

                    startBleu();

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            startDetection();
                        }
                    }, 2000);

                    Log.e("start ble", "onCheckedChanged: ");
                }else if(((isMyServiceRunning(BLE.class)==true)&& aSwitch.isChecked()==false)){

                    getActivity().stopService(new Intent(getActivity(), BLE.class));
                    Log.e("Stop ble", "onCheckedChanged: ");
                    stopBleu();
                }

            }
        })

        ;

         button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startWifiDetection();
             }
         });

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


    public void startDetection(){

        Intent serviceIntent = new Intent(getContext(),BLE.class);

        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.O) {
            getActivity().startForegroundService(serviceIntent);

        } else {
           getActivity().startService(serviceIntent);

        }

    }


    public void startWifiDetection(){

        Intent serviceIntent = new Intent(getContext(), Wifi.class);

        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.O) {
            getActivity().startForegroundService(serviceIntent);

        } else {
            getActivity().startService(serviceIntent);

        }

    }

    public void startBleu(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }
    public void stopBleu(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }


}
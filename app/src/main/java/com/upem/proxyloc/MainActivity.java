package com.upem.proxyloc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.AdvertiseSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.upem.proxyloc.adaptor.DeviceListAdapter;
import com.upem.proxyloc.models.DeviceItem;
import com.upem.proxyloc.services.TopicSubscriber;
import com.uriio.beacons.Beacons;
import com.uriio.beacons.model.EddystoneTLM;
import com.uriio.beacons.model.EddystoneUID;
import com.uriio.beacons.model.EddystoneURL;
import com.uriio.beacons.model.iBeacon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.security.auth.login.LoginException;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter BTAdapter;
    public static int REQUEST_BLUETOOTH = 1;
    private static final int REQUEST_ENABLE_BT = 456;
    private static final int REQUEST_ENABLE_LOCATION = 457;
    private Vector<DeviceItem> Vec ;
    private DeviceListAdapter adapter;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BTAdapter = BluetoothAdapter.getDefaultAdapter();


        // Phone does not support Bluetooth so let the user know and exit.
        if (BTAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        if ( checkSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {
            requestPermissions(
                    new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    REQUEST_ENABLE_LOCATION );
        }


        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        listView = findViewById(R.id.list);
        Vec = new Vector<>();
        adapter = new DeviceListAdapter(Vec,this);
        listView.setAdapter(adapter);


        start();




      //  Toast.makeText(this, "" + isMyServiceRunning(TopicSubscriber.class), Toast.LENGTH_SHORT).show();

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void start(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        scann();
    }

    public void check(View view){
        List<DeviceItem> deviceItemList =new ArrayList<>();//---------------------------------------
        Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                DeviceItem newDevice= new DeviceItem(device.getName(),device.getAddress(),"false");

                deviceItemList.add(newDevice);
            }
        }

        Toast.makeText(this, "connected "+ deviceItemList.size(), Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Toast.makeText(context, "neeeeww", Toast.LENGTH_SHORT).show();
                // Un récupère le périphérique bluetooth détecté durant le scan
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // Et on affiche ses informations dans un toast et dans la fenêtre LogCat
                //  String message = device.getName() + "-" + device.getAddress();
                DeviceItem d= new DeviceItem(device.getName(),device.getAddress(),"true");
                boolean a = true;
                for (DeviceItem dev:Vec) {
                    if(dev.getAddress().contains(d.getAddress())){a =  false;}

                }
                if(a==true){
                    Vec.add(d);
                }

                adapter.update(Vec);
                // Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                //Log.e("DebugBluetooth", message);

            }
        }
    };

        public void scann() {
            /*
                new Thread(new Runnable() {
                    public void run() {
                        while (true){
                        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                        registerReceiver(receiver, filter);

                        if (BTAdapter.isDiscovering()== false) {
                            BTAdapter.startDiscovery();
                        }

                        // On lance un nouveau scan bluetooth

                    }
                    }
                }).start();


           /* IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, filter);

            if (BTAdapter.isDiscovering()) {
                BTAdapter.cancelDiscovery();
            }

            // On lance un nouveau scan bluetooth
            BTAdapter.startDiscovery();*/


        }


        @Override
        protected void onDestroy() {
            super.onDestroy();

            // Don't forget to unregister the ACTION_FOUND receiver.
            unregisterReceiver(receiver);
        }
    }



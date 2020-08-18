package com.upem.proxyloc.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Wifi extends Service {
    private  NotificationHelper notificationHelper ;
    private final IntentFilter intentFilter = new IntentFilter();
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private ListView listView;
    String[] deviceNameArray;
    WifiP2pDevice[] devicesArray;
    String TAG = "mainn";
    WifiP2pManager.Channel channel;
    WifiP2pManager manager;
    BroadcastReceiver receiver;



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {


        return START_NOT_STICKY;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        notificationHelper = new NotificationHelper(getBaseContext());
        startForeground(1, notificationHelper.cretNotification());


        manager = (WifiP2pManager) getSystemService(getBaseContext().WIFI_P2P_SERVICE);
        channel = manager.initialize(getBaseContext(), getMainLooper(), null);


        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        registerReceiver(receiver, intentFilter);
       // listView = findViewById(R.id.list);

        try {
            discover();
        } catch (Exception e) {
            Log.e(TAG, "onCreate: "+ e.getMessage() );
        }

    }


    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            Toast.makeText(getBaseContext(), "Found", Toast.LENGTH_SHORT).show();
            if (!peerList.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                deviceNameArray = new String[peerList.getDeviceList().size()];
                devicesArray = new WifiP2pDevice[peerList.getDeviceList().size()];

                int index = 0;
                for (WifiP2pDevice device : peerList.getDeviceList()) {
                    deviceNameArray[index] = device.deviceName;
                    devicesArray[index] = device;
                    index++;
                }

                Log.d(TAG, "onPeersAvailable() returned: " + deviceNameArray);
            }

            if (peers.size() == 0) {
                Toast.makeText(getBaseContext(), "no  device found ", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    public void discover() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method m = manager.getClass().getMethod("setDeviceName", new Class[]     {channel.getClass(), String.class,
                WifiP2pManager.ActionListener.class});
        m.invoke(manager, channel, Global.mac, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

            }
            @Override
            public void onFailure(int reason) {

            }
        });
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(getBaseContext(), "Discoverry started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(getBaseContext(), "Discoverry startin fail", Toast.LENGTH_SHORT).show();
            }
        });

    }




    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}

package com.upem.proxyloc.services;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.upem.proxyloc.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONArray;

import java.util.Arrays;
import java.util.Collection;

public class BLE extends Service implements BeaconConsumer {
    private AppBarConfiguration mAppBarConfiguration;
    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;
    private int Ntfcount = 0;
    private static final int REQUEST_ENABLE_LOCATION = 457;
    private Region region;
    private BeaconTransmitter beaconTransmitter;
    private JSONArray  jsonBeacons;
    private  DBHelper dbHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dostuf();
        dostuf2();
        jsonBeacons =  new JSONArray();
        dbHelper  =  new DBHelper(this);
        //dbHelper.deleteallexpose();
        return START_NOT_STICKY;
    }

    public void dostuf() {

        Log.e(TAG, "dostuf: " +myUUid(Global.mac) );
        final String TAG = "BLE";
        Beacon beacon = new Beacon.Builder()
                .setId1(myUUid(Global.mac))
                .setId2("1")
                .setId3("2")
                .setManufacturer(0x0118) // Radius Networks.  Change this for other beacon layouts
                .setTxPower(-59)
                .setDataFields(Arrays.asList(new Long[]{3l}))
                // Remove this for beacon layouts without d: fields
                .build();



        BeaconParser beaconParser = new BeaconParser()
                .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
         beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);

        beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {

            @Override
            public void onStartFailure(int errorCode) {
                Log.e(TAG, "Advertisement start failed with code: " + errorCode);
            }

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.i(TAG, "Advertisement start succeeded.");
            }
        });

    }

    public void dostuf2() {

            beaconManager = BeaconManager.getInstanceForApplication(this);
            // To detect proprietary beacons, you must add a line like below corresponding to your beacon
            // type.  Do a web search for "setBeaconLayout" to get the proper expression.
            // beaconManager.getBeaconParsers().add(new BeaconParser().
            // setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
            beaconManager.bind(this);


    }

     @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllMonitorNotifiers();

        beaconManager.addRangeNotifier(new RangeNotifier() {
           @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

               Log.e(TAG, "didRangeBeaconsInRegion: " + dbHelper.getAllexpo().length() );

                if (beacons.size() > 0) {
                    //Log.e(TAG, "The first beacon " + beacons.iterator().next().getDistance() + " meters away : beacon  size" + beacons.size());
                    for (Beacon b : beacons)  {
                        Cursor cur = dbHelper.getexpo(b.getId1().toString());
                        if(cur.getCount()==0){
                            dbHelper.insertExpose(b.getId1().toString(),1);
                        }else{

                            cur.moveToFirst();
                            //  dbHelper.updateExpose(cur.getString(cur.getColumnIndex("mac")),cur.getInt(cur.getColumnIndex("sec"))+1);
                            Log.e("lola", "  count  " +cur.getString(0) + "  " +  cur.getString(1)+ " "+ cur.getString(2));
                        }
                    }
                    // notificationId is a unique int for each notification that you must define
                    if (Ntfcount < beacons.size()) {

                        final NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
                        notificationHelper.notify(1, false, "My title", "My content" );
                        Ntfcount++;
                    }
                }
            }

        });

        try {

            beaconManager.startRangingBeaconsInRegion(new Region("all", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

/*
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onBeaconServiceConnect() {
        final NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
                notificationHelper.notify((int) ((Math.random() * ((100 - 1) + 1)) + 1), false, "My title", "Enter" + region.getId1() );
                Toast.makeText(getApplicationContext(), "I just saw an beacon for the first time", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
                Toast.makeText(getApplicationContext(), "I no longer see an beacon", Toast.LENGTH_SHORT).show();
                notificationHelper.notify((int) ((Math.random() * ((100 - 1) + 1)) + 1), false, "My title", "Exit"+ region.getId1() );
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state);
            }
        });

        try {
            region  = new Region("ProxyLox", null, null, null);
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {    }
    }
*/

    public String myUUid(String code) {
        char a[] = code.toCharArray();
        String UUid="";
        System.out.println(a.length);
        for (int i = 0; i < 32; i++) {
            if((i==8)|| (i==12)||(i==16)||(i==20)){ UUid = UUid.concat("-");}
            if(i<a.length ) {UUid = UUid.concat(String.valueOf(a[i]));}
            else {UUid = UUid.concat("a");}
        }
        System.out.println(UUid);
        return UUid;

    }

    public void stop () throws RemoteException {


    }

    @Override
    public void onDestroy() {

        beaconTransmitter.stopAdvertising();
        beaconManager.removeAllMonitorNotifiers();
        this.stopSelf();

    }

    public void ChecknSend(){


    }
}

package com.upem.proxyloc.services;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;
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
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Arrays;
import java.util.Collection;

public class BLE extends Service implements BeaconConsumer {
    private AppBarConfiguration mAppBarConfiguration;
    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;
    private int Ntfcount = 0;
    private static final int REQUEST_ENABLE_LOCATION = 457;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dostuf();
        dostuf2();
        return START_STICKY;
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
        BeaconTransmitter beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);

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
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.e(TAG, "The first beacon I see is about " + beacons.iterator().next().getDistance() + " meters away." + beacons.size());

                    // notificationId is a unique int for each notification that you must define
                    if (Ntfcount < beacons.size()) {
                        Log.e(TAG, "didRangeBeaconsInRegion:----------------------- " );
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "lemubitA")
                                .setSmallIcon(R.drawable.ic_warning_black_24dp)
                                .setContentTitle("Warning")
                                .setContentText(" Device found near you  "+beacons.iterator().next().getBluetoothAddress())
                                .setPriority(NotificationCompat.PRIORITY_MAX);


                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        notificationManager.notify(Ntfcount, builder.build());
                        Ntfcount++;
                    }
                }
            }
        });

        try {
            Identifier id1 = Identifier.parse("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6");
            beaconManager.startRangingBeaconsInRegion(new Region("all", id1, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


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

}

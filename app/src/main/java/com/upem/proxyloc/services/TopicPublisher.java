package com.upem.proxyloc.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;

public class TopicPublisher extends Service {

    private Location currentBestLocation = null;
    private LocationManager mlocationManager = null;
    private FusedLocationProviderClient fusedLocationClient;

    public TopicPublisher() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                JSONObject obj = new JSONObject();
                try {
                    obj.put("mac","cv:5d:f4");
                    obj.put("altitude","2.65658");
                    obj.put("longitude","44.13545");
                    obj.put("status","1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                while (true) {
                    publish("tcp://mr2aqty0xnech1.messaging.solace.cloud:20966", "solace-cloud-client", "usa9boldpiapdjqr9b7gii14h",obj.toString() );

                }

               // Log.e("loal", "doInBackground: " );
               // getLastBestLocation();


            }
        }.execute();

        return START_STICKY;
    }

    public void publish(String... args) {
        System.out.println("TopicPublisher initializing...");

        String host = args[0];
        String username = args[1];
        String password = args[2];
        String msg = args[3];


        try {
            MemoryPersistence persistence = new MemoryPersistence();
            // Create an Mqtt client
            MqttClient mqttClient = new MqttClient(host, "HelloWorldPub", persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());

            // Connect the client
            System.out.println("Connecting to Solace messaging at " + host);
            mqttClient.connect(connOpts);
            System.out.println("Connected");

            // Create a Mqtt message
            String content = msg;
            MqttMessage message = new MqttMessage(content.getBytes());
            // Set the QoS on the Messages - 
            // Here we are using QoS of 0 (equivalent to Direct Messaging in Solace)
            message.setQos(0);

            System.out.println("Publishing message: " + content);

            // Publish the message
            mqttClient.publish("test", message);

            // Disconnect the client
            mqttClient.disconnect();

            System.out.println("Message published. Exiting");


        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }


    }

    private void getLastBestLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Log.e("", "getLastBestLocation: " +fusedLocationClient.getLastLocation().getResult().toString());
      //  fetchLastLocation();

    }

    private void fetchLastLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
//                    Toast.makeText(MainActivity.this, "Permission not granted, Kindly allow permission", Toast.LENGTH_LONG).show();

                return;
            }
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener((Executor) this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Log.e("LAST LOCATION: ", location.toString());
                        }
                    }
                });

    }




}
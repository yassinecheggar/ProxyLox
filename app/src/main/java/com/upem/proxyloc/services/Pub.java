package com.upem.proxyloc.services;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.upem.proxyloc.R;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.List;


import static android.content.Context.LOCATION_SERVICE;

public class Pub {
    private LocationManager mLocationManager;
    private Context context;

    public Pub(Context context) {
        this.context = context;
    }

    public void publish(String... args) {
        System.out.println("TopicPublisher initializing...");

        String host = context.getResources().getString(R.string.mqtt_host);

        String msg = args[0];


        try {
            MemoryPersistence persistence = new MemoryPersistence();
            // Create an Mqtt client
            MqttClient mqttClient = new MqttClient(host, MqttClient.generateClientId(), persistence);
            //MqttConnectOptions connOpts = new MqttConnectOptions();
           // connOpts.setCleanSession(true);
           // connOpts.setUserName(username);
           // connOpts.setPassword(password.toCharArray());

            // Connect the client
            System.out.println("Connecting to Solace messaging at " + host);
            mqttClient.connect();
            System.out.println("Connected");

            // Create a Mqtt message
            String content = msg;
            MqttMessage message = new MqttMessage(content.getBytes());
            // Set the QoS on the Messages -
            // Here we are using QoS of 0 (equivalent to Direct Messaging in Solace)
            message.setQos(0);

            System.out.println("Publishing message: " + content);

            // Publish the message
            mqttClient.publish(context.getString(R.string.mqttPubTopic), message);

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

    public Location getLastKnownLocation() {
        mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        Log.e("loc", "getLastKnownLocation: " + bestLocation.getLongitude() + " latitude" + bestLocation.getLatitude());
        return bestLocation;
    }




}

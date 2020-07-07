package com.upem.proxyloc.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TopicPublisher extends Service {

    private Location currentBestLocation = null;
    private  DBHelper dbHelper;
    private SQLiteDatabase database;

    LocationManager mLocationManager ;

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

         dbHelper  =  new DBHelper(this);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        checkifgpsEnable();
                        Log.e("is connected", " internet " +isInternetAvailable() );
                        try {
                          Log.e("db size", "run: " +dbHelper.getAll().size() );
                          // no internet
                          if(isInternetAvailable()==false){

                              dbHelper.insertLocation("ec:ef:sf:er","2.65","2.45","20-20-15");

                          }else { //with internet
                              JSONObject obj = new JSONObject();
                              try {
                                  obj.put("mac","cv:5d:f4");
                                  obj.put("altitude",currentBestLocation.getAltitude());
                                  obj.put("longitude",currentBestLocation.getLongitude());
                                  obj.put("date", Calendar.getInstance().getTime());
                              } catch (JSONException e) {
                                  e.printStackTrace();
                              }

                              publish("tcp://mr2aqty0xnech1.messaging.solace.cloud:20966", "solace-cloud-client", "usa9boldpiapdjqr9b7gii14h",obj.toString() );

                              Vector<JSONObject> vec = dbHelper.getAll();
                              if(vec.size()>0){

                                  for (JSONObject ob:vec) {
                                      publish("tcp://mr2aqty0xnech1.messaging.solace.cloud:20966", "solace-cloud-client", "usa9boldpiapdjqr9b7gii14h",ob.toString() );
                                      Log.e("sent from  db", "sent " );
                                  }
                                  dbHelper.deleteall();
                              }


                          }

                        }catch (Exception e){
                            Log.e("error", "run: " + e.getMessage());
                        }



                    }
                }, 0, 60, TimeUnit.SECONDS);




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

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
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
        return bestLocation;
    }

    private void checkifgpsEnable() {

        boolean gps_enabled = false;
        boolean network_enabled = false;
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        try {
            gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}


        if(!gps_enabled) {
            Log.e("check", "checkifgpsEnable: nooooooooon " );
        }else{
             currentBestLocation =  getLastKnownLocation();

        }
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }



}
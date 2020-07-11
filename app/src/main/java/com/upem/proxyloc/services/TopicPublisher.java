package com.upem.proxyloc.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
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

    public static final String DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";

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
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_2);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        final Pub pub = new Pub(getApplicationContext());

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        boolean Todo =  checkifgpsEnable();

                        Log.e("is connected", " internet " +isInternetAvailable() );
                        try {
                          Log.e("db size", "run: " +dbHelper.getAll().size() );
                          // no internet
                          if(isInternetAvailable()==false) {
                              if (Todo == true) {
                                  dbHelper.insertLocation("ec:ef:sf:er",String.valueOf(currentBestLocation.getLatitude()), String.valueOf(currentBestLocation.getLongitude()), ""+ dateFormat.format(Calendar.getInstance().getTime()) );
                              }


                          }else { //with internet
                              JSONObject obj = new JSONObject();
                              try {
                                  obj.put("mac","cv:5d:f4");
                                  obj.put("latitude", String.valueOf(currentBestLocation.getLatitude()));
                                  obj.put("longitude",String.valueOf(currentBestLocation.getLongitude()));
                                  obj.put("TimeColumn", dateFormat.format(Calendar.getInstance().getTime()));
                                  obj.put("UsrStatus", "0");
                              } catch (JSONException e) {
                                  e.printStackTrace();
                              }

                              pub.publish("tcp://mr2aqty0xnech1.messaging.solace.cloud:20966", "solace-cloud-client", "usa9boldpiapdjqr9b7gii14h",obj.toString() );

                              Vector<JSONObject> vec = dbHelper.getAll();
                              if(vec.size()>0){

                                  for (JSONObject ob:vec) {
                                      pub.publish("tcp://mr2aqty0xnech1.messaging.solace.cloud:20966", "solace-cloud-client", "usa9boldpiapdjqr9b7gii14h",ob.toString() );
                                      Log.e("sent from  db", "sent " );
                                  }
                                  dbHelper.deleteall();
                              }


                          }

                        }catch (Exception e){
                            Log.e("error", "run: " + e.getMessage());
                        }

                    }
                }, 0, 20, TimeUnit.SECONDS);




        return START_STICKY;
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
        Log.e("loc", "getLastKnownLocation: " +bestLocation.getLongitude() +" latitude" + bestLocation.getLatitude() );
        return bestLocation;
    }

    private boolean  checkifgpsEnable() {

        boolean gps_enabled = false;
        boolean network_enabled = false;
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        try {
            gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        } catch(Exception ex) {}


        if(!gps_enabled) {
            Log.e("check", "checkifgpsEnable: nooooooooon " );
            return false;
        }else{
             currentBestLocation =  getLastKnownLocation();
            return true;
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
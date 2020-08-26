package com.upem.proxyloc.services;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.upem.proxyloc.Home;
import com.upem.proxyloc.R;
import com.upem.proxyloc.SplashScreen;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class Sub {

    private MqttClient mqttClient;
    private MqttConnectOptions connOpts;
    private Context context;
    private Activity  activity;
    private NotificationHelper notificationHelper;


private  Intent ble ;
    public Sub(Context context) {
        this.context = context;
       ble = new Intent(context,BLE.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationHelper = new NotificationHelper(context);
        }
    }

    public void Subscrib() {
        System.out.println("TopicSubscriber initializing...");

        String host = context.getResources().getString(R.string.mqtt_host);
       // String username = context.getResources().getString(R.string.mqtt_user);
        //String password = context.getResources().getString(R.string.mqtt_pass);

        try {
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(host, MqttClient.generateClientId(), persistence);
        /*    connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());
            connOpts.setAutomaticReconnect(true);*/

            // Connect the client
            System.out.println("Connecting to Solace messaging at " + host);
            mqttClient.connect();
            System.out.println("Connected");

            // Latch used for synchronizing b/w threads


            // Topic filter the client will subscribe to
            final String subTopic = "proxylox/in/data/"+Global.mac;

            // Callback - Anonymous inner-class for receiving messages
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection to MQTT broker lost!" + cause);
                    try {
                        mqttClient.close();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String msg = new String(message.getPayload());
                    if(msg.equals("startble")){
                        start();


                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                context.startService(ble);
                            }
                        }, 2000);


                    }
                    if(msg.equals("stopble")){
                        context.stopService(ble);
                        stop();

                    }

                    if(msg.contains("setStatus")){
                        Log.e("msg", "messageArrived: " );
                        String[] parts = msg.split(":");
                        if((parts[1].equals("0"))||parts[1].equals("1")||(parts[1].equals("2"))){
                        SharedPreferences prefs = context.getSharedPreferences("ProxyLoxStatus", context.MODE_PRIVATE);

                            prefs.edit().putString("status",parts[1]).commit();

                            Global.Userstauts = parts[1] ;
                        }
                    }

                    if(msg.contains("Notify")){
                        Log.e("msg", "messageArrived: " );
                        String[] parts = msg.split(":");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            notificationHelper.notify((int) (Math.random()*10)+2, true, "Message", parts[1]);
                        }

                    }

                   // JSONObject obj = new JSONObject(msg);
                   // Global.MarkerObjects.add(obj);
                    //Global.changes =Global.changes+1 ;
                    Log.e("TAG", "messageArrived: " + msg);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

            // Subscribe client to the topic filter and a QoS level of 0
            System.out.println("Subscribing client to topic: " + subTopic);
            mqttClient.subscribe(subTopic, 1);
            System.out.println("Subscribed");

            // Wait for the message to be received

            // Disconnect the client
            //  mqttClient.disconnect();
            //System.out.println("Exiting");

            // System.exit(0);
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }
    public boolean isconnected(){

        return   this.mqttClient.isConnected();

    }

    public void reconnect() throws MqttException {

       Subscrib();

    }

    public void start(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }
    public void stop(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }

}

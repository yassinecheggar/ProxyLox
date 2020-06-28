package com.upem.proxyloc.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.sql.Timestamp;
import java.util.concurrent.CountDownLatch;

public class TopicSubscriber extends Service {



    private static String DEVICESECRET = "mr2aqty0xnech1";
    private static String DEVICENAME = "solace-cloud-client";
    private static String PRODUCTKEY = "usa9boldpiapdjqr9b7gii14h";

    private String clientId;
    private String userName="solace-cloud-client";
    private String passWord = "usa9boldpiapdjqr9b7gii14h";
    private String host=  "tcp://mr2aqty0xnech1.messaging.solace.cloud:20966";
    MqttAndroidClient mqttAndroidClient;

    final CountDownLatch latch = new CountDownLatch(1);

    public TopicSubscriber() {
        // TODO Auto-generated constructor stub
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        new AsyncTask<Void, Void, String>(){

            @Override
            protected String doInBackground(Void... voids) {
                new TopicSubscriber().run("tcp://mr2aqty0xnech1.messaging.solace.cloud:20966","solace-cloud-client","usa9boldpiapdjqr9b7gii14h");

                return null;
            }
        }.execute();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void run(String... args) {
        System.out.println("TopicSubscriber initializing...");

        String host = args[0];
        String username = args[1];
        String password = args[2];

        try {
            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient mqttClient = new MqttClient(host, "HelloWorldSub",persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());

            // Connect the client
            System.out.println("Connecting to Solace messaging at "+host);
            mqttClient.connect(connOpts);
            System.out.println("Connected");

            // Latch used for synchronizing b/w threads
            final CountDownLatch latch = new CountDownLatch(1);

            // Topic filter the client will subscribe to
            final String subTopic = "T/GettingStarted/pubsub";

            // Callback - Anonymous inner-class for receiving messages
            mqttClient.setCallback(new MqttCallback() {

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Called when a message arrives from the server that
                    // matches any subscription made by the client
                    String time = new Timestamp(System.currentTimeMillis()).toString();
                    System.out.println("\nReceived a Message!" +
                            "\n\tTime:    " + time +
                            "\n\tTopic:   " + topic +
                            "\n\tMessage: " + new String(message.getPayload()) +
                            "\n\tQoS:     " + message.getQos() + "\n");
                    latch.countDown(); // unblock main thread
                }

                public void connectionLost(Throwable cause) {
                    System.out.println("Connection to Solace messaging lost!" + cause.getMessage());
                    latch.countDown();
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                }

            });

            // Subscribe client to the topic filter and a QoS level of 0
            System.out.println("Subscribing client to topic: " + subTopic);
            mqttClient.subscribe(subTopic, 0);
            System.out.println("Subscribed");

            // Wait for the message to be received
            try {
                latch.await(); // block here until message received, and latch will flip
            } catch (InterruptedException e) {
                System.out.println("I was awoken while waiting");
            }

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





}
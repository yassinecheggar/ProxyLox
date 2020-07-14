package com.upem.proxyloc.services;

import android.content.Context;
import android.util.Log;

import com.upem.proxyloc.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

public class Sub {

    private MqttClient mqttClient;
    private MqttConnectOptions connOpts;
    private Context context;


    public Sub(Context context) {
        this.context = context;
    }

    public void Subscrib() {
        System.out.println("TopicSubscriber initializing...");

        String host = context.getResources().getString(R.string.mqtt_host);
        String username = context.getResources().getString(R.string.mqtt_user);
        String password = context.getResources().getString(R.string.mqtt_pass);

        try {
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(host, "HelloWorldSub", persistence);
            connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());
            connOpts.setAutomaticReconnect(true);

            // Connect the client
            System.out.println("Connecting to Solace messaging at " + host);
            mqttClient.connect(connOpts);
            System.out.println("Connected");

            // Latch used for synchronizing b/w threads


            // Topic filter the client will subscribe to
            final String subTopic = "test2";

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
                    JSONObject obj = new JSONObject(msg);
                    Global.MarkerObjects.add(obj);
                    Global.changes =Global.changes+1 ;
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

}

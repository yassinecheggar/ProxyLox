package com.upem.proxyloc.services;

import android.util.Log;
import android.widget.Toast;

import java.util.Vector;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;



public class SimpleMqttCallBack implements MqttCallback {

	private String msg;
	private TopicSubscriber topicSubscriber;

	public void connectionLost(Throwable throwable) {
		System.out.println("Connection to MQTT broker lost!");
	}

	public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
		msg = new String(mqttMessage.getPayload());
		Log.e("TAG", "messageArrived: "+ msg );
	}
	public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}



}
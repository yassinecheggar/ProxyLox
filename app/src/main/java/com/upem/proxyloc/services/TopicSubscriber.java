package com.upem.proxyloc.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TopicSubscriber extends Service {

    private Sub sub ;



    public TopicSubscriber() {
        // TODO Auto-generated constructor stub
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sub = new Sub(this);
        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                sub.Subscrib();

                scheduler.scheduleAtFixedRate(new Runnable() {
                    public void run() {
                        if (sub.isconnected() == false) {
                            //  Log.e("reconnect", "reconnectig ");
                            sub.Subscrib();
                        }else {// Log.e("reconnect", "connected " )
                            ;}
                    }
                }, 0, 20, TimeUnit.SECONDS);

                return null;
            }
        }.execute();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
package com.upem.proxyloc.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.upem.proxyloc.R;

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


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        sub = new Sub(this);
        final NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());

        startForeground(1,notificationHelper.getnotif(2,false,"loal","laaaal"));
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
/*
    private void createAndShowForegroundNotification(Service yourService, int notificationId) {


        final NotificationCompat.Builder builder = getNotificationBuilder(,
                "com.example.your_app.notification.CHANNEL_ID_FOREGROUND", // Channel id
                NotificationManagerCompat.IMPORTANCE_LOW); //Low importance prevent visual appearance for this notification channel on top
        builder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_warning_black_24dp)
                .setContentTitle("lola")
                .setContentText("message");

        Notification notification = builder.build();

        yourService.startForeground(notificationId, notification);

        if (notificationId != lastShownNotificationId) {
            // Cancel previous notification
            final NotificationManager nm = (NotificationManager) yourService.getSystemService(Activity.NOTIFICATION_SERVICE);
            nm.cancel(lastShownNotificationId);
        }
        lastShownNotificationId = notificationId;
    }

    public static NotificationCompat.Builder getNotificationBuilder(Context context, String channelId, int importance) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            prepareChannel(context, channelId, importance);
            builder = new NotificationCompat.Builder(context, channelId);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        return builder;
    }*/


}
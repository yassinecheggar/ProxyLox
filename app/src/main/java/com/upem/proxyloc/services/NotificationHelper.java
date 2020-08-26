package com.upem.proxyloc.services;
import android.app.PendingIntent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.content.Context;
import android.content.ContextWrapper;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.upem.proxyloc.R;

public class NotificationHelper extends ContextWrapper {
    private NotificationManager notifManager;

    private static final String CHANNEL_HIGH_ID = "com.ycheggar.Proxylox.HIGH_CHANNEL";
    private static final String CHANNEL_HIGH_NAME = "High Channel";
    public static final String CHANNELMy_ID_Name = "com.ycheggar.Proxylox.NONE_CHANNEL";
    public static final String CHANNELMy_ID = "ForegroundServiceChannel";
    private static final String CHANNEL_DEFAULT_ID = "com.ycheggar.Proxylox.DEFAULT_CHANNEL";
    private static final String CHANNEL_DEFAUL_NAME = "Default Channel";
    private static final String CHANNEL_DEFAULT_IDzz = "com.ycheggar.Proxylox2.DEFAULT_CHANNEL";



    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationHelper(Context base ) {
        super( base );

        notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        long [] swPattern = new long[] {0, 400, 200, 400,200,400};

        NotificationChannel notificationChannelHigh = new NotificationChannel(
                CHANNEL_HIGH_ID, CHANNEL_HIGH_NAME, notifManager.IMPORTANCE_HIGH );
        notificationChannelHigh.enableLights( true );
        notificationChannelHigh.setLightColor( Color.RED );
        notificationChannelHigh.setShowBadge( true );
        notificationChannelHigh.enableVibration( true );
        notificationChannelHigh.setVibrationPattern( swPattern );
        notificationChannelHigh.setLockscreenVisibility( Notification.VISIBILITY_PUBLIC );
        notifManager.createNotificationChannel( notificationChannelHigh );

        NotificationChannel notificationChannelDefault = new NotificationChannel(
                CHANNEL_DEFAULT_ID, CHANNEL_DEFAUL_NAME, notifManager.IMPORTANCE_LOW );
        notificationChannelDefault.enableLights( true );
        notificationChannelDefault.setLightColor( Color.WHITE );
        notificationChannelDefault.enableVibration( false );
        notificationChannelDefault.setShowBadge( false );
        notifManager.createNotificationChannel( notificationChannelDefault );


        NotificationChannel DefnotificationChannel = new NotificationChannel(CHANNELMy_ID, CHANNELMy_ID_Name, notifManager.IMPORTANCE_DEFAULT );
        DefnotificationChannel .enableLights( true );
        DefnotificationChannel .setLightColor( Color.WHITE );
        DefnotificationChannel .enableVibration( false );
        DefnotificationChannel .setShowBadge( false );
        notifManager.createNotificationChannel( DefnotificationChannel  );


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void notify(int id, boolean prioritary, String title, String message ) {
        String channelId = prioritary ? CHANNEL_HIGH_ID : CHANNEL_DEFAULT_ID;
        Resources res = getApplicationContext().getResources();

        Notification notification = new Notification.Builder( getApplicationContext(), channelId )
                .setContentTitle( title )
                .setContentText( message )
                .setSmallIcon( R.drawable.ic_warning_black_24dp )
                .setLargeIcon( BitmapFactory.decodeResource(res, R.drawable.antivirus) )
                .setAutoCancel( true )
                .build();

        notifManager.notify( id, notification );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification getnotif(int id, boolean prioritary, String title, String message ) {
        String channelId = prioritary ? CHANNEL_HIGH_ID : CHANNEL_DEFAULT_ID;
        Resources res = getApplicationContext().getResources();

        Notification notification = new Notification.Builder( getApplicationContext(), channelId )
                .setContentTitle( title )
                .setContentText( message )
                .setSmallIcon( R.drawable.ic_warning_black_24dp )
                .setLargeIcon( BitmapFactory.decodeResource(res, R.drawable.antivirus) )
                .setAutoCancel( true )
                .build();

       return  notification;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public  Notification cretNotification(){

        String channelId = true ? CHANNELMy_ID: CHANNEL_DEFAULT_ID;
        Resources res = getApplicationContext().getResources();

        Notification notification = new Notification.Builder( getApplicationContext(), channelId )

                .setContentText( "ProxyLox is Runnig on BackGround" )
                .setSmallIcon( R.drawable.ic_settings )

                .setAutoCancel( true )
                .build();
        return notification;
    }




    public void Notifications(int id){

        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) getBaseContext().getSystemService(getBaseContext().NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default45",
                "Channel name",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Channel description");
        notificationManager.createNotificationChannel(channel);
        Resources res = getApplicationContext().getResources();
        Notification n= new Notification.Builder(this,"default45")
                .setContentTitle("ProxyLox")
                .setContentText("warning Run .....")
                .setLargeIcon( BitmapFactory.decodeResource(res, R.drawable.antivirus) )
                .setNumber(5)
                .setSmallIcon(R.drawable.ic_warning_black_24dp )
                .setAutoCancel(true)
                .build();

        notificationManager.notify(id,n);


    }








}
package com.upem.proxyloc;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.RemoteException;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.upem.proxyloc.services.Global;
import com.upem.proxyloc.services.TopicPublisher;
import com.upem.proxyloc.services.TopicSubscriber;
import com.upem.proxyloc.ui.gallery.GalleryFragment;
import com.upem.proxyloc.ui.home.HomeFragment;
import com.upem.proxyloc.ui.mode.ModeFragment;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;
    private int Ntfcount = 0;
    private static final int REQUEST_ENABLE_LOCATION = 457;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        getSupportFragmentManager().popBackStackImmediate();


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.nav_home:

                      /*  Fragment fragmentA = getSupportFragmentManager().findFragmentById(R.id.nav_home);
                        if (fragmentA == null) {
                            Log.e("messsaage", "not found " );                        } else {
                            //Log.e("messsaage", "onNavigationItemSelected: eexist " );
                        }*/
                        HomeFragment homeFragment = new HomeFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(getVisibleFragment().getId(), homeFragment, "HomeFragment")
                                .addToBackStack(null)
                                .commit();
                        drawer.closeDrawers();
                        break;

                    case R.id.nav_gallery:
                        GalleryFragment galleryFragment = new GalleryFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(getVisibleFragment().getId(), galleryFragment, "SettingsFragment")
                                .addToBackStack(null)
                                .commit();
                        Log.e("gall", "onNavigationItemSelected: " + getVisibleFragment().getId() + " :" + R.id.nav_host_fragment);
                        drawer.closeDrawers();
                        break;


                    case R.id.nav_manuel:
                        ModeFragment modeFragment = new ModeFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(getVisibleFragment().getId(), modeFragment, "SettingsFragment")
                                .addToBackStack(null)
                                .commit();
                        drawer.closeDrawers();
                        break;
                }
                return false;
            }
        });

        createNotificationChannel();


        Intent serviceIntent = new Intent(Home.this,TopicSubscriber.class);
        Intent serviceIntent1 = new Intent(Home.this,TopicPublisher.class);
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.O) {
            Home.this.startForegroundService(serviceIntent);
            Home.this.startForegroundService(serviceIntent1);
        } else {
            startService(serviceIntent);
            startService(serviceIntent1);
        }

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            startForegroundService(new Intent(this,  TopicSubscriber.class));
            startForegroundService(new Intent(this,  TopicPublisher.class));

        } else {
            this.startService(new Intent(this, TopicSubscriber.class));
            this.startService(new Intent(this, TopicPublisher.class));
        }
*/
//        this.startService(new Intent(this, TopicSubscriber.class));
       // this.startService(new Intent(this, TopicPublisher.class));
        //   startService(new Intent(getBaseContext(), TopicSubscriber.class));
        // startService(new Intent(getBaseContext(),  TopicPublisher.class));
        //********************************


        //**************************************


        HomeFragment homeFragment = new HomeFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, homeFragment, "HomeFragment")
                .addToBackStack(null)

                .commit();

         Global.mac = getDeviceIMEI();
        Log.e("id", "divice Id"+ getDeviceIMEI() );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }


                return;
            }


        }

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = "Proxy loc chan";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("lemubitA", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            Log.e(TAG, "getVisibleFragment: " + fragments.size());
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }


    private String getBluetoothMacAddress() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String bluetoothMacAddress = "";
        try {
            Field mServiceField = bluetoothAdapter.getClass().getDeclaredField("mService");
            mServiceField.setAccessible(true);

            Object btManagerService = mServiceField.get(bluetoothAdapter);

            if (btManagerService != null) {
                bluetoothMacAddress = (String) btManagerService.getClass().getMethod("getAddress").invoke(btManagerService);
            }
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {

        }
        Log.e("ff", "getBluetoothMacAddress: " + bluetoothMacAddress);
        return bluetoothMacAddress;
    }

    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        2);
            }else{
            deviceUniqueIdentifier = tm.getDeviceId();}
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }


}

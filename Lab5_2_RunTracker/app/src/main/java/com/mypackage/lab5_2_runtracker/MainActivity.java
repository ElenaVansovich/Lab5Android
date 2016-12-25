package com.mypackage.lab5_2_runtracker;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView started;
    private static TextView latitude;
    private static TextView longitude;
    private static TextView altitude;
    private TextView time;
    private ScheduledExecutorService service;
    private LocationManager mlocManager;
    private PendingIntent pendingIntent;

    private boolean isStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        started = (TextView) findViewById(R.id.textView2);
        latitude = (TextView) findViewById(R.id.textView4);
        longitude = (TextView) findViewById(R.id.textView6);
        altitude = (TextView) findViewById(R.id.textView8);
        time = (TextView) findViewById(R.id.textView10);

        mlocManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        /*LocationListener mlocListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if (isStarted) {
                    latitude.setText(String.valueOf(location.getLatitude()));
                    longitude.setText(String.valueOf(location.getLongitude()));
                    altitude.setText(String.valueOf(location.getAltitude()));
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };*/

        if (!mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Turn on GPS!!!", Toast.LENGTH_SHORT).show();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("GPS_ACTION");
        GpsReceiver receiver = new GpsReceiver();
        registerReceiver(receiver, filter);
        Intent intent = new Intent("GPS_ACTION");
        pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

    }

    public void startClick(View v) {
        if (isStarted) {
            Toast.makeText(this, "Already started!", Toast.LENGTH_SHORT).show();
            return;
        }
        isStarted = true;
        final Date date = new Date();

        try {
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, pendingIntent);

            if (!mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mlocManager.removeUpdates(pendingIntent);
                Toast.makeText(this, "Turn on GPS!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                started.setText(date.toString());
                service = Executors.newSingleThreadScheduledExecutor();
                service.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if (isStarted) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    time.setText((new Date().getTime() - date.getTime()) + " ms");
                                }
                            });
                        }
                    }
                }, 0L, 500L, TimeUnit.MILLISECONDS);
            } else {
                Toast.makeText(this, "Turn on GPS!!!", Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    public void stopClick(View v) {
        isStarted = false;
        if (service != null) {
            service.shutdown();
        }
        try {
            mlocManager.removeUpdates(pendingIntent);
            latitude.setText("");
            longitude.setText("");
            altitude.setText("");
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    static class GpsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Location location = (Location) bundle.get(LocationManager.KEY_LOCATION_CHANGED);
            if (location != null) {
                latitude.setText(String.valueOf(location.getLatitude()));
                longitude.setText(String.valueOf(location.getLongitude()));
                altitude.setText(String.valueOf(location.getAltitude()));
            }
        }
    }

}

package com.example.loginandregister.servicepackage;

import static com.example.loginandregister.servicepackage.wastewatchservice.CHANNEL_ID;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.loginandregister.R;
import com.example.loginandregister.admin.AdminHomeFragment;
import com.example.loginandregister.admin.AdminMainActivity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class AdminLocationService extends Service {
    private static final long INTERVAL = 2000; // 2 seconds
    private Handler handler;
    private Runnable periodicTask;
    private LocationRequest locationRequest;

    @Override
    public void onCreate() {
        super.onCreate();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        handler = new Handler();
        periodicTask = new Runnable() {
            @Override
            public void run() {
                // Log "hello" or perform any other background task here
                Log.d("AdminLocationService", "hello");
                setCollectorLocation(AdminLocationService.this);
                handler.postDelayed(this, INTERVAL);
            }
        };

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Intent notificationIntent = new Intent(this, AdminMainActivity.class);
//        PendingIntent pendingIntent = new PendingIntent.getActivity(this, )

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("WasteWatch Service")
                .setContentText("Background Task running.....")
                .setSmallIcon(R.drawable.logo)
                .build();

        startForeground(1, notification);

        handler.post(periodicTask);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(periodicTask);

    }

    public boolean isLocationPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

    }
    public void setCollectorLocation(Context context) {
        if (isLocationPermissionGranted(context)) {
            Log.d("setCollectorLocation: ", "location permission granted");
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(context)
                            .removeLocationUpdates(this);

                    if(locationResult != null && locationResult.getLocations().size() > 0)
                    {
                        int index = locationResult.getLocations().size() - 1;
                        Double collector_lat_value = locationResult.getLocations().get(index).getLatitude();
                        Double collector_long_value = locationResult.getLocations().get(index).getLongitude();
                        Log.d( "onLocationResult: Lat value:", collector_lat_value.toString());
                        Log.d( "onLocationResult: Long value:", collector_long_value.toString());
                        AdminHomeFragment.sendLocationToDB(collector_lat_value, collector_long_value);
                    }
                }
            }, Looper.getMainLooper());
        }
        else{
            Log.d("setCollectorLocation: ", "location permission not granted");
        }
    }



}
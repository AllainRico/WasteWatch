package com.example.loginandregister.servicepackage;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class wastewatchservice extends Application {
    public static final String CHANNEL_ID = "wastewatchServiceChannel";
    public static final String CHANNEL_NAME = "WASTEWATCH SERVICE CHANNEL";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager nManager = getSystemService(NotificationManager.class);
            nManager.createNotificationChannel(serviceChannel);
        }
    }

}

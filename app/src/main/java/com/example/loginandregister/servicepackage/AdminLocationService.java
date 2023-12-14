package com.example.loginandregister.servicepackage;

import static com.example.loginandregister.servicepackage.wastewatchservice.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.loginandregister.R;
import com.example.loginandregister.admin.AdminMainActivity;

public class AdminLocationService extends Service {
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


        return START_NOT_STICKY;
    }
}

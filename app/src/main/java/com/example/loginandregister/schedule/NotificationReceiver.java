package com.example.loginandregister.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.loginandregister.schedule.ScheduleNotificationManager;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");

        // Display the notification using the NotificationManager
        ScheduleNotificationManager notificationManager = new ScheduleNotificationManager(context);
        notificationManager.showNotification(title, message);
    }
}

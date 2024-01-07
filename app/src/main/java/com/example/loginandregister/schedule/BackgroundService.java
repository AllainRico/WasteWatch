package com.example.loginandregister.schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.example.loginandregister.user.UserMainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BackgroundService extends JobIntentService {
    private static final int JOB_ID = 1;
    public static final String ACTION_BACKGROUND_SERVICE = "com.example.loginandregister.schedule.ACTION_BACKGROUND_SERVICE";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    private String scheduledTime;
    private ScheduleNotificationManager notificationManager;
    private Handler notificationHandler;
    private Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        notificationHandler = new Handler(Looper.getMainLooper());
    }

    public static void enqueueWork(Context context, Intent work) {
        work.setAction(ACTION_BACKGROUND_SERVICE);
        enqueueWork(context, BackgroundService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (ACTION_BACKGROUND_SERVICE.equals(intent.getAction())) {
            ScheduleNotificationManager notificationManager = new ScheduleNotificationManager(this);
//            notificationManager.schedulePeriodicNotificationCheck();
        } else {
            checkScheduledNotifications();
        }
    }

    private void checkScheduledNotifications() {
        //delay and frequency of checking
        long initialDelayMillis = 1000; // Initial delay in milliseconds
        long checkFrequencyMillis = 30000; // Check every 30 seconds

        notificationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAndScheduleNotifications();
                notificationHandler.postDelayed(this, checkFrequencyMillis);
            }
        }, initialDelayMillis);
    }

    private void checkAndScheduleNotifications() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
        String today = dateFormat.format(new Date());

        Log.d("MyApp", "Today time: " + today);

        reference = database.getReference();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String time = snapshot.child("Barangay").child("Looc").child("Schedule").child(today).getValue(String.class);
                if (time != null && !time.isEmpty()) {
                    // if Schedule is available
                    scheduledTime = time;
                } else {
                    // No Schedule
                    scheduledTime = ""; // Default Value
                }

                try {
                    Date scheduledTimeDate = new SimpleDateFormat("hh:mm a", new Locale("en", "PH")).parse(scheduledTime);

                    // Calendar instance and scheduled time
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(scheduledTimeDate);

                    // Subtract # minutes for the reminder time
                    calendar.add(Calendar.MINUTE, -5);

                    // for the reminder time
                    Date reminderTime = calendar.getTime();

                    // for the current time
                    String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

                    // Format
                    String formattedReminderTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(reminderTime);

                    if (currentTime.equals(formattedReminderTime)) {
                        String reminderTitle = "Garbage Collection Reminder";
                        String reminderMessage = "Garbage collection starts at " + scheduledTimeDate + ". Don't forget!";
                        notificationManager.showNotification(reminderTitle, reminderMessage);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("MyApp", "Error parsing time: " + e.getMessage());
                }

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String currentTime = timeFormat.format(new Date());

                try {
                    Date scheduledTimeDate = new SimpleDateFormat("hh:mm a", new Locale("en", "PH")).parse(scheduledTime);
                    String formattedScheduledTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(scheduledTimeDate);


                    if (currentTime.equals(formattedScheduledTime)) {
                        String title = "Garbage Collection";
                        String message = "It's time for garbage collection at " + formattedScheduledTime;
                        notificationManager.showNotification(title, message);
                    } else {
                        Log.d("MyApp", "Scheduled time doesn't match current time. Scheduled: " + formattedScheduledTime + ", Current: " + currentTime);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("MyApp", "Error parsing time: " + e.getMessage());
                }
            }

            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(applicationContext, "Cancel Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
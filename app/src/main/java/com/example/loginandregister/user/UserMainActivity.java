package com.example.loginandregister.user;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Toast;

import com.example.loginandregister.R;
import com.example.loginandregister.schedule.ScheduleNotificationManager;
import com.example.loginandregister.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class UserMainActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    private String scheduledTime;
    private View decorView;
    ActivityMainBinding binding;
    private ScheduleNotificationManager notificationManager; // Declare notificationManager
    private Handler notificationHandler; // Declare notificationHandler

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        // Create a NotificationManager instance
//        ScheduleNotificationManager notificationManager = new ScheduleNotificationManager(this);
//
//        // Schedule a notification
//        String title = "Garbage Collection";
//        String message = "Make sure your garbage is ready to collect.";
//        notificationManager.showNotification(title, message);

        // This initialized the Firebase Database
        reference = FirebaseDatabase.getInstance().getReference();

        // This Creates a NotificationManager instance
        notificationManager = new ScheduleNotificationManager(this);

        createNotificationChannel();

        // This Creates a handler to periodically check the time and trigger notifications
        notificationHandler = new Handler(Looper.getMainLooper());

        // Checks for scheduled notifications
        checkScheduledNotifications();

        //Hides the Navigation Bar
        decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setupSystemBarsForAndroid12AndHigher(decorView);
        } else {
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int i) {
                    if(i == 0){
                        decorView.setSystemUiVisibility(hideSystemBars());
                    }
                }
            });
        }

        initializeLayout();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Waste Watch";
            String description = "Garbage Collection Schedule";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(ScheduleNotificationManager.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void checkScheduledNotifications() {
        //delay and frequency of checking
        long initialDelayMillis = 1000; // Initial delay in milliseconds
        long checkFrequencyMillis = 60000; // Check every 60 seconds

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
                    // Schedule a notification if time is available
                    scheduledTime = time;
                } else {
                    // No Schedule
                    scheduledTime = ""; // Default Value
                }

                try {
                    Date scheduledTimeDate = new SimpleDateFormat("hh:mm a", new Locale("en", "PH")).parse(scheduledTime);

                    // Create a calendar instance and set the scheduled time
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(scheduledTimeDate);

                    // Subtract 5 minutes for the reminder time
                    calendar.add(Calendar.MINUTE, -5);

                    // Get the reminder time
                    Date reminderTime = calendar.getTime();

                    // Get the current time
                    String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

                    // Format the reminder time
                    String formattedReminderTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(reminderTime);

                    if (currentTime.equals(formattedReminderTime)) {
                        // Trigger the reminder notification
                        String reminderTitle = "Garbage Collection Reminder";
                        String reminderMessage = "Garbage collection starts at " + formattedReminderTime + ". Don't forget!";
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
                    String formattedScheduledTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(scheduledTimeDate);

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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserMainActivity.this, "Cancel Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initializeLayout() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceUserFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceUserFragment(new HomeFragment());
            } else if (itemId == R.id.schedule) {
                replaceUserFragment(new ScheduleFragment());
            } else if (itemId == R.id.map) {
                replaceUserFragment(new MapFragment());
            } else if (itemId == R.id.profile) {
                replaceUserFragment(new ProfileFragment());
            }

            return true;
        });
    }
    public void replaceUserFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    public void setBottomNavigationSelectedItem(int itemId) {
        binding.bottomNavigationView.setSelectedItemId(itemId);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void setupSystemBarsForAndroid12AndHigher(View decorView) {
        decorView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                WindowInsetsController controller = v.getWindowInsetsController();
                if (controller != null) {
                    // Hide system bars using the new API
                    controller.hide(WindowInsets.Type.systemBars());
                    controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
                return insets;
            }
        });
    }

    private int hideSystemBars(){
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
}
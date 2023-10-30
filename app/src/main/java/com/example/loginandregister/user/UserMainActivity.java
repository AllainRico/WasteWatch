package com.example.loginandregister.user;

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
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import com.example.loginandregister.R;
import com.example.loginandregister.schedule.ScheduleNotificationManager;
import com.example.loginandregister.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;


public class UserMainActivity extends AppCompatActivity {
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


        // Create a NotificationManager instance
        notificationManager = new ScheduleNotificationManager(this);

        createNotificationChannel();

        // Create a handler to periodically check the time and trigger notifications
        notificationHandler = new Handler(Looper.getMainLooper());

        // Start checking for scheduled notifications
        checkScheduledNotifications();

        //Hide the Navigation Bar
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
        // You can adjust the delay and frequency of checking as needed
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
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = timeFormat.format(new Date());

        // Fetch the scheduled time from Firebase (Replace this with your Firebase logic)
        String scheduledTime = "07:30"; // Example time

        // Compare the current time with the scheduled time
        if (currentTime.equals(scheduledTime)) {
            // It's time for the collection, so trigger a notification
            String title = "Garbage Collection";
            String message = "It's time for garbage collection at " + scheduledTime;
            notificationManager.showNotification(title, message);
        }
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
    private void replaceUserFragment(Fragment fragment){
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
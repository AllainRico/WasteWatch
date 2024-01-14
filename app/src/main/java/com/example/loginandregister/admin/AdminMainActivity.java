package com.example.loginandregister.admin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.loginandregister.R;
import com.example.loginandregister.databinding.ActivityMainBinding;
import com.example.loginandregister.schedule.ScheduleNotificationManager;
import com.example.loginandregister.servicepackage.AdminLocationService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminMainActivity extends AppCompatActivity {
    private String TAG = "Admin Main Activity";
    private View decorView;
    ActivityMainBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 1;
    public static boolean isOnline = false;
    private ScheduleNotificationManager notificationManager;
    private Handler notificationHandler;
    DatabaseReference reference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static String barname;
    public static String globalusername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // Retrieve the isOnline value from the Intent
        isOnline = getIntent().getBooleanExtra("isOnline", false);
        AdminMapFragment.adminusername = globalusername;

        notificationManager = new ScheduleNotificationManager(this);
        createNotificationChannel();

        notificationHandler = new Handler(Looper.getMainLooper());
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
        startService();
        //startThread();
    }

    //All related to UI
    private void initializeLayout() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceAdminFragment(new AdminHomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceAdminFragment(new AdminHomeFragment());
            } else if (itemId == R.id.schedule) {
                replaceAdminFragment(new AdminScheduleFragment());
            } else if (itemId == R.id.map) {
                replaceAdminFragment(new AdminMapFragment());
            } else if (itemId == R.id.profile) {
                replaceAdminFragment(new AdminReportFragment());
            }
            return true;
        });
    }

    private void replaceAdminFragment(Fragment fragment){
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: "+ globalusername);
        setAdminOffline();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: " + globalusername);
        setAdminOffline();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: " + globalusername);
        setAdminOffline();
    }

    //service-related

    public void startService(){
        Intent serviceIntent = new Intent(this, AdminLocationService.class);

        startService(serviceIntent);
    }


    //maps-related methods here

    public void setAdminOffline()
    {
        reference = database.getReference().child("collectors").child(globalusername);
        reference.child("isOnline").setValue(false);
        reference.child("latitude").setValue(0.00);
        reference.child("longitude").setValue(0.00);
        Log.d(TAG, "setAdminOffline: "+ reference.toString());
    }



    public void startThread(){
        ExampleThread thread = new ExampleThread();
        thread.start();
    }



    class ExampleThread extends Thread{
        @Override
        public void run() {
            for (int i=0; i<10; i++)
            {
                Log.d(TAG, "startThread: " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    private boolean isGPSEnabled(){
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if(locationManager == null)
            {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
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
        long checkFrequencyMillis = 10000; // Check every 10 seconds

        notificationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAndNotifyifBinisFull();
                notificationHandler.postDelayed(this, checkFrequencyMillis);
            }
        }, initialDelayMillis);
    }


    private void checkAndNotifyifBinisFull(){
        int year = AdminReportFragment.getYear();
        int month = AdminReportFragment.getMonth();
        int date = AdminReportFragment.getDate();

        reference = database.getReference();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //count the bins inside the barangay
                //path == Barangay/Looc/Bins
                DataSnapshot binsSnapshot = snapshot.child("Barangay").child(barname).child("Bins");
                for (DataSnapshot binSnapshot : binsSnapshot.getChildren()){
                    String binName = binSnapshot.getKey();

                    if (binSnapshot.exists()) {
                        ///path = Barangay/Looc/Bins/bin1/2024/1/10/FillLevel
                        Double fillLevel = binSnapshot.child(String.valueOf(year)).child(String.valueOf(month)).child(String.valueOf(date)).child("FillLevel").getValue(Double.class);
                        if (fillLevel == null)
                        {
                            fillLevel = Double.valueOf(0);
                        }
                        if (fillLevel >= 90) {
                            // Send notification for the full bin
                            Log.d(TAG, "fill level: "+fillLevel);
                            sendNotification(binName);
                        }
                    } else {
                        // Handle the case where the binSnapshot doesn't exist
                        Log.e(TAG, "Bin " + binName + " does not exist!");
                    }

                }

                //else check count
                //loop while i != count
                //each i is checked if filllevel == 100
                //if true; send notification
                //else exit
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String binName) {
        // Implement your notification logic here
        // You can use NotificationCompat or any other notification mechanism
        // to show a notification to the user.
        // For simplicity, you can log the message for now.
        Log.d(TAG, "Notification: " + binName);

        String notificationTitle = "Bin Full Alert!";
        String notificationMessage = "Bin " + binName + " is at full capacity. It's time to empty the bin.";
        notificationManager.showNotification(notificationTitle, notificationMessage);
    }

    //end
}

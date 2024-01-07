package com.example.loginandregister.admin;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.loginandregister.R;
import com.example.loginandregister.databinding.ActivityMainBinding;
import com.example.loginandregister.servicepackage.AdminLocationService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminMainActivity extends AppCompatActivity {
    private String TAG = "Admin Main Activity";
    private View decorView;
    ActivityMainBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 1;
    public static boolean isOnline = false;
    DatabaseReference reference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static String globalusername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // Retrieve the isOnline value from the Intent
        isOnline = getIntent().getBooleanExtra("isOnline", false);
        AdminMapFragment.adminusername = globalusername;

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
    //all related to location requisition

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


    //end
}

package com.example.loginandregister.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.example.loginandregister.R;
import com.example.loginandregister.databinding.ActivityMainBinding;


public class AdminMainActivity extends AppCompatActivity {
    private View decorView;
    ActivityMainBinding binding;
    boolean isOnline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        // Retrieve the isOnline value from the Intent
        isOnline = getIntent().getBooleanExtra("isOnline", false);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if(i == 0){
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Fragment shown first at start
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

        if (isOnline) {
            // Access GPS here
            // You can use location services because the user is online
        } else {
            // User is not online, handle this case accordingly
            // You might want to show a message or restrict access to GPS features
        }
    }
    public void setOnlineStatus(boolean onlineStatus) {
        isOnline = onlineStatus;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }

    private int hideSystemBars(){
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
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
}
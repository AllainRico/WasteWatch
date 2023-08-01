package com.example.loginandregister;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.example.loginandregister.databinding.ActivityMainBinding;


public class AdminMainActivity extends AppCompatActivity {
    //Hide Navigation bar variable
    private View decorView;
    //Fragment
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //Hide the Navigation Bar
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

        //Fragment shown first at startZ
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new AdminHomeFragment());
            } else if (itemId == R.id.schedule) {
                replaceFragment(new AdminScheduleFragment());
            } else if (itemId == R.id.map) {
                replaceFragment(new AdminMapFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new AdminProfileFragment());
            }

            return true;
        });

    }

    //Hide the Navigation Bar Method
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

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frameLayout, fragment);

        fragmentTransaction.commit();

    }

    public void setBottomNavigationSelectedItem(int itemId) {
        binding.bottomNavigationView.setSelectedItemId(itemId);
    }
}
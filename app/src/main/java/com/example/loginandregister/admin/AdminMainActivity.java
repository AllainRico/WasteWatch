    package com.example.loginandregister.admin;

    import androidx.annotation.NonNull;
    import androidx.annotation.RequiresApi;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentManager;
    import androidx.fragment.app.FragmentTransaction;
    import android.Manifest;
    import android.app.AlertDialog;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.location.Location;
    import android.os.Build;
    import android.os.Bundle;
    import android.os.Handler;
    import android.util.Log;
    import android.view.View;
    import android.view.WindowInsets;
    import android.view.WindowInsetsController;
    import android.widget.Toast;

    import com.example.loginandregister.Login;
    import com.google.android.gms.location.FusedLocationProviderClient;
    import com.google.android.gms.location.LocationServices;

    import com.example.loginandregister.R;
    import com.example.loginandregister.databinding.ActivityMainBinding;
    import com.google.android.gms.tasks.OnSuccessListener;


    public class AdminMainActivity extends AppCompatActivity {
        private View decorView;
        ActivityMainBinding binding;
        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
        private static final int PERMISSION_REQUEST_CODE = 123;
        boolean isOnline = false;
        private final Handler locationHandler = new Handler();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin_main);

            // Retrieve the isOnline value from the Intent
            isOnline = getIntent().getBooleanExtra("isOnline", false);

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

//            if (isOnline) {
//                // Access GPS here
//                initializeLocationService();
//            } else {
//                // User is not online, handle this case accordingly
//                showLocationPermissionDeniedDialog();
//            }

            if (isOnline) {
                // Access GPS here
                requestLocationAndStoragePermissions();
                locationHandler.postDelayed(fetchLocationRunnable, 5000);
            } else {
                // User is not online, handle this case accordingly
                showPermissionsDialog();
            }
        }
        private void requestLocationAndStoragePermissions() {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }

        private void showPermissionsDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("WasteWatch requires location and storage access to proceed.")
                    .setPositiveButton("Grant Permissions", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Request both location and storage permissions
                            requestLocationAndStoragePermissions();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // if the user denies permissions
                            Intent intent = new Intent(AdminMainActivity.this, Login.class);
                            startActivity(intent);
                            finish();
                            ((AdminMainActivity) AdminMainActivity.this).setOnlineStatus(false);
                            Toast.makeText(AdminMainActivity.this, "Permissions denied", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create()
                    .show();
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == PERMISSION_REQUEST_CODE) {
                if (grantResults.length > 0) {
                    // Check the grantResults array for both permissions
                    boolean locationPermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storagePermissionGranted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationPermissionGranted && storagePermissionGranted) {
                        // Both permissions are granted
                        // Continue with your location and storage-related tasks
                        initializeLocationService();
                        // Any other initialization or tasks that require storage access

                    } else {
                        // Handle if the user denies either or both permissions
                        Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        private boolean isLocationPermissionGranted() {
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }

//        private void requestLocationPermission() {
//            Log.d("LocationPermission", "Requesting location permission");
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    LOCATION_PERMISSION_REQUEST_CODE);
//        }

//        @Override
//        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//            if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Location permission granted, initialize location service
//                    initializeLocationService();
//                } else {
//                    showLocationPermissionDeniedDialog();
//                }
//            }
//        }

        private void initializeLocationService() {
            if (isLocationPermissionGranted()) {
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        double adminLatitude = location.getLatitude();
                                        double adminLongitude = location.getLongitude();

                                        LocationData.getInstance().setAdminLatitude(adminLatitude);
                                        LocationData.getInstance().setAdminLongitude(adminLongitude);

                                        Log.d("AdminLocation", "Latitude: " + adminLatitude);
                                        Log.d("AdminLocation", "Longitude: " + adminLongitude);
                                    } else {
                                        Toast.makeText(AdminMainActivity.this, "Location is not available", Toast.LENGTH_SHORT).show();

                                        double defaultLatitude = 10.305627;
                                        double defaultLongitude = 123.946517;

                                        LocationData.getInstance().setAdminLatitude(defaultLatitude);
                                        LocationData.getInstance().setAdminLongitude(defaultLongitude);
                                    }
                                }
                            });
                } else {
                    //showLocationPermissionDeniedDialog();
                }
            } else {
                //requestLocationPermission();
            }
        }

        private final Runnable fetchLocationRunnable = new Runnable() {
            @Override
            public void run() {
                initializeLocationService(); // Retrieve location

                // Schedule the next request for location update after 2 seconds
                locationHandler.postDelayed(this, 2000); // 2000 milliseconds = 2 seconds
            }
        };


//        private void showLocationPermissionDeniedDialog() {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage("WasteWatch requires location access to proceed.")
//                    .setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            requestLocationPermission();
//                        }
//                    })
//                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            Toast.makeText(AdminMainActivity.this, "Location is needed", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .create()
//                    .show();
//        }

        public void setOnlineStatus(boolean onlineStatus) {
            isOnline = onlineStatus;
        }

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
            locationHandler.removeCallbacks(fetchLocationRunnable);
        }

    }